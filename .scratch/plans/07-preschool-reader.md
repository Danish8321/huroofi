# 07 Preschool / reader flow (outline) — HANDOFF step 5

Reference: `Main`, `StageMap`, `Lesson`, `Trace`, `Quiz`, `Reward`. Bottom nav: Home, Map, Stickers, Parents (Parents goes through the gate).

Slices, in order:

1. **Home + bottom nav + Map**: stage map from `stages` colours, locked stages show colour + lock icon, unlock rule from plan 03.
2. **Lesson**: auto-plays letter then word on open, replay via sound button or picture. Letter-shapes row (alone/start/middle/end) **Reader only**. Gap: the JSON has only the isolated letter; shapes need data (either added to JSON + model together, or Unicode presentation forms). Decide in grill.
3. **Trace**: stroke paths per letter are **not in the pack**. Need path data (28 letters × strokes), source/authoring decision before this slice. Forgiving tolerance ~30 dp, auto-detect completion, "I did it!" fallback.
4. **Quiz**: Preschool 2–3 options, Reader 4. Wrong = gentle sound + correct picture wiggles, soft orange "Almost!" (Preschool+ only). Auto-check after trace per HANDOFF §3 note. Pure `QuizRound` logic tested.
5. **Reward + stickers**: after the 4th letter of a stage: stars, sticker, cheer; writes `completed_letters`/`stickers`, unlocks next stage. Stickers tab lists earned ones.

Risks: trace path data (3), vowel/shape data (2). Both are content inputs I cannot invent; they block their slices only.
