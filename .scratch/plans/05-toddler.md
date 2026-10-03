# 05 Toddler mode (built) — HANDOFF step 3

**Status (2026-10-03):** slices 1–6 built on `feat/toddler-mode`; `check.sh`, `test-fast.sh` and `contract.sh` pass.
Open:
- Visual behaviour (layout, animation, touch feel, audio timing) is unverified: no device or emulator was used.
- Generated captions (`ToddlerPhrases`) wait for native-speaker review: `docs/review/toddler-phrases.md`.
- No daily time limit until plan 06 (decision 15).
- Paint shows four crayons; they are treated as a palette, not answer choices, so the ≤ 3 choices rule does not apply to them (decision 2).

Each item is a vertical slice: UI + state + sound + progress write + tests at the tiers it touches.

Reference: `ToddlerHome`, `ToddlerCards`, `ToddlerFind`, `ToddlerFindTablet`, `ToddlerPaint`, `ParentGate`.

Slices, in order (each ends with something a person can tap through):

1. **Parent gate first** (it guards everything else): 3-second press-and-hold with filling ring, release resets, then "Open Parent zone". Pure state machine `HoldGate` (idle/holding/progress/complete), tested with a fake clock. Destination is a stub until plan 06.
2. **Toddler home**: 3 big activities, spoken "ماذا نلعب؟" on open, only a Home button and a small lock (`ParentLock`). Start destination when `mode == TODDLER`.
3. **Look & listen**: swipeable pager over 28 cards, big prev/next arrows (≥ 84 dp), tap picture = wiggle + word clip.
4. **Where's the…?**: 2 pictures. Pure `FindRound` logic (pick target + 1 distractor, never repeat last, tap result → NUDGE/CORRECT). Prompt on open and via sun button. Wrong = nudge, right one wiggles, never red/X. Right = stars + green Next.
5. **Finger paint**: 30 dp brush over dashed letter, 4 colours + wipe, selected crayon has ring, star after threshold of painted distance. Nothing can be wrong. Painting model (strokes, threshold) pure and tested.
6. **Child-safety plumbing**: system Back → in-app Home (never exit); exit only via gate.

Rules to assert in tests: no screen offers > 3 choices, every interactive node ≥ 64 dp, no red colour used on toddler screens.

## Decisions (grill, 2026-10-03)

1. `ParentLock` keeps the prototype's 44 dp visible circle with a 48 dp hit area. It is the one named exception to the 64 dp toddler target rule (it is the control a toddler should not hit). The "every interactive node ≥ 64 dp" test excludes it by name and asserts ≥ 48 dp for it.
2. Coral crayon `#FF7A59` stays. "No red" means no red for feedback or status. The four crayon colours (coral, blue, green, purple) are an allow-list in the test.
3. Finger paint picks a random letter, never the same as the last one. Letter outlines are generated, not hand-drawn. Home tile keeps its decorative ب. (Which letters are eligible: see decision 4.)
4. All 28 letters are available in every toddler activity from day one. No stage gating, no completion, no progress writes from toddler activities. Paint outlines come from the Noto Naskh glyph path drawn as a dashed stroke at runtime (no outline data).
5. Find it: a set is 3 rounds, header stars = rounds solved in the current set. After the third, Next starts a fresh set and stars reset. Endless, no result screen. Each round: random target + 1 random distractor from the other letters; target never equals the previous target; a wrong tap never removes a star.
6. System Back on Toddler Home is swallowed (no sound, no animation). Back on any other toddler screen, or on the gate, returns to Toddler Home. No exit path except through the gate.
7. The gate's unlocked state shows two buttons: "Open Parent zone" and "Close Huroofi" (`finishAffinity()`). No cancel button; Back returns to Toddler Home.
8. Look & listen: on arriving at a card (swipe or arrow) the letter clip plays, then the word clip. A picture tap = wiggle + word clip only. A new arrival cuts any clip still playing.
9. Look & listen: cards in alphabet order, wrap-around at both ends, no page dots. Bottom row is just prev and next (96 dp).
10. Paint: when the star appears, a green 96 dp Next appears with it (same control as Find), over the bottom-right of the canvas card. Painting stays possible after the star; wipe clears the same letter. Next draws a new random letter (never the last) and clears the canvas.
11. Paint star threshold: 400 dp of total stroke length, measured as distance (not pointer events), counted anywhere on the canvas (on or off the letter). Wipe resets the distance and clears the star.
12. Toddler Home: "ماذا نلعب؟" plays every time Home appears (start, Home button, Back from a screen). Tapping the speech bubble replays it. Starting an activity cuts it.
13. Find sounds: wrong tap = `boing` + nudge, then (after the 0.3 s delay) the spoken prompt replays as a hint; repeated wrong taps replay only the boing. Right tap = random `praise_1..3` (never same as last) + stars pop; the 3rd star of a set also plays `cheer`.
14. Plan 05 is phone layouts only. Until plan 08, wider windows get a centred column, max content width 480 dp (fallback, not a design). All tablet work, including the `ToddlerFindTablet` pattern, is plan 08 slice 1.
15. Daily-limit counting and enforcement are both plan 06. Plan 05 keeps every toddler screen in one `NavHost` / one activity so 06 can intercept in one place. Known gap until 06 lands: toddler play is not time-limited.
