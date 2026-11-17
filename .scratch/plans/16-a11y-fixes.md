# 16 Accessibility fixes from the UI/UX review (2026-11-16)

Source: second-pass design and accessibility review, run on `huroofi_phone` and `huroofi_tablet_small` on 2026-11-14 and 2026-11-16. Each slice gets its own branch, gated by `check.sh` and `test-fast.sh`, plus an emulator walk and `a11y.sh` on the screens it touches. Slice 1 was built on "Move to next"; the open points in 2 and 3 were answered by the maintainer on 2026-11-16.

## Slices

1. **The UI never mirrors.** Branch `feat/a11y-ltr`.
   - Finding: with the app language set to Arabic, Home read "!Hi there", Trace's button read "First ق Finish", and the bottom bar and crayons reversed. HANDOFF §1 says the UI is English and left-to-right. A phone whose system language is Arabic, Urdu, Persian or Hebrew does the same.
   - Fix: `HuroofiTheme` provides `LayoutDirection.Ltr`. `MainActivity` has one `setContent`, which goes through `HuroofiTheme` (`HuroofiApp.kt:84`). There are no dialogs or popups. The Arabic rows keep their own `Rtl` (`LessonScreen.kt:290`, `MapScreen.kt:335`, `RewardScreen.kt:249`, `ParentZoneScreen.kt:581`).
   - `supportsRtl` stays on, so system bars and any future platform UI still follow the phone.

2. **The moving finger draws.** Branch `feat/a11y-fingers`.
   - Finding: Trace and Paint follow only the first finger down (`TraceScreen.kt:502`, `PaintScreen.kt:374–384`). A second finger already resting on the card left 0 px of ink, against 75,883 px without it.
   - Decided (maintainer, 2026-11-16): follow whichever finger moves. A finger that stays still leaves no ink and ticks no dots.
   - Fix: `Pen` (`ui/components/Pen.kt`) picks the finger that draws, and Trace and Paint both use it.
     - A finger becomes the pen once it moves past touch slop, and its stroke starts where it landed.
     - A lone finger that lifts without moving is a tap and leaves a dot, so dot strokes still work. A still finger that shared the screen leaves nothing.
     - While the pen is down, other fingers are ignored. Once it lifts, the next finger to move draws, within the same multi-finger gesture.

3. **Rotation and settings changes keep the round.** Branch `feat/a11y-config`.
   - Finding: rotating the tablet mid-paint swapped the letter and dropped the paint. A dark-theme switch wiped a half-traced letter.
   - Decided (maintainer, 2026-11-16): also cover `fontScale|density|locale`, so settings changes mid-round keep it too.
   - Fix:
     - `android:configChanges` covers orientation, screen size and layout, uiMode, fontScale, density, locale, layoutDirection, keyboard and navigation. The activity is no longer recreated, so every round keeps its state.
     - A resize refits the letter, so the ink moves with it. Trace remaps its ink with `refit` and replays it into the new check, so ticked dots stay ticked. Paint remaps its strokes in dp (`Painting.moved`); the painted distance stays.
     - `BoxWithConstraints`' `maxWidth`/`maxHeight` keep the old density after a Display size change, because the px constraints are unchanged and it is not re-measured. All six users now read `windowWidth`/`windowHeight` (`LiveConstraints.kt`).
   - Dropped: the letter index in `rememberSaveable`. The round state is a whole object, so process death still restarts the round.

4. **The countdown keeps its time with animations removed** (outline).
   - Finding: with "Remove animations" on, Find moved on 0.5 s after the right tap instead of after 5 s, and Paint jumped mid-stroke (`CountdownNext.kt:73–81`).
   - Fix: time the wait on the frame clock, as the parent gate's hold does. The prototype's calm-motion rule drops only confetti, wiggles and pulses.

## Later (needs the prototype first, then `design.sh`)

- Short windows: Lesson, Quiz and Trace minimum heights push the main button off-screen on a 600 dp-tall tablet and a 320 dp phone.
- Toddler layouts at 360 and 320 dp, and on the tablet in landscape: wrapping prompt, play circle over the labels, crayon and eraser row too wide. The same happens at a larger Display size (520 dpi on the phone): Paint's eraser and Trace's pink crayon are clipped at the right edge.
- Fix next, items 7–14: live regions, the gate's long-click action, missing labels, auto-advance under TalkBack and Arabic `localeList`, text at 150 % and above, the alif target, sub-16 sp text, and Paint's gate "Back to play".

## Needs the maintainer's decision

Easier tracing; a warning before Rest and a daily limit "off"; tablet landscape layouts; sound safety (mute detection, audio focus, keep-screen-on).

## Built

- Slice 1, `feat/a11y-ltr`, merged to `main`. `check.sh` and `test-fast.sh` PASS. No unit test was added: one would need a Compose UI test runner, and plan 00 decision 2 declined Robolectric.
  - Emulator `huroofi_phone`, app language set to Arabic, then restored:
    - Home: "Hi there!", with the bottom bar Home → Parents from left to right.
    - Lesson: back arrow on the left, step track Meet → Play.
    - Trace: "Finish ق first" in order, crayons orange → pink.
    - Map: stage chips still ق ك ل م from right to left.
  - `a11y.sh` PASS on all four (6, 5, 8 and 9 targets, min 64 dp).
  - No gate covers layout direction.
- Slice 2, `feat/a11y-fingers`, merged to `main`. `check.sh` and `test-fast.sh` PASS. New `PenTest`: a stroke starts where it landed, a lone tap makes a dot, a resting thumb leaves no ink and does not block, a second finger takes over after the pen lifts, and closing ends an open stroke.
  - Emulator `huroofi_phone`, Trace ق, the review's multi-touch scripts, ink counted in the crayon colour:
    - One finger: 77,071 px.
    - A thumb resting on the card: 76,094 px (it was 0 before the fix).
    - A thumb resting beside the card: 75,769 px.
    - A second finger landing mid-stroke, then a later stroke: 157,498 px, both strokes painted (the later one was blocked before the fix).
    - A lone tap on dot stroke 2 still turned it green.
  - `a11y.sh` PASS on Trace (8 targets, min 64 dp).
  - Paint uses the same `Pen` but was not walked on the emulator.
- Slice 3, `feat/a11y-config`, not merged. `check.sh` and `test-fast.sh` PASS. New tests:
  - `refitKeepsAPointOnTheSameSpotOfTheLetter`.
  - `inkRefittedToAnotherSizeKeepsItsFinishedStrokes`, over all 28 letters at a larger and a smaller fit. A smaller fit can finish one more stroke (ث's dots at 160 px), as fresh tracing there would.
  - Emulator `huroofi_phone`, same pid throughout (no recreation):
    - Trace ق: font 1.3 → 1.0 and density 520 → 420 keep the ink and the green dots. Before the remap, the dots reset after a font change.
    - Paint ب: font and density changes keep the paint on the same part of the letter.
    - Find: "Where's the strawberry" kept through the font and density changes.
    - Home: density 520 → 420 returns to the original layout. Before `windowHeight`, Trace stayed about 460 px short.
    - Dark and light switches kept ink and progress (checked before the remap).
  - Tablet-sized phone (density 160, sw ≈ 675 dp), rotation to landscape and back mid-round: Paint س keeps its paint on the letter, and Trace ق keeps its ink and green dots.
  - Not walked: the `huroofi_tablet_small` AVD itself, or a locale change since the remap.
