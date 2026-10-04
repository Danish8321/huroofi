# Map subtitle orphan word and hard cut under header

Status: open · Type: polish · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Subtitle wraps leaving "next" alone; card above current stage is cut sharply by the header.

## Expected
Subtitle on one or two balanced lines; soft fade or padding under header.
