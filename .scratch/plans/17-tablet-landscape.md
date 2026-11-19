# 17 Tablet landscape layouts

Started 2026-11-19 by the maintainer ("start with the other resolution changes in a different branch"), reversing the deferral in plan 16. HANDOFF gives only Toddler Find a tablet layout; every other screen uses the phone layout, centred, which leaves a 600 dp-tall landscape tablet with a small canvas. One branch per screen; prototype first (`design.sh`), then Compose; gates `check.sh`, `test-fast.sh`, `a11y.sh` on `huroofi_tablet_small`.

## Decisions

1. **Which windows:** Find's existing rule, now shared as `useWideLayout` (`ui/components/LiveConstraints.kt`): 840 dp or wider and wider than tall. Tablet portrait and phones keep the phone layout.
2. **Prototype:** `S.device === 'tablet'` draws a screen's wide layout when the route is in `TABLET_ROUTES` (`shell.js`); a `*Tablet` static key is snapshotted at 1180 × 820 (`design.sh`).
3. **Order, biggest gain first:** Trace (slice 1), then Toddler Paint, Meet, Quiz, Look & listen, Toddler home, Home / Map. Each slice is its own branch and merge.

## Slices

1. **Trace** (`feat/tablet-trace`). The canvas fills the left at full height; the hint, paints, Again and Next stack in a 380 dp column on the right, Again/Next at its bottom (`TraceTablet.html`). The step bar spans the top. Practice uses it too. A large text size scrolls the side column (`FillColumn` with a spacer).
   - Also: once a letter is done, every coin shows its star. A rotation refits the ink and re-checks it, which could leave a stroke just under its share and show its coin behind the "You traced" state.
2. **Finger paint** (`feat/tablet-paint`). The canvas fills the left at full height; the crayons and wipe, or once done "Paint it again" and the ring, sit centred in a 320 dp column on the right (`ToddlerPaintTablet.html`). The header (Home, prompt, lock) spans the top. `ToddlerScaffold(wide = true)` drops the 480 dp phone column so the next Toddler screens can reuse it.
3. **Meet** (`feat/tablet-meet`). The letter card fills the left at full height, the letter up to 260 dp; the word card, the shapes card (Early reader) and "Trace it" stack in a 380 dp column on the right, "Trace it" at its bottom (`LessonTablet.html`). A large text size scrolls the column.
   - Also: a shape cell grows with the text size instead of clipping its label ("End", "Alone" were cut at 200 %, on phones too).
4. **Picture game** (`feat/tablet-quiz`). All the pictures sit in one row (3 in Preschool, 4 in Early reader), up to 160 dp; once solved the word goes under the picture (`QuizTablet.html`). Close, dots, question and foot keep their phone order across the full width. `CappedWidth(wide = true)` now drops the 480 dp column for any screen; `ToddlerScaffold` uses it.
5. **Look & listen** (`feat/tablet-cards`). The card fills the middle at full height; the previous arrow sits on its left and the next arrow on its right, each with its neighbour's picture under it (`ToddlerCardsTablet.html`).
6. **Toddler home** (`feat/tablet-toddler-home`). Leo and the question on one line across the top; the three tiles stand side by side at full height, picture on top (grown up to 1.6×), word and play circle under it (`ToddlerHomeTablet.html`).

## Built

- Slice 1, `feat/tablet-trace`, merged to `main`. `check.sh`, `test-fast.sh` and `design.sh` PASS (`FindLayoutTest` now calls `useWideLayout`; new reference screen `TraceTablet.html`).
  - Emulator `huroofi_tablet_small` (960 × 600 dp), Preschool, Trace أ:
    - Landscape: the canvas is about 500 × 470 dp (was 440 × 234 in the phone layout). Tracing works and finishes; `a11y.sh` PASS (8 targets, min 64 dp).
    - At 200 % text the side column still fits, with Again/Next on screen.
    - Rotated to portrait mid-round: the phone layout, the ink and the done state kept; after the star fix, every coin is a star.
  - The tablet is back to auto-rotate, 100 % text, Toddler mode and a 60-minute limit (set to Off for the walk).
- Slice 2, `feat/tablet-paint`, merged to `main`. `check.sh`, `test-fast.sh` and `design.sh` PASS (new reference screen `ToddlerPaintTablet.html`).
  - Emulator, Toddler, Finger paint: the canvas is about 570 × 465 dp beside the crayons; painting ر finished it and the next letter started by itself; ض showed the green frame, stars, Again and the ring beside the canvas. `a11y.sh` PASS (7 targets, min 64 dp). Portrait keeps the phone layout; at 200 % text the prompt grows and everything still fits.
- Slice 3, `feat/tablet-meet`, merged to `main`. `check.sh`, `test-fast.sh` and `design.sh` PASS (new reference screen `LessonTablet.html`).
  - Emulator, Early reader, Meet أ: the letter card is about 500 × 470 dp beside the word and shapes cards; `a11y.sh` PASS (5 targets, min 64 dp). Portrait keeps the phone layout. At 200 % text the shape labels show whole and the side column scrolls to "Trace it".
- Slice 4, `feat/tablet-quiz`, merged to `main`. `check.sh`, `test-fast.sh` and `design.sh` PASS (new reference screen `QuizTablet.html`).
  - Emulator, Early reader, after tracing أ (100 %): four tiles of about 215 × 290 dp in one row; a wrong pick (sparrow) showed "Almost! Try again" and dimmed it; the lion then went green with أسد under it and "Great job! Next". `a11y.sh` PASS (6 targets, min 64 dp). Portrait keeps the 2 × 2 phone layout; 200 % text still fits.
- Slice 5, `feat/tablet-cards`, merged to `main`. `check.sh`, `test-fast.sh` and `design.sh` PASS (new reference screen `ToddlerCardsTablet.html`).
  - Emulator, Toddler, Look & listen: the card is about 440 × 300 dp between the arrows; Next went from أسد to بطة and the peeks followed (lion left, apple right). `a11y.sh` PASS (7 targets, min 64 dp). Portrait keeps the phone layout; 200 % text fits.
- Slice 6, `feat/tablet-toddler-home`, merged to `main`. `check.sh`, `test-fast.sh` (new `fitScale` grow test) and `design.sh` PASS (new reference screen `ToddlerHomeTablet.html`).
  - Emulator, Toddler home: three tiles of about 300 × 450 dp side by side; Find it opened from its tile. `a11y.sh` PASS (5 targets, min 64 dp). Portrait keeps the phone layout; 200 % text fits.
