# 09 Guided tracing — executable task list

Written 2026-10-05 so a session with no prior conversation can build plan 09.
Spec: `09-guided-trace.md` (approved, decisions 1–8). Where the two disagree, `09-guided-trace.md` wins and the conflict is reported, not guessed.

## Read first (in this order)

1. `CLAUDE.md`, `HANDOFF.md` §7 (Trace rule), §8, `CONTEXT.md` (Stroke, Stroke order).
2. `.scratch/plans/09-guided-trace.md` and `.scratch/issues/014-guided-trace-hints.md` with its reference pictures.
3. Prototype `design-reference/screens/Trace.html`.
4. Code this builds on: `learn/TraceScreen.kt` (`TraceSpec`, `TraceScreen`), `learn/TraceCheck.kt` (replaced), `learn/LearnScreens.kt` (`TraceRoute`), `ui/theme/GlyphFit.kt` (`fitGlyph`, `inkBounds`), `toddler/paint/PaintScreen.kt`, `toddler/paint/LetterOutline.kt`, `data/content/` (`ContentRepository`, `ContentJson`, `Models`), `audio/Clips.kt`, tests `data/content/ContentContractTest.kt`, `learn/TraceCheckTest.kt`, `learn/LearnSpecTest.kt`, `ui/theme/TokensTest.kt`.

## Where to work

- Branch `feat/guided-trace` from `main`. One commit per task; push after every slice. Never commit to `main`.
- Conventional commit messages ending with the harness attribution lines.

## Invariants (every task)

- No new dependency. No Robolectric, no instrumented tests: logic in pure functions with JVM tests, UI checked by the gates and the emulator.
- Never silence lint or a test. Delete what you replace (`TraceCheck`, `sampleMask`, `glyphTargets`, their test).
- Child screens: touch ≥ 64 dp, no red, sound first, no fail state.
- No `letters.json`, DataStore or colour-token change.

## Verification gates

From repo root with `sh`: `.claude/scripts/check.sh`, `test-fast.sh`, `contract.sh`, `a11y.sh` (emulator). A task is finished only when its own check **and** `check.sh` + `test-fast.sh` pass.

---

## Task 0 — Baseline

Commit `09-guided-trace.md`, this file, `00-index.md`, issue 014 and `CONTEXT.md` (`docs(plan): guided tracing plan 09`). Run the gates on untouched code. Red baseline = stop.

---

## Slice 1 — Stroke data and review sheet

After this slice the data exists, is checked by the contract gate, and the review sheet is ready for the maintainer.

### 1.1 Model and decoder
Files: `data/content/Models.kt`, `ContentJson.kt`, `ContentRepository.kt`, new `data/strokes.json`, `app/src/main/assets/strokes.json` (two letters only for now: alif, baa).
- `@Serializable data class LetterStrokes(val index: Int, val strokes: List<TraceStroke>)`, `data class TraceStroke(val order: Int, val dot: Boolean, val points: List<List<Float>>)`; file root `{ "letters": [...] }`. Strict decode like `letters.json`.
- `ContentRepository.strokes(index): List<TraceStroke>` sorted by `order`.
Check: decode test for the two letters; `check.sh`, `test-fast.sh`.

### 1.2 Contract
Files: `ContentContractTest.kt`, `.claude/scripts/contract.sh`.
- `contract.sh`: app copy == `data/strokes.json` (line endings ignored).
- Tests: indices are exactly 1–28 once complete (until 1.4, assert only that present indices exist in `letters.json`); orders are 1..n without gaps; every point within 0..1; a dot has 1 point, any other stroke ≥ 2; at least one non-dot stroke per letter.
Check: `contract.sh` PASS; a hand-broken copy fails it (then restored).

### 1.3 Review sheet
Files: new `docs/review/strokes.html`, `docs/review/strokes.md`.
- HTML loads `../../assets/fonts/` Noto Naskh Arabic Bold and `../../data/strokes.json`; for each letter draws the glyph pale on a canvas, maps points through the ink box from `measureText` (`actualBoundingBox*`), draws each stroke's centre line, numbered coins (1 green, rest blue) and an end arrow. No external script.
- `strokes.md`: how to open the sheet, what to check (start point, direction, order, dots last unless taught otherwise), a table of 28 letters with a sign-off column, status "pending review".
Check: open in a browser, screenshot alif and baa; points sit on the glyph.

### 1.4 All 28 letters
Files: `data/strokes.json`, app copy.
- Draft the remaining 26 letters. Dots are separate strokes after the body; hamza (أ) is its own short stroke.
- Tighten the 1.2 test to exactly indices 1–28.
Check: `contract.sh`, `test-fast.sh`; screenshot of the full sheet saved beside `strokes.md`; every letter checked against standard handwriting order and noted in `strokes.md`.

---

## Slice 2 — Guided trace (Preschool / Early reader)

### 2.1 Ink box on the canvas
Files: `ui/theme/GlyphFit.kt`, `GlyphFitTest` (pure part).
- `fitGlyph` also returns the glyph's ink rect in canvas pixels (or a new `fitGlyphBox`); existing callers keep working.
- Pure `fun toCanvas(point: List<Float>, ink: Rect): Offset`.
Check: unit test of `toCanvas` corners and centre.

### 2.2 Stroke check (pure)
Files: new `learn/StrokeCheck.kt`, `learn/StrokeCheckTest.kt`; delete `TraceCheck.kt`, `TraceCheckTest.kt`.
- `fun strokeDots(points: List<Offset>, spacingPx: Float): List<Offset>` resamples a polyline evenly (a dot stroke gives its one point).
- `class StrokeCheck(strokes: List<List<Offset>>, tolerancePx: Float, need: Float = 0.7f)`: `addInk(p)`, `covered(stroke, dot)`, `coverage(stroke)`, `done` (every stroke ≥ need), `nextStroke(): Int?` (lowest unfinished), `clear()`.
Check: tests for any-order completion, a skipped dot stroke keeps it not done, direction ignored, `nextStroke` after each stroke, `clear`.

### 2.3 Draw the guide
Files: `learn/TraceScreen.kt`, `LearnSpecTest.kt`.
- Pale band (glyph fill), dots per stroke, numbered coins with arrows, pulse on `nextStroke`, covered dots shrink, finished coin becomes a star. Bubble text per decision 7.
- `TraceSpec.textPairs`: replace "letter guide" with the dot, coin-number and coin-edge pairs.
Check: contrast test green; phone emulator screenshots of baa before, during and after tracing; auto-complete still fires, "I did it!" still appears with any ink.

---

## Slice 3 — Demo ball

### 3.1 Schedule (pure)
Files: new `learn/TraceDemo.kt`, `learn/TraceDemoTest.kt`.
- A pure state machine: events `Enter`, `SoundDone`, `Touch`, `Ink`, `Idle(ms)`, `HelperTap`, `Again`, `Done`; output which strokes to play (all / next only / none). Rules from decision 4.
Check: tests for every rule, including no idle replay once done.

### 3.2 Animate and wire
Files: `learn/TraceScreen.kt`, `learn/LearnScreens.kt` (`TraceRoute` plays the letter clip on entry).
- Ball with fading trail moves along the stroke points at ~1.5 s per stroke; touch stops it. Helper picture becomes the "Show me how" button (64 dp).
Check: emulator: sound then demo on entry (log the clip call; sound is silent placeholder), touch stops it, 5 s idle replays the next stroke, helper and "Again" replay; `a11y.sh` PASS on Trace.

---

## Slice 4 — Toddler Paint ball

### 4.1
Files: `toddler/paint/PaintScreen.kt` (and a shared ball composable from 3.2 moved to `ui/`).
- On each new page, the ball travels the letter's strokes once over the outline. No numbers, dots, check or idle repeat.
Check: emulator screenshots mid-demo; `a11y.sh` PASS in Toddler.

---

## Slice 5 — Close out

- Phone and both tablet emulators, portrait and landscape: Trace for at least alif, baa, jiim, siin, kaaf, haa (26) and yaa; `a11y.sh` on Trace and Paint; text size 1.3x.
- `00-index.md` row 09: "built, stroke order pending review", with what was and was not verified (sound, real devices).
- Issue 014 closed as done, pointing at plan 09.
