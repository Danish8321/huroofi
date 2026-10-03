#!/bin/sh
# Create silent placeholder audio for every clip the app can ask for (names: plan 03).
#   tools/make-placeholders.sh          create missing files only; real recordings are never overwritten
#   tools/make-placeholders.sh --list   list files that are still placeholders (identical to tools/silent.mp3)
cd "$(dirname "$0")/.." || exit 1
JSON=data/letters.json
OUT=app/src/main/assets
SILENT=tools/silent.mp3

paths() {
  grep -o '"audio/[^"]*"' $JSON | tr -d '"'
  grep -o '"picture": *"[^"]*"' $JSON | sed 's/.*: *"\(.*\)"/audio\/prompts\/where_is_\1.mp3/'
  grep -o '"name_latin": *"[^"]*"' $JSON | sed 's/.*: *"\(.*\)"/audio\/prompts\/colour_\1.mp3/'
  echo audio/prompts/what_shall_we_play.mp3
  echo audio/prompts/time_to_rest.mp3
  echo audio/prompts/which_starts_with.mp3
  echo audio/sfx/praise_1.mp3
  echo audio/sfx/praise_2.mp3
  echo audio/sfx/praise_3.mp3
  echo audio/sfx/boing.mp3
  echo audio/sfx/cheer.mp3
}

if [ "$1" = "--list" ]; then
  for p in $(paths); do
    [ -f "$OUT/$p" ] && cmp -s "$SILENT" "$OUT/$p" && echo "$p"
  done
  exit 0
fi

n=0
for p in $(paths); do
  if [ ! -f "$OUT/$p" ]; then
    mkdir -p "$OUT/$(dirname "$p")"
    cp "$SILENT" "$OUT/$p"
    n=$((n + 1))
  fi
done
echo "make-placeholders: created $n placeholder files"
