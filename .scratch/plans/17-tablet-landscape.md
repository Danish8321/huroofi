# 17 Tablet landscape layouts

Started 2026-11-19 by the maintainer ("start with the other resolution changes in a different branch"), reversing the deferral in plan 16. HANDOFF gives only Toddler Find a tablet layout; every other screen uses the phone layout, centred, which leaves a 600 dp-tall landscape tablet with a small canvas. One branch per screen; prototype first (`design.sh`), then Compose; gates `check.sh`, `test-fast.sh`, `a11y.sh` on `huroofi_tablet_small`.

## Decisions

1. **Which windows:** Find's existing rule, now shared as `useWideLayout` (`ui/components/LiveConstraints.kt`): 840 dp or wider and wider than tall. Tablet portrait and phones keep the phone layout.
2. **Prototype:** `S.device === 'tablet'` draws a screen's wide layout when the route is in `TABLET_ROUTES` (`shell.js`); a `*Tablet` static key is snapshotted at 1180 × 820 (`design.sh`).
3. **Order, biggest gain first:** Trace (slice 1), then Toddler Paint, Meet, Quiz, Look & listen, Toddler home, Home / Map. Each slice is its own branch and merge.

## Slices

1. **Trace** (`feat/tablet-trace`). The canvas fills the left at full height; the hint, paints, Again and Next stack in a 380 dp column on the right, Again/Next at its bottom (`TraceTablet.html`). The step bar spans the top. Practice uses it too. A large text size scrolls the side column (`FillColumn` with a spacer).
   - Also: once a letter is done, every coin shows its star. A rotation refits the ink and re-checks it, which could leave a stroke just under its share and show its coin behind the "You traced" state.

## Built

- Slice 1, `feat/tablet-trace`, merged to `main`. `check.sh`, `test-fast.sh` and `design.sh` PASS (`FindLayoutTest` now calls `useWideLayout`; new reference screen `TraceTablet.html`).
  - Emulator `huroofi_tablet_small` (960 × 600 dp), Preschool, Trace أ:
    - Landscape: the canvas is about 500 × 470 dp (was 440 × 234 in the phone layout). Tracing works and finishes; `a11y.sh` PASS (8 targets, min 64 dp).
    - At 200 % text the side column still fits, with Again/Next on screen.
    - Rotated to portrait mid-round: the phone layout, the ink and the done state kept; after the star fix, every coin is a star.
  - The tablet is back to auto-rotate, 100 % text, Toddler mode and a 60-minute limit (set to Off for the walk).
