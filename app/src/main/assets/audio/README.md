# Audio

Every file here is currently a silent placeholder (a copy of `tools/silent.mp3`). A native Arabic
speaker must record them (HANDOFF section 4); replace a file in place, same name.

- `letters/`, `words/`: names come from `data/letters.json`.
- `prompts/where_is_<picture>.mp3`: "أين البطة؟" style, one per picture.
- `prompts/colour_<letter_name>.mp3`: "لوّن الباء" style.
- `prompts/what_shall_we_play.mp3`: "ماذا نلعب؟".
- `sfx/praise_1..3.mp3`, `sfx/cheer.mp3`, and `sfx/boing.mp3` (gentle, never harsh).

`sh tools/make-placeholders.sh` creates missing files only. `sh tools/make-placeholders.sh --list`
prints the files that are still placeholders.
