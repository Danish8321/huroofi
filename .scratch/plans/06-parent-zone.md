# 06 Parent zone (outline) — HANDOFF step 4

Reference: `Parents.html`. Behind the parent gate (plan 05 slice 1).

Slices:

1. **Zone shell + progress grid**: all 28 letters from `ContentRepository`, completion from `ProgressRepository`.
2. **Mode switch** (Toddler/Preschool/Reader): writes `mode`, next app start opens the matching home. Switching takes effect without restart if feasible; confirm.
3. **Toggles**: voice, ḥarakāt (changes which letter/word forms `ArabicText` shows — needs vowelled word data not in `letters.json`; **gap**, ask whether to add `word_ar_vowelled` to the JSON and to the model in the same commit).
4. **Daily time limit**: stepper 5–60 step 5, default 20. Usage meter ticks only while a child screen is foreground. At limit: friendly "time to rest" screen (no scary UI), gate still leads to the zone. Pure `limitReached` already in plan 03.
5. **Offline pack**: DECIDED (user): keep an "Offline mode" row in the zone. App is fully offline by design (no INTERNET permission), so the row is static: "All content is on this device." No download logic.

Parent zone uses `parentBg`, 16 sp minimum, AA contrast; it is for adults so English text is fine.
