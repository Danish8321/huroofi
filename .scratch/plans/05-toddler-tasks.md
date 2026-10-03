# 05 Toddler mode — executable task list (slices 2–6)

Written 2026-10-03 so a session with no prior conversation (e.g. a cloud session) can build the rest of plan 05.
Spec: `05-toddler.md` (approved, decisions 1–15). This file only breaks that spec into tasks; where the two disagree, `05-toddler.md` wins and the conflict is reported, not guessed.

## Read first (in this order)

1. `CLAUDE.md` (project rules), `HANDOFF.md` §2–§7, `CONTEXT.md` (glossary).
2. `.scratch/plans/05-toddler.md` — the decisions are binding.
3. Prototypes: `design-reference/screens/ToddlerHome.html`, `ToddlerCards.html`, `ToddlerFind.html`, `ToddlerPaint.html`, `ParentGate.html`. Rebuild natively in Compose; never a WebView.

## Where to work

- Branch `feat/steps-1-2-foundation` (already on origin). It holds steps 1+2 and slice 1 (parent gate). Work on it directly; one commit per task; push after every slice.
- Package root: `app/src/main/java/com/huroofi/app/`. Tests: `app/src/test/java/com/huroofi/app/` (JVM only).
- Commit messages: conventional (`feat(toddler): ...`), ending with the attribution lines your harness gives you.

## Invariants (every task)

- No new dependency. Everything here is buildable with what `app/build.gradle.kts` already has (Compose BOM, foundation, material3, navigation-compose, activity-compose, coroutines, junit, coroutines-test). `HorizontalPager` is in `compose.foundation`.
- No Robolectric, no instrumented tests (decision 2 in `00-index.md`). UI is checked by `@Preview` + `check.sh`; logic is pulled into pure classes and unit-tested. Report visual behaviour as **unverified**.
- Never silence lint or a test (no `@Suppress`, no baseline, no `lint.xml`; `check.sh` fails on baselines).
- Letters, words, stage colours come from `ContentRepository` (`LocalAppContainer.current.content`). Never hard-code a letter list. Decorative art named in a prototype (lion/duck/apple on Home tiles, ب on the Paint tile) may reference `R.drawable.pic_*` / a literal glyph directly.
- Arabic text: `ArabicText` (Naskh Bold, RTL run). English: `HuroofiText` styles (Baloo). Nothing under 16 sp.
- Toddler rules: touch targets ≥ 64 dp (only `ParentLock` exempt, ≥ 48 dp), ≤ 3 choices per screen, no red for feedback/status, sound first, no fail state.
- **Spec objects**: each toddler screen puts its sizes and colours in a public `object XxxSpec` (e.g. `ToddlerHomeSpec`) instead of inline literals, so task 6.2 can sweep them. Prototype colours that are not HANDOFF tokens live there (like `ParentGateScreen` keeps its private gate colours).
- Delete code you replace (the activity stubs from task 2.6 go away in slices 3–5).

## Verification gates

From repo root, using `sh` (scripts are not executable in git):

- `sh .claude/scripts/check.sh` — assembleDebug + lintDebug + compileReleaseKotlin
- `sh .claude/scripts/test-fast.sh` — all JVM unit tests
- `sh .claude/scripts/contract.sh` — content/audio contract

"Done" for a task = its own stated check passes **and** `check.sh` + `test-fast.sh` pass. Paste the last line of each gate into the task's checkbox note. `test-full.sh` / `e2e.sh` do not exist (no emulator yet); say so, don't claim them.

---

## Task 0 — Environment (cloud only; skip if the gates already pass)

Files: `gradlew` (mode only).

1. JDK 17 on PATH (`java -version`).
2. Android SDK: if none, install command-line tools into `$HOME/Android/Sdk/cmdline-tools/latest`, then
   `sdkmanager --licenses` and `sdkmanager "platforms;android-36" "build-tools;36.0.0" "platform-tools"`. `_env.sh` finds `$HOME/Android/Sdk` or `$ANDROID_HOME`.
3. `gradlew` is committed as 100644, so `./gradlew` fails on Linux. Fix in git: `git update-index --chmod=+x gradlew`, commit `build: mark gradlew executable`.
4. `local.properties` is git-ignored and Windows-specific; do not commit one. `ANDROID_HOME` is enough.

Check: all three gates pass on the untouched branch **before** any code change. If they don't, stop and report; don't start slice 2 on a red baseline.

---

## Slice 2 — Toddler home

Spec: plan 05 slice 2, decisions 6, 12, 14. Prototype `ToddlerHome.html`.

### 2.1 Start route from stored mode (pure)
Files: `HuroofiApp.kt`, new `test/.../StartRouteTest.kt`.
- Add to `Routes`: `ToddlerHome = "toddler_home"`, `ToddlerCards = "toddler_cards"`, `ToddlerFind = "toddler_find"`, `ToddlerPaint = "toddler_paint"`.
- Add `fun startRoute(mode: AgeMode): String` — TODDLER → `ToddlerHome`; PRESCHOOL, READER → `Placeholder` (Main is plan 07).
Check: test asserts all three modes.

### 2.2 App starts on the mode's home
Files: `HuroofiApp.kt`, `debug/.../DebugDestinations.kt`, `release/.../DebugDestinations.kt`.
- Decision 12: mode is read **once per app start**, no live switch. In `HuroofiApp`: `var mode by rememberSaveable { mutableStateOf<AgeMode?>(null) }`, `LaunchedEffect(Unit) { if (mode == null) mode = container.progress.mode.first() }`. While null, draw only a `sky` background. Then `NavHost(startDestination = startRoute(mode))`.
- Remove `DEBUG_START_ROUTE` from both source sets. Add `val DEBUG_GALLERY_ROUTE: String?` — `GALLERY_ROUTE` in debug, `null` in release.
- `ParentZoneStub` shows a `PrimaryButton("Component gallery (debug)")` only when `DEBUG_GALLERY_ROUTE != null` (the gallery stays reachable in debug).
Check: `check.sh` (compileReleaseKotlin proves the release source set).

### 2.3 Toddler rule helpers (pure)
Files: new `toddler/ToddlerRules.kt`, new `test/.../toddler/ToddlerRulesTest.kt`.
- `const val MAX_TODDLER_CHOICES = 3`.
- `fun isRed(c: Color): Boolean` — HSV hue < 20° or > 340°, saturation ≥ 0.5, value ≥ 0.35 (compute from `c.red/green/blue`; no Android classes).
Check: tests — `#FF7A59` (coral crayon) is red; `#EC4F8C`, `#FF6FA5`, `#F59E0B`, `#3BAA5C`, `#FFFFFF`, `HuroofiTokens.*` are not.

### 2.4 Home icon and toddler Home button
Files: `ui/components/Icons.kt`, new `ui/components/ToddlerHomeButton.kt`, test `ui/components/ToddlerHomeButtonTest.kt`.
- `HomeIcon` (Canvas, from the prototype SVG: roof `M3 11 l9-7 9 7`, body `M5 10 v10 h14 V10`, stroke 2.4/24 of size, round caps).
- `ToddlerHomeButton(onClick)`: 68 dp white circle, 5 dp solid shadow `#CFE2F7` below (same sink-on-press idiom as `PrimaryButton`), `HomeIcon` 34 dp navy, contentDescription "Home". Size in `object ToddlerHomeButtonSpec { val Size = 68.dp }`.
- Add it to `ComponentSamples` (debug gallery).
Check: test `Size >= HuroofiDimens.ToddlerMinTouch`.

### 2.5 Toddler activity model (pure)
Files: new `toddler/ToddlerActivity.kt`, test `toddler/ToddlerActivityTest.kt`.
- `enum class ToddlerActivity(val route, val arabicLabel, val englishLabel, val contentDescription, val tile: TileColors)`:
  - CARDS → `Routes.ToddlerCards`, "اسمع", "Look & listen", "Look and listen: picture cards", bg `#FFEDC4` border `#F59E0B` shadow `#C77F00` text `#6B4A00`.
  - FIND → `Routes.ToddlerFind`, "ابحث", "Find it", "Find it: where is the picture?", bg `#DAEAFF` border `#2F80ED` shadow `#1F5FB8` text `#0B4FB0`.
  - PAINT → `Routes.ToddlerPaint`, "ارسم", "Paint", "Paint the letter", bg `#FFDDEA` border `#EC4F8C` shadow `#B83A6B` text `#8A1E4C`.
- `object ToddlerHomeSpec`: `TileHeight = 196.dp`, `TileCorner = 40.dp`, `TileBorder = 5.dp`, `TileShadow = 8.dp`, `BubbleHeight = 68.dp`, `MaxContentWidth = 480.dp`.
Check: tests — entries ≤ `MAX_TODDLER_CHOICES`; routes distinct; no tile colour `isRed`; `TileHeight` and `BubbleHeight` ≥ 64 dp.

### 2.6 Toddler Home screen (stateless UI) + activity stubs
Files: new `toddler/ToddlerHomeScreen.kt`, new `toddler/ToddlerActivityStub.kt`.
- `ToddlerHomeScreen(onReplayPrompt, onOpenActivity: (ToddlerActivity) -> Unit, onRequestParentZone)`. Layout per prototype: `sky` bg, padding 18/20/30, gap 20; content column `widthIn(max = 480.dp)` centred (decision 14).
  - Header 112 dp: `pic_lion` 96 dp, white bubble (radius 30/30/30/6, height 68) with "ماذا نلعب؟" 34 sp and a pulsing `SoundIcon` (primary-blue, scale 0.85↔1.1, 800 ms, infinite). Whole bubble is the replay target (contentDescription "Leo says: What shall we play? Tap to hear it again"). `ParentLock` top-right ("Grown-ups: press and hold to open settings").
  - Three tiles from `ToddlerActivity.entries`; each `weight(1f).heightIn(max = TileHeight)`; solid shadow below like `PrimaryButton`; Arabic label 40 sp + English 16 sp bold in a 92 dp column on the right.
  - Tile art: CARDS = two white 112×140 cards (radius 22, border 4 `#F59E0B`) rotated −8°/+7°, offset ±14 dp, `pic_lion` / `pic_duck` 92 dp. FIND = `pic_apple` 132 dp + magnifier Canvas 120 dp (handle `#26324A` 16 + `#8D5A2B` 8; lens r=38 white 45 % fill, `#26324A` 10 stroke, `#FFC93C` 5 stroke), overlapping −46 dp x, +30 dp y. PAINT = "ب" 110 sp `#EC4F8C` + crayon Canvas 104 dp rotated 35° (body `#FFC93C`, collar `#B0BEC5`, tip `#EC4F8C`, outline `#26324A` 4).
  - `@Preview(widthDp = 390, heightDp = 844)`.
- `ToddlerActivityStub(activity, onHome, onRequestParentZone)`: header row (`ToddlerHomeButton` left, `ParentLock` right), centred Arabic label 40 sp + "Coming soon" body. Temporary; deleted by 3.4 / 4.6 / 5.6.
Check: `check.sh`; preview renders in Android Studio (unverified in cloud).

### 2.7 Prompt playback helper (pure, reused by every toddler screen)
Files: new `toddler/PromptPlayer.kt`, test `toddler/PromptPlayerTest.kt`, test helper `test/.../audio/FakeSoundPlayer.kt`.
- `class PromptPlayer(private val sound: SoundPlayer, private val scope: CoroutineScope)` with `fun play(vararg clips: Clip)` — cancels the running job, `sound.stop()`, then plays clips in order in a new job — and `fun stop()`.
- `FakeSoundPlayer`: records `play`/`stop` calls; `play` suspends until the test calls `finish()` or the job is cancelled (copy the idea from `SoundPlayerTest.FakeBackend`).
Check: tests — `play(a, b)` plays a then b; `play(c)` while a is running cancels a and b never starts; `stop()` stops and nothing further plays.

### 2.8 Wire Toddler Home into navigation
Files: `HuroofiApp.kt`, new `toddler/ToddlerHomeRoute.kt`.
- `ToddlerHomeRoute`: `PromptPlayer` over `LocalAppContainer.current.sound` and `rememberCoroutineScope()`. `LaunchedEffect(Unit) { prompt.play(Clips.whatShallWePlay) }` — runs every time Home appears (start, Home button, Back) per decision 12. Bubble tap → same. Opening an activity or the lock → `prompt.stop()` then navigate. `BackHandler {}` swallows Back (decision 6: no sound, no animation).
- NavHost: `composable(Routes.ToddlerHome)`; one `composable(activity.route)` per `ToddlerActivity` showing the stub with `onHome = { nav.popBackStack(Routes.ToddlerHome, inclusive = false) }`.
- Lock from any toddler screen: `nav.navigate(Routes.Gate) { popUpTo(Routes.ToddlerHome) }` so Back on the gate lands on Toddler Home (decision 6).
Check: `check.sh`, `test-fast.sh`. Manual (if a device exists): fresh install opens Toddler Home; tiles open stubs; Home and Back return; Back on Home does nothing; lock → gate → Back → Home.

Slice 2 done: update `05-toddler.md` status line, push.

---

## Slice 3 — Look & listen

Spec: plan 05 slice 3, decisions 8, 9. Prototype `ToddlerCards.html` (ignore its dots; decision 9 removes them).

### 3.0 Letter picture lookup
Files: new `ui/components/LetterImages.kt`.
- Nothing yet maps `Letter.pictureResourceName()` to a drawable id (`ContentContractTest` already proves every file exists). Add `@Composable fun letterPicture(l: Letter): Painter` that resolves the id once per name with `resources.getIdentifier(name, "drawable", packageName)` and fails loudly (`error(...)`) on 0.
- If lint reports `DiscouragedApi` as an error, do **not** suppress it: stop and ask (alternative is a generated `name → R.drawable` table, which is a build change).
Check: `check.sh` (lint) passes.

### 3.1 Card deck (pure)
Files: new `toddler/cards/CardDeck.kt`, test.
- `CardDeck(size: Int)`: `fun indexOf(page: Int): Int` = floorMod(page, size); `START_PAGE` = a large multiple of size (e.g. `size * 1000`) so the pager wraps both ways; `PAGE_COUNT = size * 2000`.
Check: tests — page START → index 0; START−1 → size−1; START+size → 0; with size 28 from the real content file (load via the same reader `ContentRepositoryTest` uses).

### 3.2 Card audio (pure)
Files: new `toddler/cards/CardAudio.kt`, test.
- Uses `PromptPlayer`. `onArrive(letter)` → `play(Clips.letter(l), Clips.word(l))`. `onPictureTap(letter)` → `play(Clips.word(l))`. New arrival cuts any clip (decision 8). Expose `speaking: StateFlow<Boolean>` (true while a job runs) for the talking bars.
Check: tests with `FakeSoundPlayer` — arrival plays letter then word; tap plays only word; arrival during word cancels it.

### 3.3 Card UI
Files: new `toddler/cards/LookListenScreen.kt` (+ `object CardsSpec`).
- Header: `ToddlerHomeButton` + `ParentLock`. `HorizontalPager(pageCount = PAGE_COUNT, initialPage = START_PAGE)`; `LaunchedEffect(pagerState.settledPage)` → `onArrive`.
- Card per prototype using the letter's stage colours (`Stage.colors()`: border = ring, pastel, accent = dark): ring 8 dp padding, radius 40, shadow 8 `#CFE2F7`; inner white radius 34; letter box min 150×136 pastel radius 36, letter 110 sp; talking bars (3 × 6×24, ring colour, staggered 0/200/400 ms) top-left while `speaking`; picture button 226 dp pastel circle, image 196 dp (via 3.0), contentDescription "<meaning_en>, <word_ar>"; tap = wiggle (rotate 0→−8→8→0 + scale 1.08, 700 ms) + `onPictureTap`; word 88 sp with `wordFirst` in accent colour then `wordRest`.
- Bottom row only prev (white, shadow `#CFE2F7`) and next (primary, shadow primaryShadow), both 96 dp, chevron 44 dp; they `animateScrollToPage(±1)`.
Check: `check.sh`; tests in `CardsSpecTest`: buttons ≥ 64 dp; choices (prev, next, picture) ≤ 3.

### 3.4 Wire and delete stub
Files: `HuroofiApp.kt`. Route `ToddlerCards` → `LookListenScreen`. Remove CARDS use of the stub.
Check: gates. Push.

---

## Slice 4 — Where's the…?

Spec: plan 05 slice 4, decisions 5, 13, 14. Prototype `ToddlerFind.html` (phone only; tablet is plan 08).

### 4.1 Random pick helper (pure)
Files: new `toddler/Picks.kt`, test.
- `fun <T> pickExcept(items: List<T>, except: T?, random: Random): T` — never returns `except` when `items.size > 1`.
Check: 1 000 seeded draws never equal `except`; single-item list returns that item.

### 4.2 Find round and set (pure)
Files: new `toddler/find/FindGame.kt`, test.
- `FindRound(target: Letter, options: List<Letter>)` (2 options, shuffled), `fun tap(l): TapResult` (`NUDGE` / `CORRECT` / `IGNORED` once solved).
- `FindSet` state: `stars` (0..3), `round`, `previousTarget`. `newRound(random)`: target = `pickExcept(all 28, previousTarget)`, distractor = random other letter. `onCorrect()` → stars+1. Wrong never removes a star. `next()` after the 3rd star starts a new set (stars = 0). Endless.
Check: tests — 500 seeded rounds: target ≠ previous, distractor ≠ target, exactly 2 options; wrong tap keeps stars; 3 correct → `setComplete`; `next()` resets stars.

### 4.3 Prompt phrases (pure) — **needs native-speaker review**
Files: new `toddler/ToddlerPhrases.kt`, test.
- Data has no definite forms, so phrases are generated: `whereIsAr(l) = "أين ال" + l.wordAr + "؟"`, `whereIsEn(l) = "Where's the " + l.meaningEn + "?"`, `colourAr(l) = "لوّن ال" + l.nameAr` (task 5.4).
- Write all 28 × 2 Arabic phrases into `docs/review/toddler-phrases.md` (generated by a test-only printer or by hand from the function) and list it in the slice's push note as **pending native-speaker review**. Do not change `letters.json`.
- Note: `لوّن` carries a shadda. Plain-form rules (CONTEXT.md) only strip short vowels; keep it as the prototype has it and flag it in the review file.
Check: tests — duck → "أين البطة؟", lion → "أين الأسد؟", baa → "لوّن الباء".

### 4.4 Find sounds (pure)
Files: new `toddler/find/FindAudio.kt`, test.
- On open and on sun button: `play(Clips.whereIs(target))`.
- Wrong tap: first wrong of the round → `play(boing)`, wait 300 ms, `play(whereIs)`; later wrong taps → `boing` only (decision 13).
- Right tap: `praise(n)` with n = `pickExcept(1..3, lastPraise)`; if that made the 3rd star, then `cheer`.
Check: tests with `FakeSoundPlayer` + `runTest` virtual time: exact call sequences for each case, including the 300 ms gap.

### 4.5 Find UI
Files: new `toddler/find/FindScreen.kt` (+ `object FindSpec`).
- Header: home, 3 stars (30 dp, filled `sun` when earned else white, outline `#26324A`), lock.
- Prompt row: sun round button 84 dp (shadow sunShadow) with `SoundIcon`; white bubble radius 26/26/26/8 with Arabic 42 sp + English 18 sp muted, right-aligned.
- Two tiles, 230 dp tall, radius 40, border 6, shadow 8: idle white/white/`#CFE2F7`; solved target `#DFF5D5`/`#3BAA5C`/`#9FD58A`. Picture 140 dp (`letterPicture`, 3.0), word 50 sp with first letter in the stage accent colour.
- Animations: wrong tile nudge (x 0→−8→8→0 dp, 400 ms); after a wrong tap the target wiggles (±9°, scale 1.1, 800 ms, 300 ms delay); solved target "yay" (scale 1→1.18→1.08, 600 ms) and three pop stars (`#FFC93C` 64 dp, `#FF6FA5` 48 dp, `#3B8CF0` 54 dp). Never red, never an X.
- After solved: green 96 dp Next (success/successShadow) pops in at the bottom centre.
Check: `check.sh`; `FindSpecTest`: tiles and buttons ≥ 64 dp; options = 2; no colour `isRed`.

### 4.6 Wire and delete stub
Files: `HuroofiApp.kt`. Route `ToddlerFind` → `FindScreen`. Check: gates. Push.

---

## Slice 5 — Finger paint

Spec: plan 05 slice 5, decisions 2, 3, 4, 10, 11. Prototype `ToddlerPaint.html` (its 60-event threshold is replaced by decision 11).

### 5.1 Paint model (pure)
Files: new `toddler/paint/PaintModel.kt`, test.
- Crayons: `enum class Crayon(color)` CORAL `#FF7A59` (label "Orange paint"), BLUE `#3B8CF0`, GREEN `#3BAA5C`, PURPLE `#8A6CE8`. Default CORAL.
- Strokes: list of (crayon, points in dp). `start(p)`, `moveTo(p)` adds `distance(last, p)` to `totalDp`, `end()`. `starEarned = totalDp >= 400` (counted anywhere on the canvas). `wipe()` clears strokes, distance and star. `select(crayon)`.
Check: tests — 399 dp no star, 400 dp star; distance not event count (one 400 dp move earns it, 1 000 zero-length moves don't); wipe resets.

### 5.2 Paint letter choice (pure)
Files: same file or `PaintSession`, test.
- `next(random)`: letter = `pickExcept(all 28, current)`, model wiped. Painting allowed after the star (decision 10).
Check: tests — 200 seeded `next()` never repeat the previous letter; model empty after `next()`.

### 5.3 Dashed glyph outline
Files: new `toddler/paint/LetterOutline.kt`.
- Decision 4: no outline data. Draw the letter with Noto Naskh Bold via `rememberTextMeasurer()` + `drawText(..., drawStyle = Fill)` in `#FFF0F5`, then again with `drawStyle = Stroke(width = 4.dp, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp, 10.dp)))` in `#F5A9C6`. Size so the glyph fills ~80 % of the canvas height, centred.
Check: `check.sh`; preview for أ, ب, ي, ه.

### 5.4 Paint sounds
Files: `toddler/paint/PaintAudio.kt`, test.
- On open and on each `next()`: `play(Clips.colour(letter))`. Bubble tap replays. Star earned → `praise(n)` (same `pickExcept` rule).
Check: `FakeSoundPlayer` sequences.

### 5.5 Paint UI
Files: new `toddler/paint/PaintScreen.kt` (+ `object PaintSpec`).
- Header: home, bubble (64 dp tall, radius 30, `colourAr` 34 sp, pink `SoundIcon` 26 dp), lock.
- Canvas card: white, radius 40, border 6 `#EC4F8C`, shadow 8 `#B83A6B`, aspect 320:380, clipped. Outline (5.3) under strokes. Strokes 30 dp, round cap/join, alpha 0.9. `pointerInput` with `awaitEachGesture` → model start/move/end; convert px → dp at this boundary.
- Star 86 dp (sun, outline `#26324A`) pops top-right when earned; with it a green 96 dp Next at the canvas card's bottom-right (decision 10).
- Bottom row: 4 crayons 64 dp (selected ring 6 dp navy, else 6 dp white; shadow `#13294B` 20 %) + wipe 64 dp (white, border 3 `#A9CBF2`, wipe icon from prototype).
Check: `check.sh`; `PaintSpecTest`: all controls ≥ 64 dp; colours either not `isRed` or in the crayon allow-list (decision 2).

### 5.6 Wire and delete stub
Files: `HuroofiApp.kt`; delete `ToddlerActivityStub.kt` (now unused). Check: gates. Push.

---

## Slice 6 — Child-safety plumbing

Spec: plan 05 slice 6, decisions 6, 7, 15.

### 6.1 Back rules (pure + wiring)
Files: new `toddler/ToddlerBack.kt`, test; `HuroofiApp.kt`.
- `fun toddlerBack(route: String): BackAction` — `ToddlerHome` → `Swallow`; any toddler activity route or `Gate` (when start is Toddler Home) → `PopToToddlerHome`. Apply with one `BackHandler` per route in the NavHost (remove the ad-hoc ones from 2.8 so there is one place).
- Exit only via the gate's "Close Huroofi": assert no other `finish`/`finishAffinity` call exists (`grep -rn "finish" app/src/main` shows only `MainActivity`'s `onCloseApp` lambda).
Check: unit test for every route; grep result in the commit note.

### 6.2 Toddler rule sweep
Files: new `test/.../toddler/ToddlerRulesSweepTest.kt`.
- Enumerate every `*Spec` object (Home, HomeButton, Cards, Find, Paint) explicitly in the test. Assert: every touch size ≥ 64 dp; `ParentLockSize.Hit` ≥ 48 dp (named exception); no colour `isRed` except the 4 `Crayon` colours; choice count ≤ `MAX_TODDLER_CHOICES`.
Check: `test-fast.sh`.

### 6.3 Close out
- Mark `05-toddler.md` "built", update `00-index.md` row 05, note: visual behaviour unverified (no device), phrases pending native review (4.3), no daily limit until plan 06 (decision 15).
- Run all three gates; paste the last lines. Push. Do **not** merge to `main` or open a PR unless the user asks.

## Stop and ask (don't guess) if

- A gate fails three times on the same cause (then use `diagnosing-bugs`).
- A task needs a new dependency, a lint suppression, or a change to `letters.json`.
- The prototype and a plan decision disagree in a way this file doesn't settle.
