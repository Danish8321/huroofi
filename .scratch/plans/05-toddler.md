# 05 Toddler mode (outline) — HANDOFF step 3

Sharpen with `grill-with-docs` before starting. Each item is a vertical slice: UI + state + sound + progress write + tests at the tiers it touches.

Reference: `ToddlerHome`, `ToddlerCards`, `ToddlerFind`, `ToddlerFindTablet`, `ToddlerPaint`, `ParentGate`.

Slices, in order (each ends with something a person can tap through):

1. **Parent gate first** (it guards everything else): 3-second press-and-hold with filling ring, release resets, then "Open Parent zone". Pure state machine `HoldGate` (idle/holding/progress/complete), tested with a fake clock. Destination is a stub until plan 06.
2. **Toddler home**: 3 big activities, spoken "ماذا نلعب؟" on open, only a Home button and a small lock (`ParentLock`). Start destination when `mode == TODDLER`.
3. **Look & listen**: swipeable pager over 28 cards, big prev/next arrows (≥ 84 dp), tap picture = wiggle + word clip.
4. **Where's the…?**: 2 pictures. Pure `FindRound` logic (pick target + 1 distractor, never repeat last, tap result → NUDGE/CORRECT). Prompt on open and via sun button. Wrong = nudge, right one wiggles, never red/X. Right = stars + green Next. Tablet layout per `ToddlerFindTablet`.
5. **Finger paint**: 30 dp brush over dashed letter, 4 colours + wipe, selected crayon has ring, star after threshold of painted distance. Nothing can be wrong. Painting model (strokes, threshold) pure and tested.
6. **Child-safety plumbing**: system Back → in-app Home (never exit); exit only via gate.

Rules to assert in tests: no screen offers > 3 choices, every interactive node ≥ 64 dp, no red colour used on toddler screens.
