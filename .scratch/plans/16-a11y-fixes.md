# 16 Accessibility fixes from the UI/UX review (2026-11-16)

Source: second-pass design and accessibility review, run on `huroofi_phone` and `huroofi_tablet_small` on 2026-11-14 and 2026-11-16. Each slice gets its own branch, gated by `check.sh` and `test-fast.sh`, plus an emulator walk and `a11y.sh` on the screens it touches. Slice 1 was built on "Move to next"; the open points in 2 and 3 were answered by the maintainer on 2026-11-16.

## Slices

1. **The UI never mirrors.** Branch `feat/a11y-ltr`.
   - Finding: with the app language set to Arabic, Home read "!Hi there", Trace's button read "First ق Finish", and the bottom bar and crayons reversed. HANDOFF §1 says the UI is English and left-to-right. A phone whose system language is Arabic, Urdu, Persian or Hebrew does the same.
   - Fix: `HuroofiTheme` provides `LayoutDirection.Ltr`. `MainActivity` has one `setContent`, which goes through `HuroofiTheme` (`HuroofiApp.kt:84`). There are no dialogs or popups. The Arabic rows keep their own `Rtl` (`LessonScreen.kt:290`, `MapScreen.kt:335`, `RewardScreen.kt:249`, `ParentZoneScreen.kt:581`).
   - `supportsRtl` stays on, so system bars and any future platform UI still follow the phone.

2. **Any finger draws** (outline, sharpened before it starts).
   - Finding: Trace and Paint follow only the first finger down (`TraceScreen.kt:502`, `PaintScreen.kt:374–384`). A second finger already resting on the card left 0 px of ink, against 75,883 px without it.
   - Decided (maintainer, 2026-11-16): follow whichever finger moves. A finger that stays still leaves no ink and ticks no dots.

3. **Rotation and dark-theme switches keep the round** (outline).
   - Finding: rotating the tablet mid-paint swapped the letter and dropped the paint. A dark-theme switch wiped a half-traced letter.
   - Fix: add `android:configChanges` for orientation, screenSize, screenLayout, smallestScreenSize and uiMode. Put the letter index in `rememberSaveable` (`PaintScreen.kt:178`, `TraceScreen.kt:347`, `FindScreen.kt:201–203`).
   - Decided (maintainer, 2026-11-16): also add `fontScale|density|locale`, so settings changes mid-round keep it too.

4. **The countdown keeps its time with animations removed** (outline).
   - Finding: with "Remove animations" on, Find moved on 0.5 s after the right tap instead of after 5 s, and Paint jumped mid-stroke (`CountdownNext.kt:73–81`).
   - Fix: time the wait on the frame clock, as the parent gate's hold does. The prototype's calm-motion rule drops only confetti, wiggles and pulses.

## Later (needs the prototype first, then `design.sh`)

- Short windows: Lesson, Quiz and Trace minimum heights push the main button off-screen on a 600 dp-tall tablet and a 320 dp phone.
- Toddler layouts at 360 and 320 dp, and on the tablet in landscape: wrapping prompt, play circle over the labels, crayon and eraser row too wide.
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
