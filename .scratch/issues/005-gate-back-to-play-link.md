# Parent gate lacks Back to play link

Status: fixed · Type: polish · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Prototype ParentGate.html has a "Back to play" link; app relies on system Back only.

## Expected
On-screen Back to play returning to the session home.

## Resolution
"Back to play" link at the bottom of the gate, always shown, returns to the session home. Plan 05 decision 7 amended.
