# Trace and Paint guides tiny for short letters

Status: open · Type: bug · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Guide glyph is sized by font size, not by its drawn bounds. د ر ز و fill about a third of the canvas.

## Expected
Scale the glyph so its drawn bounds fill the guide share of the canvas, for every letter, in Trace and Paint.
