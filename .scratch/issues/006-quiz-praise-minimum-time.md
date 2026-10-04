# Right-answer state can flash under a second

Status: open · Type: polish · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Next round starts after praise clip + 600 ms; placeholder clips are near silent so the green state lasts < 1 s.

## Expected
Minimum time on the right-answer state regardless of clip length.
