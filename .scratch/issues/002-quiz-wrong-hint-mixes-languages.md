# Wrong-answer hint pairs English word with Arabic letter

Status: open · Type: bug · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Hint reads "Lemon starts with ل". "Lemon" starts with L, so the line teaches a false link.

## Expected
Hint uses the Arabic picture word: "ليمون starts with ل", first letter highlighted, like the right-answer panel.
