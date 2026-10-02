# 03 Content layer, audio, progress

Goal: everything the screens will read or play, behind small interfaces. Slice: from `letters.json` and DataStore up to a `ViewModel`-ready repository, tested on the JVM. Depends on 01.

## Contract

`data/letters.json` is the contract between content and app (`{letters[28], stages[7]}`). Rules:
- Kotlin models `@Serializable Letter`, `Stage`, `LetterAudio`, `StageColors` mirror the JSON exactly. They are the only place JSON shape is declared.
- Decoder uses **strict** mode (`ignoreUnknownKeys = false`). A JSON change that the models do not know fails the contract test, forcing model + JSON to change in the same commit.
- No letter lists in code (CLAUDE.md rule). Index, stage, order all come from JSON.

## Content (`data.content`)

- `ContentRepository { letters: List<Letter>; stages: List<Stage>; letter(index); lettersInStage(n) }` loaded once from `assets/letters.json` through an `AssetReader` interface (fake in tests).
- Drawable lookup: `Letter.picture` → `R.drawable.pic_<picture>`; `card_image` → `card_<nn>_<name>`. Resolved by name at runtime through a single `ResourceResolver`; missing resource is a test failure, not a runtime crash (contract test iterates all 28).

## Audio (`audio`)

- `interface SoundPlayer { suspend fun play(clip: Clip); fun stop(); fun release() }` where `Clip` is a value type over the asset path (`audio/letters/01_alif.mp3`, `audio/words/01_lion.mp3`, plus prompts below).
- Implementation `AndroidSoundPlayer` on platform `MediaPlayer`/`SoundPool` (no new dependency). **Missing file = silent no-op**, never an error, so the app runs before recordings exist.
- Respects the Parent-zone "voice" toggle (reads from progress/settings store) and plays one clip at a time.
- Placeholder files: one minimal silent MP3 committed under `tools/silent.mp3` and `tools/make-placeholders.sh` copies it to every path in `letters.json` plus the prompt list. Placeholders are generated, listed in `audio/README.md`, and flagged so they get replaced by real recordings (HANDOFF §4).
- Extra clip names proposed (no names given in HANDOFF; need your OK): `audio/prompts/where_is_<picture>.mp3` ("أين البطة؟" style, one per picture), `audio/prompts/what_shall_we_play.mp3` ("ماذا نلعب؟"), `audio/prompts/colour_<letter_name>.mp3` ("لوّن الباء"), `audio/sfx/praise_1..3.mp3`, `audio/sfx/boing.mp3`, `audio/sfx/cheer.mp3`.

## Progress and settings (`data.progress`)

DataStore Preferences, one store, keys documented here and treated as the "schema" (versioned with `schema_version`, never renamed in place; a key change = new key + migration reading the old one):

| Key | Type | Default |
|---|---|---|
| `schema_version` | Int | 1 |
| `mode` | String enum `TODDLER/PRESCHOOL/READER` | TODDLER |
| `completed_letters` | Set<String> of letter indexes | empty |
| `stickers` | Set<String> stage numbers earned | empty |
| `voice_enabled` | Boolean | true |
| `harakat_enabled` | Boolean | false (confirm default) |
| `daily_limit_minutes` | Int 5–60 step 5 | 20 |
| `usage_day` / `usage_seconds` | String ISO date / Int | today / 0 |
| `offline_pack_ready` | Boolean | false |

`ProgressRepository` exposes `Flow`s plus `suspend` writers; derived rules live in pure functions: `unlockedStage(completed)` (stage n+1 opens when all 4 letters of stage n are complete), `isStageComplete`, `limitReached(usage, limit)`, and daily rollover on date change. All clamped to valid ranges at the write boundary.

Child data stays on device. No ID, no analytics, no network.

## Tasks (each: test first)

1. Models + strict decoder + contract test against the real `letters.json` (28 letters, 7 stages × 4, all card/picture/audio refs consistent, every stage colour valid hex).
2. `ContentRepository` + `AssetReader` fake tests.
3. Pure progress rules (unlock, limit, rollover, clamping) tests.
4. `ProgressRepository` over DataStore on a JVM temp file tests.
5. `SoundPlayer` interface, `Clip`, placeholder script, `AndroidSoundPlayer`. Player logic (single-clip, voice toggle, missing file) behind a fake backend so it is unit-testable; the real MediaPlayer path is device-only → reported as unverified.
6. Wire through a manual `AppContainer` (no DI library).

## Verification

`test-fast.sh` + `contract.sh` green. Real audio playback = device only, not claimed.
