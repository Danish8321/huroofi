# 15 Redesign — build the approved prototype (HANDOFF §9)

Approved 2026-10-08 by the maintainer ("Go ahead") after the prototype `design-reference/prototype.html` was approved and HANDOFF.md §9 listed what changes. One branch per slice, behaviour first; each slice gated by `check.sh` and `test-fast.sh`, plus `a11y.sh` on the emulator for the screens it touches.

## Decisions

1. **Trace finishes on the whole letter, and Play waits for a tap** (§9 items 2, 10). Branch `feat/redesign-trace`.
   - A stroke needs 100 % of its dots (was 70 %), within 30 dp, any order or direction.
   - The letter also has to be painted all over: a grid every 6 dp inside the glyph, at least 4 dp from its edge. Ink paints everything within the brush of its path.
   - The brush is sized per letter: its radius is the furthest grid point from the strokes' centre lines plus 4 dp (at least 13 dp), so tracing down the middle of every stroke paints the whole letter. The ink is drawn that wide, clipped to the letter.
   - Once every stroke is done, unpainted spots pulse yellow (7 dp, navy edge) and the hint becomes "Now colour in the yellow spots!" (new clip `fill_hint.mp3`, silent until recorded). The 5 s idle replay says it again when no stroke is left to show.
   - When done: confirm buzz, cheer, star burst and "You traced ب!"; a locked grey "Finish ب first" button turns into "Play" ("Next letter" in practice). Nothing moves on until it is tapped.
2. **Quiz waits for "Great job! Next"** after each right answer (§9 item 3).
3. **Trace practice counts toward play time** (§9 item 9).
4. **Find**: no sets of three or star counter; 4 s fill-up Next (§9 item 5).
5. **Paint**: celebration, 4 s ring countdown, "Paint it again" (§9 item 4).
6. Shared components, then the layouts screen by screen: Home / Map / Stickers, Meet / Trace / Quiz, Reward / gate / Parent zone / Rest, Toddler home and Look & listen (§9 items 1, 6, 7, 8, 11).

Slices 2–6 are sharpened before each starts.

## Out of scope

The unapproved explorations (ToddlerIntro, ToddlerWellDone, ParentProgress); recording the new clips (maintainer).

## Built

- Decision 1, `feat/redesign-trace`, merged to `main` (`4f4d623`). `check.sh`, `test-fast.sh`, `contract.sh` PASS; new `LetterAreaTest` (brush reach, centre-line trace paints a bar, thin brush leaves gaps), `StrokeCheckTest` now needs every dot. Emulator `huroofi_phone`, Trace ص: a trace down the dots finished the letter with the whole shape painted, "You traced ص!" badge and green edge, "Play" unlocked and stayed put for 4 s+, a tap opened the game. After Again, the same trace about 8 dp low followed every dot but left two yellow spots, the hint switched to "Now colour in the yellow spots!" and the button stayed "Finish ص first"; painting the spots unlocked Play. `a11y.sh` PASS on the locked and the done screen.
- Not verified: Trace practice's "Next letter" on the emulator, tablet, sound (`fill_hint.mp3` is a silent placeholder), the star burst of the prototype (left to the shared-components slice), no JVM test renders real glyphs, so "centre line paints every letter" rests on the brush rule and the prototype's flow test (ج د ه), not on all 28 in the app.
- Decision 2, `feat/redesign-quiz`. `check.sh`, `test-fast.sh` PASS; `QuizRoundTest` covers the button label ("Great job! Next", then "Next letter", "Get your sticker", "See your sticker" once all 28 are learned, "Back home" for a review). A right answer plays praise then the word, as in the prototype. Emulator `huroofi_phone`, Play ص: each of the three rounds stayed on its right answer for 5 s until "Great job! Next" was tapped; the third read "Get your sticker" and opened the Rocket Base sticker; `a11y.sh` PASS on the answered screen. The panel's own "Great job!" was dropped (the button says it), so it reads "صاروخ starts with ص"; that change is after the emulator walk, gates only.
- Not verified: "Back home" and "See your sticker" on the emulator, Early reader's 2 × 2 grid.
- Decision 3, `feat/redesign-practice-time`. `check.sh`, `test-fast.sh` PASS; `PlayTimeTest.tracePracticeCountsAsPlay`. Not walked on the emulator (needs a 5-minute limit to run out).
