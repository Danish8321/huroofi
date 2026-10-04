# Quiz tiles show no picture word

Status: won't fix · Type: bug · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Plan 07 decision 4: options are picture + picture word. Tiles show the picture only (Preschool and Early reader).

## Expected
Each tile shows its picture word in Noto Naskh under the picture, first letter highlighted like Home/Lesson.

## Resolution
Picture only stays, as in `Quiz.html`: the word would give the answer away. Plan 07 decision 4 amended to match.
