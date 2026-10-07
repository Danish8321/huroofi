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
