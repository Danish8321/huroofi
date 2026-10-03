# 07 Preschool / reader flow — executable task list

Written 2026-10-03 so a session with no prior conversation can build plan 07.
Spec: `07-preschool-reader.md` (approved, decisions 1–9). This file only breaks that spec into tasks; where the two disagree, `07-preschool-reader.md` wins and the conflict is reported, not guessed.

## Read first (in this order)

1. `CLAUDE.md`, `HANDOFF.md` §2, §3, §6, §7, `CONTEXT.md` (Letter path, Learned / Learning letter, Today's letter, Stage complete, Sticker, Sticker book, Letter shape, Non-joining letter).
2. `.scratch/plans/07-preschool-reader.md` — decisions are binding.
3. Prototypes `design-reference/screens/{Main,StageMap,Lesson,Trace,Quiz,Reward}.html`. Rebuild natively; never a WebView.
4. Code this builds on: `HuroofiApp.kt` (routes, `startRoute`, `PlayTimeKeeper`), `parent/PlayTime.kt` (`ChildRoutes`), `parent/ParentProgress.kt` (learning-letter rule), `data/progress/*` (`markLetterComplete`, `awardSticker`, `stickers`, `unlockedStage`, `mode`), `audio/Clips.kt`, `toddler/PromptPlayer.kt`, `toddler/ToddlerBack.kt`, `ui/components/*` (`StepTabs`, `LetterChip`, `PictureTile`, `letterPicture`, `PrimaryButton`, `RoundIconButton`, `SpeechBubble`), `toddler/paint/` (finger drawing).

## Where to work

- Branch `feat/preschool-reader` from `main` at `4c3782d`. One commit per task; push after every slice. Never commit to `main`.
- New code in package `learn/` under `app/src/main/java/com/huroofi/app/`. Tests in `app/src/test/java/com/huroofi/app/learn/` (JVM only).
- Conventional commit messages (`feat(learn): ...`) ending with the harness attribution lines.

## Invariants (every task)

- No new dependency, no Robolectric, no instrumented tests. Logic in pure functions/classes with unit tests; UI checked by `@Preview`, `check.sh` and the emulator pass.
- Never silence lint or a test.
- Letters, stages, colours come from `ContentRepository`. No hard-coded letter list. The non-joining set is the one exception allowed by the glossary; keep it in one constant with a test against the six letters.
- No `letters.json` change, no DataStore key or schema change.
- Child screens: touch ≥ 64 dp, no red, no X, no fail state, sound first. Sizes and colours live in one `object XxxSpec` per screen; every spec joins a sweep test (task 6.1).
- Arabic in `ArabicText` (Noto Naskh), English in Baloo. `safeDrawingPadding()` on every screen.
- Letter-path screens take the letter index as a nav argument (`lesson/{index}`, `trace/{index}`, `quiz/{index}`; `reward/{stage}`), so a review letter stays the same across the three steps.
- Delete what you replace (`Routes.Placeholder`, `PlaceholderScreen`).
- Never import `androidx.compose.foundation.layout.weight` (scope member; the import breaks the build).

## Verification gates

From repo root with `sh`: `.claude/scripts/check.sh`, `test-fast.sh`, `contract.sh`. A task counts as finished only when its own check **and** `check.sh` + `test-fast.sh` pass. `test-full.sh` / `e2e.sh` do not exist; don't claim them.

---

## Task 0 — Baseline

Commit `CONTEXT.md`, both plan files and `00-index.md` (`docs(plan): sharpen plan 07 preschool / reader`). Run the three gates on untouched code. Red baseline = stop.

---

## Slice 1 — Home and Meet (Lesson)

Decisions 1, 2, 7, 9. After this slice a Preschool / Reader child opens Home and hears a lesson.

### 1.1 Today's letter (pure)
Files: new `learn/LetterPath.kt`, test `learn/LetterPathTest.kt`.
- `fun todaysLetter(letters, completed: Set<Int>, openStage: Int, random: Random): Letter` — the learning letter (reuse `parentProgress` rule; move the shared bit into one function rather than copying it); all 28 learned → a random learned letter.
- `fun nextStep(letters, completed, justLearned: Int): PathNext` — `sealed`/enum result: `Reward(stage)` when `justLearned` made its stage complete, else `Meet(nextLearningIndex)`, else `Home` (all done).
Check: tests — empty → alif; gap in open stage → the gap; all 28 → letter from `completed` (seeded Random); 4th of stage 1 → Reward(1); 3rd → Meet(next); 28th → Reward(7).

### 1.2 Letter shapes (pure)
Files: new `learn/LetterShapes.kt`, test.
- `NON_JOINING = setOf("أ","د","ذ","ر","ز","و")`; `fun letterShapes(letter: String): List<Pair<ShapeKind, String>>` — Alone, Start, Middle, End via U+0640; non-joining → Alone, End only.
Check: tests — ب gives 4 shapes with tatweel in the right places; each of the six non-joining letters gives 2; the six constants match letters present in content.

### 1.3 Routes and play time
Files: `HuroofiApp.kt`, `parent/PlayTime.kt`, `toddler/ToddlerBack.kt` (+ tests), `StartRouteTest.kt`, `PlayTimeTest.kt`.
- Routes `Home = "home"`, `Lesson = "lesson/{index}"` (+ builder). `startRoute(PRESCHOOL|READER) = Home`. Delete `Placeholder` + `PlaceholderScreen`.
- `ChildRoutes` += Home, Lesson (later slices add theirs in the same task that adds the route).
- Back per decision 9: Home → Gate (`popUpTo(Home)`), Lesson → Home. Extend `toddlerBack` or add a `learnBack(route)` beside it; one pure function, tested.
Check: `StartRouteTest`, `PlayTimeTest`, back tests updated; gates.

### 1.4 Home screen
Files: new `learn/HomeScreen.kt` (`object HomeSpec`, stateless screen, preview), `learn/HomeRoute.kt`, `learn/LearnNav.kt` (bottom nav).
- Lion avatar + "Hi there!"; Today card (label, big letter, Latin name, picture, picture word with first letter highlighted, "Let's go!"); one row of 3 tiles Learn / Trace / Play (Trace and Play hidden until slices 2–3 wire them); stage strip with 4 chips; bottom nav Home / Map / Stickers / Parents (Map and Stickers items arrive in slices 4–5; Parents → Gate).
- Route collects `completedLetters`, `unlockedStage`; remembers the review pick with `rememberSaveable`.
Check: `check.sh`; previews (fresh, mid-stage, all learned).

### 1.5 Lesson (Meet)
Files: new `learn/LessonScreen.kt` (`object LessonSpec`), `learn/LessonRoute.kt`.
- Back chevron, `StepTabs` Meet/Trace/Play on Meet; card ringed in stage border colour with index circle, Latin name, Arabic name pill, big letter; word row (picture, picture word, English meaning); sound button "Hear the letter" + "Say it with me!"; shapes grid RTL for READER only (decision 2); "Next: Trace it" (until slice 2: hidden).
- On open: `PromptPlayer` plays letter clip then word clip; sound button or picture replays both.
- Mode read from `progress.mode`.
Check: gates; previews Preschool and Reader (ب and د). Push.

---

## Slice 2 — Trace

Decision 3.

### 2.1 Coverage check (pure)
Files: new `learn/TraceCheck.kt`, test.
- `class TraceCheck(targets: List<Offset-like Pair<Float,Float>>, tolerancePx: Float, need: Float = 0.7f)`; `fun addInk(points)`; `val coverage`; `val done` (≥ need). Grid-bucket the targets so each ink point checks only nearby cells.
- `fun sampleMask(width, height, step, isInk: (x, y) -> Boolean)`: grid sampling helper, pure.
Check: tests — no ink 0; ink on every target → done; ink 31 px away with 30 px tolerance → not covered; 70 % boundary; ink far outside changes nothing.

### 2.2 Trace screen
Files: new `learn/TraceScreen.kt` (`object TraceSpec`), `learn/TraceRoute.kt`; route `trace/{index}` in `HuroofiApp.kt`, `ChildRoutes`, back → Lesson.
- Lion helper bubble "Trace the letter with your finger!"; faded big Naskh glyph; on layout, render the glyph to an `ImageBitmap` the size of the canvas and `sampleMask` it (≈ 8 dp step); tolerance 30 dp in px.
- Finger ink reuses the toddler paint approach; 5 crayons; "Again" clears ink and `TraceCheck`; "I did it!" visible once ink exists.
- Done (auto or button): `cheer`, short pause, navigate `quiz/{index}`. Until slice 3, go back to Home.
- Wire Lesson "Next: Trace it" and Home Trace tile.
Check: gates; emulator spot check of one joining and one non-joining letter. Push.

---

## Slice 3 — Play (quiz)

Decisions 1, 4.

### 3.1 Quiz round (pure)
Files: new `learn/QuizRound.kt`, test.
- `fun quizRound(target: Letter, letters, optionCount: Int, random): List<Letter>` — target + distractors from all 28, never a distractor whose `wordFirst` equals the target letter, shuffled.
- `class QuizGame(rounds = 3)`: `answer(index): Answer { Right, Wrong }`, `roundsDone`, `finished`.
- `fun optionCount(mode) = if (mode == READER) 4 else 3`.
Check: tests — counts 3 / 4; target always present; no duplicates; guard rule; seeded randoms vary order; wrong answers never advance; 3 rights finish.

### 3.2 Quiz clip
Files: `audio/Clips.kt` (`whichStartsWith`), `ClipsTest` count, `tools/make-placeholders.sh`, run it, `docs/review/toddler-phrases.md` (new section before "## Per-letter prompts"; the table stays last).
Check: `contract.sh`, `ToddlerPhrasesTest`.

### 3.3 Quiz screen
Files: new `learn/QuizScreen.kt` (`object QuizSpec`), `learn/QuizRoute.kt`; route `quiz/{index}`, `ChildRoutes`, back/✕ → Home.
- ✕ button, 3 progress dots, "Which one starts with" + letter chip + "Hear the question"; options grid (2×2 for 4, row/column of 3 for 3).
- On open and each round: `whichStartsWith` then letter clip.
- Wrong: `boing`, soft orange "Almost! Try again", correct option wiggles, tapped option dims. Right: green, `praise`, "Great job!", next round.
- Finished: if not yet learned, `markLetterComplete(index)`; if that makes the stage complete, `awardSticker(stage)` in the same coroutine. Then "Continue" → `nextStep` (Meet next letter / Reward / Home). Review letters write nothing.
- Wire Trace done → quiz and Home Play tile.
Check: gates; `QuizSpec` colour test: no red (hue check like toddler rules). Push.

---

## Slice 4 — Reward and Sticker book

Decision 5.

### 4.1 Sticker badge
Files: new `learn/StickerBadge.kt` (composable + preview).
- Round badge: stage pastel fill, border ring, the stage's 4 letter pictures in a 2×2; name "<stage> sticker" below. `empty` variant: soft pastel outline + "?".
Check: `check.sh`; preview all 7.

### 4.2 Reward screen
Files: new `learn/RewardScreen.kt` (`object RewardSpec`), `learn/RewardRoute.kt`; route `reward/{stage}`, `ChildRoutes`, back → Map (Home until slice 5).
- Confetti (simple Canvas particles), 3 stars, "Stage N complete!", "You learned 4 new letters", 4 tiles, sticker card, "<next> unlocked" / "All letters learned!", "Next stage". `cheer` on open.
Check: gates; preview stages 1 and 7.

### 4.3 Sticker book
Files: new `learn/StickerBookScreen.kt`, route `stickers`, `ChildRoutes`, back → Home; nav item Stickers.
- Title, 7 slots grid from `progress.stickers`.
Check: gates. Push.

---

## Slice 5 — Letter Map

Decision 8.

### 5.1 Stage states (pure)
Files: `learn/LetterPath.kt` (+ test).
- `enum StageState { FINISHED, CURRENT, LOCKED }`; `fun stageStates(stages, completed, stageOf)`; chip state per letter reuses `LetterState`.
Check: tests — fresh: 1 current, rest locked; stage 1 done: 1 finished, 2 current; all done: all finished, none current.

### 5.2 Map screen
Files: new `learn/MapScreen.kt` (`object MapSpec`), `learn/MapRoute.kt`; route `map`, `ChildRoutes`, back → Home; nav item Map; Home stage strip → Map; Reward "Next stage" and back → Map.
- Title "Letter Map" + subtitle; vertical scroll to current stage; dotted path on `Canvas`; 7 zig-zag cards with name, 4 chips, state decoration; play button → `lesson/{today}`; locked tap → `boing` + wiggle.
Check: gates. Push.

---

## Slice 6 — Close out

### 6.1 Sweep
Files: test `learn/LearnSpecTest.kt` — every `*Spec` touch size ≥ 64 dp, text ≥ `TypeScale.FLOOR_SP`, no red hues in child colours.

### 6.2 Ḥarakāt note
Files: `parent/ParentZoneScreen.kt` — muted line "Vowel marks arrive in a later update." under the toggle (≥ 16 sp, AA contrast, add pair to `ZoneSpec.textPairs`).
Check: `ZoneSpecTest`.

### 6.3 Emulator pass
`huroofi_phone`: set Preschool in the zone, restart, walk one letter Meet → Trace → Play; finish stage 1 (4 letters) and see Reward and the sticker in the book and the Map; switch to Early reader and check shapes for ب and د and 4 options; Back from every screen; play-time limit 5 → Rest replaces a learn screen. Record what was and wasn't verified (sound is not).

### 6.4 Statuses
Mark `07-preschool-reader.md` built, `00-index.md` row 07. Gates, push. No merge or PR unless asked.

## Stop and ask if

- A gate fails three times on the same cause (`diagnosing-bugs`).
- A task needs a new dependency, lint suppression, DataStore change or `letters.json` change.
- Glyph-to-bitmap sampling proves unreliable on the emulator (fallback must be agreed, not invented).
- The prototype and a decision disagree in a way this file doesn't settle.
