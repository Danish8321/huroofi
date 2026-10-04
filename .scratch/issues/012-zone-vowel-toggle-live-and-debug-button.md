# Vowel toggle live though inert; debug gallery visible

Status: fixed · Type: polish · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Show vowel marks can be switched though it does nothing yet. Component gallery (debug) shows in debug test APKs.

## Expected
Disable the toggle until vowelled forms ship. Debug button: acceptable in debug builds; release has none.

## Resolution
Vowel toggle locked off and faded with "Coming soon" caption until vowelled forms ship; stored value untouched. Debug gallery button stays in debug builds only.
