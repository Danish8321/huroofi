# 12 Trace hand — demo hand, green dots, ink inside the letter

Approved 2026-10-07 by the maintainer ("Yes go ahead and a paint/trace should be inside the letter only not outside the letter") after comparing Trace with a sample from another kids' app (a pointing hand demos each stroke; passed dots turn green; later strokes' dots stay faint; thick outlined letter). Branch `feat/trace-hand`, one commit per decision. Changes plan 09 decisions 4 and 6 and plan 05 decision 11 as noted below.

## Decisions

1. **Demo hand.** The demo ball becomes a pointing hand drawn in code (white glove, Navy edge, no new art): the fingertip rides the stroke, the hand sits below and to the right of it, and the short fading trail stays. Same timing and schedule (plan 09 decision 4). Used by Trace's entry demo, idle replay and "Show me how", and Paint's once-per-page demo. `DemoBall` is renamed `DemoHand`.
2. **Dots show progress.** A covered dot turns solid green and grows a little, instead of shrinking away (plan 09 decision 6), so the traced path stays visible. The next unfinished stroke's dots are full strength; every other stroke's open dots are faint (35 %). Order is still never enforced and any covered dot counts.
3. **Outlined letter.** Trace's pale band gets a 3 dp Navy outline drawn over the ink, as a colouring book.
4. **Ink stays inside the letter.** In Trace and Paint, ink and paint show only inside the letter's shape (out to its outline); a finger off the letter leaves nothing. Trace completion is unchanged (dots within 30 dp of the finger count). Paint's star counts only distance painted on the letter (within 8 dp of it), so scribbling beside the letter no longer earns it; this changes plan 05 decision 11 ("anywhere on the canvas"). Paint's dashed edge is drawn over the paint.

## Out of scope

The beach-ball start marker and a coins-only intro frame (the pulsing coin and sound-first demo already cover them); stroke-order sign-off; audio.
