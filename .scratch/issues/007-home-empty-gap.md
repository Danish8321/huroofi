# Home has an empty band above the bottom nav

Status: fixed · Type: polish · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
About 80 dp blank between stage strip and nav on the phone.

## Expected
Decide: distribute space or accept.

## Resolution
Spare Home height is shared between the three section gaps (min 22 dp each); small phones still scroll.
