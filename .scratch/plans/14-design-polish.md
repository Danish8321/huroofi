# 14 Design polish — fill the screen, speak the hint, calm motion, vibration

Approved 2026-10-07 by the maintainer ("all recommendation which makes app engaging and easy for learning arabic alphabets") after the component review (`/ui-ux-pro-max`, emulator `huroofi_phone`: Home, Map, Stickers, Meet, Trace, gate, Parent zone, Toddler home, Look & listen, Find, Paint). Branch `feat/design-polish`, one commit per decision.

## Decisions

1. **Fill spare height.** On a tall phone the empty band below the content goes to the content: Find's two picture cards, Paint's canvas and Preschool Home's Today's letter card grow into it (Meet already does, plan 09). Growth is capped so tablets keep their layouts, and text size 1.3x must not clip. The Parent gate stays as is (a grown-up screen).
2. **Trace hint speaks.** Trace's lion bubble gets the speaker icon of the Toddler home bubble; tapping the bubble replays the hint. On entry Trace plays the letter, then the hint, then the demo hand starts (sound first, plan 09 decision 4). New clip `audio/prompts/trace_hint.mp3`, silent until recorded. Applies to Trace practice too.
3. **Calm motion.** When the phone's "Remove animations" setting is on, decoration stops: no confetti, no wiggle (a tap still plays its sound), no sticker spring or bob, and the Map jumps to the current stage instead of scrolling. The demo hand still runs, because it teaches the stroke.
4. **Vibration** (glossary). A Parent-zone switch, on by default (key `haptics_enabled`). With it on: a light tick when a Trace stroke is finished; a confirm buzz for a traced letter, a right Quiz or Find answer, a Paint star and a new sticker. Nothing on a wrong answer (no fail states). Compose haptics, no new dependency.
5. **Trace layout.** "Again" is centred under the paints instead of sitting alone at the left.

Parked as issues (they need art or a content review, not code): sticker silhouettes, issue 015 (would change the glossary's "gentle empty slots"); concrete words for Toddler mode, issue 016.

Grilled 2026-10-07: Q1 scope 1–5, with 6 and 7 parked as issues. The maintainer then delegated the remaining child-facing choices ("all recommendation"): order letter → hint → demo; demo hand exempt from calm motion; vibration on by default, never on a wrong answer.

## Out of scope

Quiz, Reward and Rest layouts (not reviewed this round); dark mode; new art; recording the hint clip (maintainer).

## Built

On `feat/design-polish`: decision 1 `b00ebbc`, 2 `83dccf3`, 3 `5d3d531`, 4 `d41b5f1`, 5 `e1a6fd4`. Gates `check.sh`, `test-fast.sh`, `contract.sh` pass on each.

- Decision 1, `huroofi_phone`: Find tiles and Paint canvas now reach the reserved Next slot; Home's card takes the spare height (capped 460 dp) with the letter row centred, no clipping at text size 1.3x; `a11y.sh` PASS on Home. `huroofi_tablet` landscape Home fits without scrolling. Home's card cannot grow sideways (letter and picture already fill its width), so the gain is height around the row, not bigger art.
- Decision 2, `huroofi_tablet`: bubble shows the speaker and is a 64 dp+ button ("Hear the hint"); `a11y.sh` PASS. Order letter, hint, demo is by code only: `trace_hint.mp3` is a silent placeholder (added to `tools/make-placeholders.sh` and `docs/review/toddler-phrases.md`).
- Decision 3: Compose already snaps wiggle, sticker spring and bob when the animator scale is 0, and the Map already jumped (`scrollTo`). Fixed the two gaps: the demo hand now runs at real speed (seen mid-stroke on `huroofi_tablet` with all animation scales 0) and Reward drops confetti. Reward under calm motion not walked on the emulator.
- Decision 4, `huroofi_tablet`: Vibration switch in the Parent zone (`a11y.sh --parent` PASS); tracing alif logged a SEGMENT_TICK for stroke 1 and a CONFIRM for the letter in `dumpsys vibrator_manager`; with the switch off a full trace logged none. Quiz, Find, Paint star and Reward buzz are by code and `BuzzTest` only.
- Decision 5, `huroofi_tablet`: Again centred (x 1160–1400 of 2560).

Not checked: `huroofi_tablet_small`, real devices.
