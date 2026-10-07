# 13 Parent options — unlock all, trace only, start over, shorter hold

Approved 2026-10-07 by the maintainer ("Go ahead") after choosing each option. Branch `feat/parent-options`, one commit per decision.

## Decisions

1. **Shorter parent gate hold.** The hold is 2 s (was 3 s). The gate text says "2 seconds". Departs from HANDOFF and the project `CLAUDE.md` ("3-second hold"); `CLAUDE.md` is updated.
2. **Start over.** The Parent zone's last section has "Start over". A confirm step ("Start over? This clears every learned letter and sticker. It can't be undone.", Cancel / Start over) clears learned letters and stickers; the Map returns to stage 1. Mode, the switches, sound, play time and today's usage stay.
3. **Unlock all** (glossary). Parent zone → Lessons → switch, off by default (key `unlock_all`). When on, every stage that is neither finished nor current is *open* on the Map: full colour, no lock; tapping it opens the full letter path (Meet) for its first unlearned letter, and learning counts as usual. Home, Today's letter and the learning letter keep following progress (grilling Q3). Completing any stage gives its sticker and Reward; the Reward's "unlocked" pill shows only when that completion moved progress on (the stage was the open stage), otherwise it is hidden and the button reads "Back to map" (Q4). Turning it off brings the locks back and keeps progress.
4. **Trace only = trace practice** (glossary). Parent zone → Lessons → switch, off by default (key `trace_only`), for Preschool and Early reader. When on, "Let's go!" opens Trace for Today's letter; after the cheer the next letter in alphabet order opens in Trace, wrapping after the last, taken only from open stages (all 28 with Unlock all) (Q2). A Map card opens Trace for its stage's first letter. A practice trace never makes a letter learned, never gives a sticker or Reward and saves nothing (Q1). The step track is hidden and Back goes Home. Trace still plays the letter sound on entry.

Grilled 2026-10-07 ("review the plans"): Q1 practice, not learning; Q2 alphabet order through open stages; Q3 progress order unchanged under Unlock all; Q4 pill only when progress moved.

## Out of scope

A browse-all-letters screen; practice tracing without progress; resetting settings.
