# Unearned sticker slots leave a blank band

Status: fixed · Type: polish · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Empty name line keeps slot height, so unearned cards look half empty.

## Expected
Tighter unearned slot or a placeholder name line.

## Resolution
Unearned slots show the sticker name in muted semibold under the "?" outline.
