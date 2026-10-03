# 06 Parent zone (approved) — HANDOFF step 4

Reference: `Parents.html`. Behind the parent gate (plan 05 slice 1). Task list: [06-parent-zone-tasks.md](06-parent-zone-tasks.md).

Each slice is vertical: UI + state + persistence + tests at the tiers it touches.

## Slices

1. **Zone shell + child card + letter grid**: replaces `ParentZoneStub`. Card: stage line, progress bar, learned / learning / to-go counts. Grid of all 28 letters from `ContentRepository`, coloured by `LetterProgress`.
2. **Learning mode switch**: Toddler / Preschool / Early reader. Writes `mode`; next app start opens that mode's home.
3. **Toggles + offline row**: voice & sounds, vowel marks, static offline row.
4. **Daily play time**: stepper in the zone, usage counting at the `NavHost`, Rest screen that replaces child screens at the limit.
5. **Close out**: rule sweep for the zone, statuses, gates.

## Decisions (grill, 2026-10-03)

1. **One child per device.** No name, photo or profile is stored; text says "your child". The prototype's "Sara" and "Switch child" are dropped. The card keeps the lion avatar, "Stage N of 7 · <stage name>", the progress bar and the counts.
2. **Learned / learning / to go** (glossary terms in `CONTEXT.md`). Learned = lesson completed (`completedLetters`). Learning = the first letter in alphabet order that is not learned within the open stage (`unlockedStage`); exactly one, or none when all 28 are learned. To go = the rest. Grid colours: learned solid green `#158048` with white glyph; learning white with a 3 dp `#1F6FE0` ring; to go `#EEF3F9` with `#4A5D7A` glyph. When mode is Toddler, a line under the counts says: "Toddler play isn't tracked. Letters count once lessons start (Preschool mode)."
3. **Grid is display only** in plan 06: no tap, no hint line. Plan 07 adds "replay lesson". No per-letter reset.
4. **Ḥarakāt toggle applies to lessons only.** Plan 06 stores the setting; subtitle "Adds vowel marks like بَ بِ بُ in lessons". No visible effect until plan 07, which adds the vowelled data to `letters.json` (native-reviewed). Toddler screens always show plain form.
5. **Voice & sounds toggle** keeps plan 03's behaviour: off = `SoundPlayer` plays nothing (speech and effects), effective immediately. Subtitle "Native-speaker audio for every letter".
6. **Daily play time.**
   - Counts seconds while a child screen is shown and the app is in the foreground. Gate, zone, Rest screen and the debug gallery do not count. Counting happens in one place at the `NavHost` (plan 05 decision 15); saved every 10 s and when the app goes to the background.
   - At the limit the current child screen is replaced by the **Rest screen** at once, sound cut, one calm spoken line plays.
   - Rest screen: lion art, "وقت الراحة" big, English caption "Time to rest. See you tomorrow!", the Parent lock only. No timer, countdown or red. Back is swallowed.
   - Parent override: gate → zone → raise the limit. While usage is under the limit the Rest screen sends the child home. No separate "extra time" control.
   - New day = local midnight (`usageForToday`). A Rest screen open across midnight stays until it is next shown or the app reopens.
   - Stepper 5–60 step 5, default 20, effective immediately.
   - New clip `audio/prompts/time_to_rest.mp3` ("وقت الراحة، إلى اللقاء غدًا!"): silent placeholder via `tools/make-placeholders.sh`, in the audio contract, listed for native review.
7. **Offline row is static**: "Offline pack" / "All content is on this device." No Download button (app has no INTERNET permission).
8. **Zone back button** ("Back to kid mode", 48 dp) and system Back return to this session's home. The mode subtitle "Changes what your child sees when the app opens" covers that a new mode applies on next start (plan 05 decision 12).
9. **Zone is for adults**: `parentBg`, English text, nothing under 16 sp, AA contrast, touch targets ≥ 48 dp (the prototype's 44 px stepper buttons become 48 dp). Content scrolls.
10. **Branch**: plan 05 merged to `main` (`1a4a1b0`); plan 06 is built on `feat/parent-zone` from `main`.
