# 13 Parent options — unlock all, trace only, start over, shorter hold

Approved 2026-10-07 by the maintainer ("Go ahead") after choosing each option. Branch `feat/parent-options`, one commit per decision.

## Decisions

1. **Shorter parent gate hold.** The hold is 2 s (was 3 s). The gate text says "2 seconds". Departs from HANDOFF and the project `CLAUDE.md` ("3-second hold"); `CLAUDE.md` is updated.
2. **Start over.** The Parent zone's last section has "Start over". A confirm step ("Start over? This clears every learned letter and sticker. It can't be undone.", Cancel / Start over) clears learned letters and stickers; the Map returns to stage 1. Mode, the switches, sound, play time and today's usage stay.
3. **Unlock all letters.** Parent zone → Lessons → switch, off by default (key `unlock_all`). When on, every stage that is neither finished nor current is *open* on the Map: full colour, no lock; tapping it opens Meet for its first unlearned letter. Learned letters, stickers, Rewards and Home's Today's letter work as before. Turning it off brings the locks back and keeps progress.
4. **Trace only.** Parent zone → Lessons → switch, off by default (key `trace_only`), for Preschool and Early reader. When on, Home's "Let's go!" and the Map open Trace directly; finishing the trace learns the letter (sticker and Reward as usual) and the next letter opens in Trace. The step track is hidden and Back goes Home. Trace still plays the letter sound on entry.

## Out of scope

A browse-all-letters screen; practice tracing without progress; resetting settings.
