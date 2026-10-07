# 11 Kid review — fixes from the ages 1.5–5 screen review

Approved 2026-10-07 by the maintainer ("Yes go ahead with all recommendations") after the screen review on the phone emulator. Branch `feat/kid-review`, one commit per decision. Child-facing details are Claude's call (memory `kid-ux-judgment`); anything touching content, data or scope goes back to the maintainer.

## Decisions

1. **Trace is easier to see.** The letter's ink fills 80 % of the canvas (was 70 %). The band is `#C8DCF4` (was `#EEF3F9`, near-invisible on white); this changes plan 09 decision 6's band colour only. A dot stroke's numbered coin sits beside its dot, pushed away from the letter, and steps clear of every other coin and dot (`coinSpots`, `TraceCoinsTest`), so ت ث ش and the hamza no longer hide the dots to tap.
2. **What kids tap, answers.** Tapping the Lesson's big letter or picture row, the Look & listen letter tile, picture and word, and Home's letter and picture plays its sound (letter or word) and gives a short wiggle (`Wiggle`, Look & listen's old picture wiggle). An earned sticker says its stage's letters and wiggles; an empty slot does nothing. On the Map, a finished stage card replays: it opens Meet for one of its letters at random, never the one picked last (`replayLetter`); the 40 dp chips stay too small to be targets. The step pills become circles on a track with labels under them, so nothing looks pressable.
3. **Preschool Home has one main action.** "Let's go!" starts today's letter at Meet. The Learn/Trace/Play tiles and the stage strip's "Map >" link are removed; the strip stays as a progress display and the Map tab reaches the Map. Reason: about ten targets with four duplicates, and the Play tile skipped Trace, so a letter could be learned without tracing. This departs from the approved `Main.html` and plan 07's "one row of 3 tiles".
4. **Quiz.** The right answer never sits in the same position on two rounds in a row (`QuizRoundTest`). The footer "Listen, then tap a picture" is plain text, not a white pill that looks like a button. The heading "Which one starts with" does not break mid-phrase.
5. **Paint and Find it.** Paint: the crayons sit right under the canvas (the ~145 dp gap goes) and the Next button no longer covers the canvas corner: it appears centred under the crayons in a slot kept for it. Find it on the phone: the picture sits beside the word, not over it, so it takes the tile's height (about 96 dp became 180 dp); the tablet keeps picture over word.
6. **Map and Reward.** A locked stage's press ripple follows the card's rounded corners and the card wiggles. Reward: the stage sticker is the big, animated hero (260 dp, springs in, bobs, a tap wiggles it and says its letters); its name and "Stage N complete · added to your sticker book" sit under it. The sticker card, "NEW STICKER" and "You learned 4 new letters" are removed.

## Out of scope

Audio recordings (maintainer); stroke-order sign-off (`docs/review/strokes.md`); the launcher icon branch; tablet and Early reader review; the parent gate's strength.
