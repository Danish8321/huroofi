# Lesson shapes row hidden behind Next button

Status: fixed · Type: polish · Found: regression pass 2026-10-04 on `main` 99c902d (phone emulator)

## Found
Early reader Lesson on a 1080x2400 phone: shapes grid sits under the fixed Next: Trace it button until scrolled.

## Expected
Shapes row visible without scrolling on a standard phone.

## Resolution
Early reader Lesson uses a smaller big letter (146 dp box, 120 sp) so the shapes row shows above "Next: Trace it" on a 1080x2400 phone. Preschool keeps 220 dp / 170 sp.
