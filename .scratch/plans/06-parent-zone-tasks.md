# 06 Parent zone — executable task list

Written 2026-10-03 so a session with no prior conversation can build plan 06.
Spec: `06-parent-zone.md` (approved, decisions 1–10). This file only breaks that spec into tasks; where the two disagree, `06-parent-zone.md` wins and the conflict is reported, not guessed.

## Read first (in this order)

1. `CLAUDE.md`, `HANDOFF.md` step 4, `CONTEXT.md` (glossary: Learned / Learning letter, Letters to go, Daily play time, Rest screen).
2. `.scratch/plans/06-parent-zone.md` — the decisions are binding.
3. Prototype `design-reference/screens/Parents.html`. Rebuild natively; never a WebView.
4. Existing code the zone builds on: `data/progress/ProgressRepository.kt` + `ProgressRules.kt` (mode, completed letters, voice, ḥarakāt, limit, usage — all already stored), `HuroofiApp.kt` (routes, `ParentZoneStub`), `toddler/ToddlerBack.kt`, `audio/Clips.kt`, `tools/make-placeholders.sh`.

## Where to work

- Branch `feat/parent-zone` (from `main` at `1a4a1b0`). One commit per task; push after every slice. Never commit to `main`.
- Package root `app/src/main/java/com/huroofi/app/`; new code goes in `parent/`. Tests: `app/src/test/java/com/huroofi/app/` (JVM only).
- Commit messages: conventional (`feat(parent): ...`), ending with the harness attribution lines.

## Invariants (every task)

- No new dependency. `lifecycle-runtime-compose` (2.10.0) is already there for `collectAsStateWithLifecycle` / `LifecycleResumeEffect`.
- No Robolectric, no instrumented tests. Logic lives in pure classes with unit tests; UI checked by `@Preview` + `check.sh` + the emulator pass in 5.2.
- Never silence lint or a test.
- Letters and stages come from `ContentRepository`. No hard-coded letter list.
- Zone: `parentBg`, English in Baloo (`HuroofiText`), Arabic in `ArabicText`, nothing under 16 sp, touch ≥ 48 dp, AA contrast. Sizes and prototype colours live in `object ZoneSpec` (like the toddler `*Spec` objects).
- Rest screen is a child screen: toddler rules apply (no red, Parent lock is the only control, sound first).
- No change to the DataStore keys or schema version: everything plan 06 stores already has a key.
- Delete what you replace (`ParentZoneStub`).

## Verification gates

From repo root with `sh`: `.claude/scripts/check.sh`, `test-fast.sh`, `contract.sh`. "Done" for a task = its own check **and** `check.sh` + `test-fast.sh` pass. `test-full.sh` / `e2e.sh` do not exist; don't claim them.

---

## Task 0 — Baseline

Commit `CONTEXT.md` + both plan files (`docs(plan): sharpen plan 06 parent zone`). Run the three gates on the untouched code. Red baseline = stop.

---

## Slice 1 — Zone shell, child card, letter grid

Decisions 1, 2, 3, 8, 9.

### 1.1 Parent progress (pure)
Files: new `parent/ParentProgress.kt`, test `parent/ParentProgressTest.kt`.
- `enum class LetterState { LEARNED, LEARNING, TO_GO }`.
- `data class ParentProgress(stage: Stage, states: Map<Int, LetterState>)` with `learned`, `learning`, `toGo` counts and `fraction` (learned / 28).
- `fun parentProgress(letters, stages, completed: Set<Int>, openStage: Int): ParentProgress` — learned = in `completed`; learning = first not-learned letter (by `index`) whose `stage == openStage`, else none; rest to go.
Check: tests — nothing done = alif learning, 0 learned / 1 learning / 27 to go; stage 1 done opens stage 2's first letter; a gap inside the open stage is the learning one; all 28 done = 28/0/0 and stage 7; counts always sum to 28.

### 1.2 Zone spec + header + card
Files: new `parent/ParentZoneScreen.kt` (`object ZoneSpec`, `ParentZoneScreen` stateless, preview).
- `ZoneSpec`: page padding 20, gap 16; card white, radius 22, border 1 `#D5E2F0`, padding 16; back 48; avatar 52 (`#FFF4D6`, border `#FFC93C`, `pic_lion`); bar 14 (track `#E3ECF7`, learned `#158048`, learning `#1F6FE0`); legend squares 12 (to go `#C9D6E6`); grid cell 42, gap 6, radius 12, 7 columns, glyph 26 sp; learned / learning / to-go cell colours from decision 2; stepper button 48; switch 56×34.
- Header: round back button (`RoundIconButton`, label "Back to kid mode") + title "Parent zone" 26 sp/800.
- Card: avatar, "Your child", "Stage N of 7 · <name>", progress bar (learned segment then learning segment), legend with counts. Toddler-mode note when mode is TODDLER.
- Column scrolls (`verticalScroll`), `safeDrawingPadding()`.
Check: `check.sh`; preview with a mid-way progress.

### 1.3 Letter grid
Files: same screen file.
- Section card "All 28 letters" (no hint line). 4 rows × 7 cells, right-to-left (alif top-right) — use `LayoutDirection.Rtl` for the grid only. Cells are not clickable (decision 3); `contentDescription` "<name_latin>, learned/learning/to go".
Check: `check.sh`; preview.

### 1.4 Wire the zone
Files: `HuroofiApp.kt` (delete `ParentZoneStub`), new `parent/ParentZoneRoute.kt`.
- Route reads `mode`, `completedLetters`, `unlockedStage` with `collectAsStateWithLifecycle`, builds `ParentProgress`, passes it down. Keeps the debug gallery button (`DEBUG_GALLERY_ROUTE`) at the bottom.
- Back button and system Back: `nav.popBackStack(startRoute, inclusive = false)` (decision 8).
Check: gates. Push.

---

## Slice 2 — Learning mode

Decision 8 (applies on next start).

### 2.1 Mode picker
Files: `parent/ParentZoneScreen.kt`, `ParentZoneRoute.kt`, test `parent/ZoneSpecTest.kt` (start it here).
- Card "Learning mode", subtitle "Changes what your child sees when the app opens". 3 equal buttons (min 64 dp, radius 16, border 2): Toddler "18m–3y", Preschool "3–5y", Early reader "5y+". On `#1F6FE0` with white text; off `#F4F7FB`, border `#D5E2F0`, navy text. `Role.RadioButton`, `selected` semantics.
- Tap = `progress.setMode(it)`. No navigation, no live switch.
Check: `ZoneSpecTest` — every zone touch size ≥ 48 dp; on-button text/background contrast ≥ 4.5 (write `contrast(a, b)` in the test from relative luminance). Gates. Push.

---

## Slice 3 — Toggles and offline row

Decisions 4, 5, 7.

### 3.1 Switch row component
Files: `parent/ParentZoneScreen.kt`.
- `ToggleRow(title, subtitle, checked, onChange)`: min height 64, divider `#EEF3F9`, whole row `toggleable(role = Role.Switch)`; drawn switch 56×34, track on `#158048` / off `#C9D6E6`, knob 26 white. Title 18 sp/800, subtitle 16 sp `#4A5D7A`.
Check: `check.sh`; preview both states.

### 3.2 Settings card
Files: screen + route.
- One card, as in the prototype: voice, vowel marks, daily play time (slice 4), offline.
- "Voice & sounds" / "Native-speaker audio for every letter" → `setVoiceEnabled`.
- "Show vowel marks (ḥarakāt)" / "Adds vowel marks like بَ بِ بُ in lessons" (Arabic part via `ArabicText` inline) → `setHarakatEnabled`.
- Static "Offline pack" / "All content is on this device." row, no control.
Check: gates. Push.

---

## Slice 4 — Daily play time

Decision 6.

### 4.1 Child routes and rest redirect (pure)
Files: new `parent/PlayTime.kt`, test `parent/PlayTimeTest.kt`; `HuroofiApp.kt` (`Routes.Rest = "rest"`).
- `val ChildRoutes = setOf(Placeholder, ToddlerHome, ToddlerCards, ToddlerFind, ToddlerPaint)` (Placeholder stands in for the Preschool / Reader home until plan 07).
- `enum class RestMove { None, ToRest, LeaveRest }`; `fun restMove(route: String?, limitReached: Boolean)` — child route + reached → ToRest; Rest + not reached → LeaveRest; else None.
Check: tests for every route in `Routes` × both flags; Gate, ParentZone, Rest never count as child routes.

### 4.2 Usage meter (pure)
Files: same, test.
- `class UsageMeter(flushEvery: Int = 10)`: `tick(): Int?` adds one second and returns the seconds to save when `flushEvery` is reached; `drain(): Int` returns and clears the rest; `pending`.
- `fun limitReached(stored, pending, limitMinutes)` reuses `ProgressRules.limitReached` on `stored + pending` so the Rest screen is not up to 10 s late.
Check: tests — 9 ticks save nothing, 10th returns 10; drain after 13 ticks returns 3; drain twice returns 0.

### 4.3 Rest clip
Files: `audio/Clips.kt` (`timeToRest = Clip("audio/prompts/time_to_rest.mp3")`, in `all`), `tools/make-placeholders.sh` (`echo audio/prompts/time_to_rest.mp3`), run the script, `docs/review/toddler-phrases.md` (add "وقت الراحة، إلى اللقاء غدًا!").
Check: `contract.sh` (fails before the placeholder exists, passes after).

### 4.4 Rest screen
Files: new `parent/RestScreen.kt` (`object RestSpec`), preview; test: add `RestSpec` to `ToddlerRulesSweepTest`.
- Sky background, `pic_lion` large, "وقت الراحة" big (`ArabicText`), caption "Time to rest. See you tomorrow!", `ParentLock` top corner. No other control, no timer.
- On first show: `PromptPlayer` plays `Clips.timeToRest` (replacing whatever was playing). Bubble-free; no replay.
Check: sweep test passes; `check.sh`.

### 4.5 Count and redirect at the NavHost
Files: `HuroofiApp.kt`, `toddler/ToddlerBack.kt` (+ test).
- Current route from `nav.currentBackStackEntryAsState()`. Counting = route in `ChildRoutes` and lifecycle ≥ RESUMED (`LifecycleResumeEffect` keyed on route): a coroutine ticks `UsageMeter` every second and calls `addUsageSeconds` on flush; on pause / dispose, `drain()` and save in an app-scope `rememberCoroutineScope` launch.
- `LaunchedEffect(route, reached)` applies `restMove`: ToRest → `nav.navigate(Rest) { popUpTo(startRoute) }`; LeaveRest → `nav.popBackStack(startRoute, false)`.
- Rest composable: Back swallowed (`toddlerBack` gets `Rest → Swallow` in every mode); lock → `Gate` with `popUpTo(startRoute)`. Zone back returns to start, which redirects to Rest again while the limit is still reached.
- Limit change in the zone takes effect immediately because `reached` is a flow.
Check: `ToddlerBackTest` (Rest swallows in toddler and non-toddler start); gates. Push.

### 4.6 Stepper
Files: screen + route.
- Row in the settings card, above Offline: "Daily play time" / "A friendly “time to rest” screen appears", − value + (buttons 48 dp, radius 12, `#F4F7FB`, border `#D5E2F0`; value min-width 54, 18 sp, "20 min"). Bounds 5 / 60 disable the button; `setDailyLimitMinutes(±5)`.
Check: gates. Push.

---

## Slice 5 — Close out

### 5.1 Zone sweep
Files: `parent/ZoneSpecTest.kt` — all touch sizes ≥ 48 dp, all text sizes ≥ 16 sp, text/background pairs ≥ 4.5 contrast.

### 5.2 Emulator pass
`huroofi_phone` AVD: open zone via lock hold, check card / grid / mode / toggles / stepper; set limit 5, let Find run 5 minutes (never edit DataStore by hand) and confirm Rest appears, Back does nothing, lock → zone → raise limit → back lands on home. Record what was and wasn't verified.

### 5.3 Statuses
Mark `06-parent-zone.md` built, update `00-index.md` row 06. Gates, push. Do not merge or open a PR unless asked.

## Stop and ask if

- A gate fails three times on the same cause (`diagnosing-bugs`).
- A task needs a new dependency, a lint suppression, a DataStore schema change or a `letters.json` change.
- The prototype and a decision disagree in a way this file doesn't settle.
