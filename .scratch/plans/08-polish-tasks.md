# 08 Tablet, immersive, accessibility, Families — executable task list

Written 2026-10-04 so a session with no prior conversation can build plan 08.
Spec: `08-polish.md` (approved, decisions 1–14). This file only breaks that spec into tasks; where the two disagree, `08-polish.md` wins and the conflict is reported, not guessed.

## Read first (in this order)

1. `CLAUDE.md`, `HANDOFF.md` §1, §3, §8 (rule 4), §9 step 6, `CONTEXT.md`.
2. `.scratch/plans/08-polish.md` — decisions are binding.
3. Prototype `design-reference/screens/ToddlerFindTablet.html` (the only tablet design) and `ToddlerFind.html`.
4. Code this builds on: `MainActivity.kt` (orientation via `R.bool.portrait_only`, already in place), `HuroofiApp.kt` (routes), `toddler/ToddlerHomeScreen.kt` (`ToddlerScaffold`, `ToddlerHomeSpec.MaxContentWidth`), `toddler/find/FindScreen.kt` (`FindSpec`, `FindTile`), `parent/ParentZoneScreen.kt` (`SettingRow`), `ui/theme/Colors.kt`, tests `ui/theme/TokensTest.kt`, `parent/ZoneSpecTest.kt`, `learn/LearnSpecTest.kt`, `toddler/ToddlerRulesSweepTest.kt`.

## Where to work

- Branch `feat/polish` from `main`. One commit per task; push after every slice. Never commit to `main`.
- Conventional commit messages ending with the harness attribution lines.

## Invariants (every task)

- No new dependency in `build.gradle.kts` / `libs.versions.toml`. `androidx.core` (`WindowCompat`, `WindowInsetsControllerCompat`) is already on the classpath through `activity-compose`; using it is allowed.
- No Robolectric, no instrumented tests. Logic in pure functions with JVM tests; UI checked by `check.sh` and the emulator pass.
- Never silence lint or a test. Delete what you replace.
- Child screens: touch ≥ 64 dp, no red, sound first. Parent zone: touch ≥ 48 dp.
- No `letters.json`, DataStore or colour change without asking the user first (decision 9).
- Never import `androidx.compose.foundation.layout.weight`.

## Verification gates

From repo root with `sh`: `.claude/scripts/check.sh`, `test-fast.sh`, `contract.sh`, plus `a11y.sh` once slice 6 adds it. A task counts as finished only when its own check **and** `check.sh` + `test-fast.sh` pass.

## Emulator facts

- AVDs: `huroofi_phone` (1080x2400, 2.625 px/dp); slice 2 adds `huroofi_tablet` and `huroofi_tablet_small`.
- Daily play limit: `adb root`, `settings put global auto_time 0`, move the date forward, restore with `auto_time 1` and `adb unroot` afterwards.
- `uiautomator dump` gives bounds and labels; `adb exec-out screencap -p` gives screenshots. Use `MSYS_NO_PATHCONV=1` for `/sdcard` paths in Git Bash.

---

## Task 0 — Baseline

Commit `08-polish.md`, this file and `00-index.md` (`docs(plan): sharpen plan 08 polish`). Run the three gates on untouched code. Red baseline = stop.

---

## Slice 1 — Orientation and immersive

Decisions 2, 7, 8. After this slice a child sees no system bars; the Parent zone shows them.

### 1.1 Orientation check (decision 2)
Files: none expected. `MainActivity` already locks portrait below sw600dp via `R.bool.portrait_only`.
Check: on `huroofi_phone`, `adb shell settings put system user_rotation 1` with auto-rotate off leaves the app portrait (screenshot). If it does not, fix it and report.

### 1.2 Bars policy (pure)
Files: new `ui/SystemBars.kt`, test `ui/SystemBarsTest.kt`.
- `fun barsHidden(route: String?): Boolean` — false only for the Parent zone route; true for every other route, including the gate and the rest screen; true for null.
Check: test lists every route constant from `HuroofiApp` / `LearnRoutes` and asserts the result.

### 1.3 Apply immersive
Files: `MainActivity.kt`, `HuroofiApp.kt`.
- `WindowCompat.getInsetsController(window, view)`, behaviour `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`; hide or show `systemBars()` whenever the current back-stack route changes, using `barsHidden`.
- Re-apply on window focus regained (dialogs or the app switcher can bring the bars back).
Check: on the phone emulator, `adb shell dumpsys window | grep -i "mVisibleTypes\|statusBars\|navigationBars"` or screenshots show no bars on Toddler Home, Look & listen, Lesson, Quiz, the gate and the rest screen; bars visible in the Parent zone; hidden again after leaving the zone. An edge swipe shows them briefly.

### 1.4 Screen-pinning tip (decision 8)
Files: `parent/ParentZoneScreen.kt`.
- One muted caption line under the Offline pack row: "To keep your child in Huroofi, turn on App pinning in your phone's Settings." No link, no new setting.
Check: screenshot of the zone; `ZoneSpecTest` still green.

---

## Slice 2 — Centred column on tablets

Decisions 1, 4, 6. After this slice every screen sits in a 480 dp column on a tablet, both orientations.

### 2.1 Tablet AVDs
Files: none in the repo.
- `avdmanager create avd -n huroofi_tablet -d pixel_tablet -k <installed google_apis x86_64 image>` and `huroofi_tablet_small` with `-d small_tablet`.
Check: both boot headless; `adb shell wm size` and `wm density` give about 1280x800 dp and 960x600 dp in landscape.

### 2.2 Shared width
Files: `ui/theme/` (new `HuroofiDimens.MaxContentWidth = 480.dp` or the existing dimens object), `toddler/ToddlerHomeScreen.kt`, the other users of `ToddlerHomeSpec.MaxContentWidth`.
- Move the constant to the theme; delete `ToddlerHomeSpec.MaxContentWidth`.
Check: `grep -rn MaxContentWidth` shows one definition; gates pass.

### 2.3 Learn, gate and Parent screens in the column
Files: `learn/{HomeScreen,MapScreen,LessonScreen,TraceScreen,QuizScreen,RewardScreen,StickerBookScreen}.kt`, `gate/ParentGateScreen.kt`, `parent/ParentZoneScreen.kt`, `parent/RestScreen.kt`.
- Background fills the window; content is `widthIn(max = MaxContentWidth)`, centred. Where the content is taller than a landscape window, it scrolls vertically (Trace keeps its canvas: give it a minimum height and let the column scroll).
Check: on both tablet AVDs, portrait and landscape, screenshot every screen; nothing clipped or overlapping; `lp.sh` on Map, Home and zone still within 10%.

---

## Slice 3 — Where's the…? on tablets

Decisions 3, 5.

### 3.1 Layout choice and scale (pure)
Files: new `toddler/find/FindLayout.kt`, test.
- `fun useTabletFind(widthDp: Float, heightDp: Float): Boolean` — `widthDp >= 840 && widthDp > heightDp`.
- `fun tabletFindScale(widthDp: Float, heightDp: Float): Float` — `min(widthDp / 1180, heightDp / 820, 1f)`.
- `fun minTouch(designDp: Float, scale: Float): Float` — `max(designDp * scale, 64f)`.
Check: tests — 1280x800 → tablet, scale ≈ 0.976; 960x600 → tablet, scale ≈ 0.73, Home button 84 → 64; 800x1280 → phone layout; 840x840 → phone layout.

### 3.2 Tablet Find screen
Files: `toddler/find/FindScreen.kt` (new `FindTabletSpec` beside `FindSpec`).
- `BoxWithConstraints` picks the layout. Tablet layout from `ToddlerFindTablet.html`: header (Home 84, speaker 104, star dots 40, lock 52 visual / ≥ 64 touch), sentence card 60 sp Arabic + 20 sp English, two tiles side by side (380 tall, 48 gap, 60 side padding, 48 corner, 8 border, 10 shadow, 250 picture, 72 sp word with highlighted first letter, party stars). All sizes × scale, touch sizes through `minTouch`.
- Same `FindGame` / `FindAudio`; no logic copied.
- `FindTabletSpec` joins `ToddlerRulesSweepTest`.
Check: screenshots on both tablet AVDs in landscape, next to the prototype at the same size; a round played to the party state; portrait shows the phone layout in the column.

---

## Slice 4 — Contrast pairs

Decision 9.

### 4.1 Declared pairs
Files: every screen spec object (`ToddlerHomeSpec`, `CardsSpec`, `FindSpec`, `FindTabletSpec`, `PaintSpec`, `HomeSpec`, `MapSpec`, `LessonSpec`, `TraceSpec`, `QuizSpec`, `RewardSpec`, `ZoneSpec`, gate and rest specs).
- Add `val textPairs: List<ContrastPair>` (`fg`, `bg`, `large: Boolean`, `what: String`) for every text or non-text cue the screen draws on a fixed colour. Stage-coloured pairs are functions of a `Stage`.
- `ContrastPair` and `contrastRatio` live once in `ui/theme/` (move the copies out of `TokensTest` and `ZoneSpecTest`).
Check: compiles; existing tests green.

### 4.2 One sweep test
Files: new `ui/theme/ContrastSweepTest.kt`.
- Every declared pair, plus stage pairs for all 7 stages from `TestContent.repo`: ≥ 4.5 normal, ≥ 3.0 large / non-text.
Check: run it. Any failure: stop, list the failing pairs with ratios, ask the user. Do not change colours.

---

## Slice 5 — Text size 1.3x

Decision 10.

### 5.1 Fixed letters
Files: `ui/theme/ArabicText.kt` (`CenteredLetter`), any other single-letter-in-tile site.
- Letters in fixed tiles size from dp, not the font scale: convert with the density but `fontScale = 1f` (e.g. wrap in `CompositionLocalProvider(LocalDensity provides Density(density.density, 1f))`).
Check: test or preview at `fontScale = 2f` shows tile letters unchanged; `lp.sh` at scale 1.3 still within 10%.

### 5.2 Device pass
Files: whatever clips.
- `adb shell settings put system font_scale 1.3`; walk every screen in all three modes on `huroofi_phone`. Fix clipping by wrapping or scrolling, never by shrinking text. Reset font scale to 1.0 afterwards.
Check: screenshots of every screen at 1.3; list of fixes in the commit message.

---

## Slice 6 — Accessibility gate

Decision 11.

### 6.1 `a11y.sh`
Files: new `.claude/scripts/a11y.sh` (+ a small Python or awk parser beside it), `CLAUDE.md` gate list if it names scripts.
- Dumps the current emulator screen with `uiautomator dump`. Fails when a `clickable` or `long-clickable` node has empty `text` and `content-desc` with no labelled child; when a clickable node is under 64 dp on either side on a child screen; under 48 dp in the Parent zone (`a11y.sh --parent`). Prints the offending nodes with bounds.
Check: passes on Toddler Home; fails on a deliberately unlabelled build (revert it after).

### 6.2 Sweep and TalkBack
Files: whatever fails.
- Run `a11y.sh` on every screen per mode on phone and both tablets. Fix every failure.
- TalkBack on (`adb shell settings put secure enabled_accessibility_services com.google.android.marvin.talkback/...` if installed, else by hand): reading order on Toddler Home, Lesson, Quiz, Parent zone is top-to-bottom and sensible.
Check: `a11y.sh` output for every screen; notes on the TalkBack walk in the commit.

---

## Slice 7 — Release build

Decisions 13, 14.

### 7.1 Shrinking
Files: `app/build.gradle.kts`, `app/proguard-rules.pro`.
- `isMinifyEnabled = true`, `isShrinkResources = true` for release; keep rules for kotlinx-serialization `@Serializable` content classes. Release signed with the debug key locally only (no keystore, no signing secrets in the repo).
Check: `./gradlew assembleRelease` succeeds; install on the phone emulator; Toddler, Preschool lesson-to-quiz, the gate and the zone all work; letters load (serialization kept).

### 7.2 Size
Files: `08-polish.md` (record).
- Record debug and release APK sizes and the art's share. If art is a large share, show numbers to the user before any WebP change.
Check: numbers written in the plan.

---

## Slice 8 — Families

Decision 12.

### 8.1 Permission and dependency gate
Files: `.claude/scripts/check.sh`, new `.claude/scripts/apk-audit.sh` (called by `check.sh`), allow-list file `.claude/scripts/allowed-deps.txt`.
- `aapt2 dump permissions` on the release APK (fall back to debug if release is not built): fail on `INTERNET`, `ACCESS_NETWORK_STATE` only if paired with INTERNET, `com.google.android.gms.permission.AD_ID`, location, camera, `RECORD_AUDIO`, contacts.
- `./gradlew :app:dependencies --configuration releaseRuntimeClasspath`: fail on any group outside the allow-list (`androidx.*`, `org.jetbrains.kotlin*`, `org.jetbrains.kotlinx`, `org.jetbrains` annotations, `com.google.guava:listenablefuture` if pulled by androidx).
Check: passes today; fails when `INTERNET` is added to the manifest (revert it after).

### 8.2 Policy document
Files: new `docs/families-check.md`.
- Read the current Google Play Families policy, Families ads/SDK requirements and Data safety help pages (WebFetch). For each requirement: the source link, date read, Huroofi's answer and evidence (file or gate). Suggested Data safety answers (no data collected or shared) and target audience (Under 5, 6–8). Marked draft for the user to submit.
Check: every requirement row has a source and an answer; the user reviews it.

---

## Done for plan 08

Every task's check passed; `00-index.md` row 08 updated with what was verified and what was not (sound, real devices).
