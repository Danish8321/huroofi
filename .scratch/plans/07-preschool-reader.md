# 07 Preschool / reader flow — HANDOFF step 5

Status: **built 2026-10-03** (slices 1–6 on `feat/preschool-reader`, not merged). Emulator pass done on `huroofi_phone`: Preschool Meet → Trace → Play with 3 pictures; stage 1 finished into Reward, sticker in the book, finished stage on the Map; locked-card tap plays a clip and moves; Early reader shapes for ج (4) and د (Alone, End) and 4 pictures; Back from Reward → Map, Map and Stickers → Home; 5-minute limit → Rest. Real sound not checked (placeholder audio). Stage 7 colours moved off red. Task list: [07-preschool-reader-tasks.md](07-preschool-reader-tasks.md).

Reference prototypes: `Main`, `StageMap`, `Lesson`, `Trace`, `Quiz`, `Reward` in `design-reference/screens/`. Rebuild natively. Where a decision below disagrees with a prototype, the decision wins.

Glossary terms used here (see `CONTEXT.md`): Letter path, Learned letter, Learning letter, Today's letter, Stage complete, Sticker, Sticker book, Letter shape, Non-joining letter.

## Decisions

### 1. Letter path and "learned"
- One letter = Meet (Lesson) → Trace → Play (Quiz).
- The letter is learned (`markLetterComplete`) when Play's last round is answered correctly. Meet and Trace alone do not count.
- After Play, "Continue" opens Meet for the next learning letter. If that Play made the stage complete, it opens Reward instead.
- Home's Learn / Trace / Play tiles and the Map's play button open that step for Today's letter. No free letter choice.
- Today's letter = the learning letter; once all 28 are learned, a random learned letter (review). Review Play does not write anything new.
- Replay from the Parent-zone grid: out of scope.

### 2. Letter shapes (Lesson, Early reader only)
- Built in code from the letter plus tatweel (U+0640): alone `ب`, start `بـ`, middle `ـبـ`, end `ـب`. No JSON change.
- Grid RTL, cells Alone / Start / Middle / End. Non-joining letters (أ د ذ ر ز و) show only Alone and End; the other two cells are absent, not greyed.
- Never vowelled. Cells are not tappable.
- Preschool Lesson has no shapes grid.

### 3. Trace
- Guide = the letter's Naskh glyph, big and faded. No stroke-path data, no start dot or arrow. Helper bubble: "Trace the letter with your finger!"
- Auto-completes when ≥ 70 % of the glyph's sample points are within ~30 dp of the child's ink. Stroke order, direction and ink outside the letter are ignored. Dots count like the rest of the glyph.
- "I did it!" appears once any ink exists and also completes. "Again" clears the ink. 5 crayons = colour only.
- Same in Preschool and Reader. `cheer` on completion, then go to Play.
- Supersedes index decision 11 (Claude-authored stroke paths): none are needed.

### 4. Play (quiz)
- "Which one starts with [letter]?" with picture options (picture + picture word). Auto-plays the new `which_starts_with` clip, then the letter's clip. "Hear the question" button replays.
- Options: Preschool 3, Early reader 4.
- Distractors: random other letters from all 28, positions shuffled. Skip any distractor whose picture word starts with the target letter. Pure, unit-tested `QuizRound`.
- 3 rounds, all about Today's letter, fresh distractors and order each round. Progress = 3 dots.
- Wrong: `boing`, soft orange "Almost! Try again", correct picture wiggles, wrong option dims. No red, no X, no score. Tries are unlimited.
- Right: green, `praise`, "Great job!". After round 3 the letter is learned, then "Continue".

### 5. Reward, stickers, sticker book
- One Sticker per stage (7), named "<stage name> sticker". Drawn in code: round badge in the stage pastel and border colours with the stage's 4 letter pictures. No new art, no JSON change.
- Reward: confetti, always 3 stars, `cheer`, "Stage N complete!", "You learned 4 new letters", 4 letter tiles, the sticker card, "<next stage name> unlocked" (last stage: "All letters learned!"). One button: "Next stage" → Map.
- The sticker is awarded (`awardSticker`) in the same step that marks the stage's 4th letter learned, before Reward opens, so killing the app on the way never loses it. The next stage unlocks by the existing `unlockedStage` rule.
- Stickers tab = Sticker book: 7 slots; earned shows badge and name, unearned a soft pastel outline with "?". No lock, no grey, not tappable.
- No stars currency anywhere (no star pill, no "+12").

### 6. Vowelled forms
- Deferred to a later plan, once a native-reviewed vowelled list for the 28 picture words exists. Plan 07 adds no vowelled text.
- Parent zone: a muted line under the ḥarakāt toggle: "Vowel marks arrive in a later update."

### 7. Home and bottom nav
- Lion avatar + "Hi there!". No name, no star pill.
- Today card: "TODAY'S LETTER", big letter, Latin name, picture, picture word with its first letter highlighted, "Let's go!" → Meet.
- One row of 3 tiles: Learn, Trace, Play. No Songs.
- Stage strip: the open stage's 4 chips (learned green, learning white + blue ring, rest pastel). Tap → Map.
- Bottom nav: Home, Map, Stickers, Parents (active `#1F6FE0`). Parents opens the Parent gate; it is never a tab. Nav shows on Home, Map, Stickers only.
- Preschool and Reader start on Home; the `Placeholder` route is deleted.

### 8. Letter Map
- Vertical scroll opening at the current stage; dotted path drawn on a Compose `Canvas` behind 7 zig-zag cards. Subtitle "28 letters · 7 stages · finish one to unlock the next"; no star pill.
- Finished stage: green ring + check; tap does nothing. Current: 4 px ring in the stage border colour + play button → Meet for Today's letter. Locked: `#EEF3F9` + lock; tap = `boing` + lock wiggle.
- Chip palette identical to Home's strip; locked chips `#EEF3F9`.
- All 28 learned: every card finished, no play button.

### 9. Back, play time, sizes, Lesson sound
- Back (system and on-screen agree): Lesson → Home; Trace → Lesson; Play ✕ → Home; Reward → Map; Map / Stickers → Home; Home → Parent gate.
- Home, Map, Stickers, Lesson, Trace, Play, Reward join `ChildRoutes`: they count towards Daily play time and are replaced by the Rest screen at the limit.
- Every child touch target ≥ 64 dp, enforced by a spec test like `ToddlerSpec`.
- Lesson auto-plays letter then word on open; sound button or picture replays both. "Say it with me!" stays. Word row shows the picture word and its English meaning; no transliteration.
- New clip: `audio/prompts/which_starts_with.mp3` only (placeholder, `Clips.all`, phrase review doc).

## Notes added while writing the task list
Follow from the decisions above; none changes them.
- Lesson, Trace and Play carry the letter index in their route (`lesson/{index}`, `trace/{index}`, `quiz/{index}`; `reward/{stage}`), so a random review letter stays the same across the three steps.
- Sticker timing (decision 5): saved together with the stage's 4th learned letter, before Reward opens.
- Back on Home opens the Parent gate (decision 9), as leaving the app is guarded in Toddler mode.
- Stop and ask if rendering the glyph to trace sample points proves unreliable on the emulator; a fallback must be agreed, not invented.

## Slices
1. Home + Meet (Lesson). 2. Trace. 3. Play (quiz, marks learned). 4. Reward + Sticker book. 5. Letter Map. 6. Close out (spec sweep, ḥarakāt note, emulator pass, statuses). One commit per task, push per slice.

## Not in plan 07
Vowelled data; replay from the Parent zone; Songs; stars; per-shape sounds; stroke-order teaching; tablet layout (plan 08).
