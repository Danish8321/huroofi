# Stroke order review

**Status: pending review.** Claude drafted `data/strokes.json` (plan 09 decision 2). Before release the maintainer or a teacher checks every letter below and signs it off. Until all 28 rows are signed, plan 09 stays "built, stroke order pending review".

## Fit to the font

On 2026-10-06 the points were snapped onto Noto Naskh Arabic Bold with `python tools/strokes_fit.py --write`: line strokes onto the glyph's centre line, dot strokes onto the centre of the letter's own dots. Start points, direction and order were kept. Run the script without `--write` to see how far each stroke sits from the centre line (needs Pillow and numpy on the developer machine). Stroke order still needs the sign-off below.

## How to open the sheet

The sheet reads the font and the JSON files, which browsers block from `file://`. From the repo root:

```
python -m http.server 8765
```

Then open <http://127.0.0.1:8765/docs/review/strokes.html>.

## What to check for each letter

- **Start:** the green **1** sits where a child is taught to begin the letter.
- **Direction:** each line runs from its numbered coin to its arrow, the way the pen moves.
- **Order:** the numbers follow the taught order. The body comes first; dots and hamza come after it.
- **Dots:** each dot is its own numbered stroke, and the count is right.
- **Fit:** every line stays inside the pale letter shape.

The app does not enforce order or direction (plan 09 decision 3); the numbers and the demo ball only teach them. A wrong start or order would still teach the wrong habit, so please mark anything that looks unusual.

## Least certain

Claude is least sure about these; please look at them first:

- **أ alif:** the hamza's path (head, then the tail to the left).
- **ك kaaf:** the small mark inside, drawn as one stroke after the body.
- **ه haa:** where it starts and how the eye is drawn.
- **ع ayn, غ ghayn:** the head goes out to the right before the lower curve starts.
- **ط taa, ظ zaa:** the loop and base come first, then the upright stroke from the top.

`strokes-sheet.png` beside this file is the sheet as drafted (2026-10-05).

## Sign-off

Write OK, or what to change, in the Check column, and your initials in Signed.

| # | Letter | Name | Check | Signed |
|---|---|---|---|---|
| 1 | أ | alif | | |
| 2 | ب | baa | | |
| 3 | ت | taa | | |
| 4 | ث | thaa | | |
| 5 | ج | jiim | | |
| 6 | ح | ḥaa | | |
| 7 | خ | khaa | | |
| 8 | د | daal | | |
| 9 | ذ | dhaal | | |
| 10 | ر | raa | | |
| 11 | ز | zaay | | |
| 12 | س | siin | | |
| 13 | ش | shiin | | |
| 14 | ص | ṣaad | | |
| 15 | ض | ḍaad | | |
| 16 | ط | ṭaa | | |
| 17 | ظ | ẓaa | | |
| 18 | ع | ʿayn | | |
| 19 | غ | ghayn | | |
| 20 | ف | faa | | |
| 21 | ق | qaaf | | |
| 22 | ك | kaaf | | |
| 23 | ل | laam | | |
| 24 | م | miim | | |
| 25 | ن | nuun | | |
| 26 | ه | haa | | |
| 27 | و | waaw | | |
| 28 | ي | yaa | | |
