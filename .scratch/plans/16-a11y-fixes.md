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
     - A still finger that lifts leaves a dot (a tap), unless ink was made while it was down; that is what a resting thumb looks like. So a dot stroke can be tapped while a thumb rests.
     - A pen that has not moved for 300 ms hands over to the next finger that moves, so a thumb that drifted and stopped does not keep the pen. A pen that is still moving keeps it.

3. **Rotation and settings changes keep the round.** Branch `feat/a11y-config`.
   - Finding: rotating the tablet mid-paint swapped the letter and dropped the paint. A dark-theme switch wiped a half-traced letter.
   - Decided (maintainer, 2026-11-16): also cover `fontScale|density|locale`, so settings changes mid-round keep it too.
   - Fix:
     - `android:configChanges` covers orientation, screen size and layout, uiMode, fontScale, density, locale, layoutDirection, keyboard and navigation. The activity is no longer recreated, so every round keeps its state.
     - A resize refits the letter, so the ink moves with it. Trace remaps its ink with `refit` and replays it into the new check, so dots ticked by on-path ink stay ticked. Ink drawn on a small fit and enlarged a lot (tablet landscape to portrait) scales its wobble past the fixed tolerance and may untick; that is a known limit. Paint remaps its strokes in dp (`Painting.moved`); the painted distance stays.
     - Paint's gesture loop reads its callbacks through `rememberUpdatedState`. Before, it kept the first page's callbacks, so a resize, and every page after the first, checked paint against a stale letter mask.
     - `BoxWithConstraints`' `maxWidth`/`maxHeight` keep the old density after a Display size change, because the px constraints are unchanged and it is not re-measured. All six users now read `windowWidth`/`windowHeight` (`LiveConstraints.kt`).
   - Dropped: the letter index in `rememberSaveable`. The round state is a whole object, so process death still restarts the round.

4. **The countdown keeps its time with animations removed.** Branch `feat/a11y-countdown`.
   - Finding: with "Remove animations" on, Find moved on 0.5 s after the right tap instead of after 5 s, and Paint jumped mid-stroke (`CountdownNext.kt:73–81`).
   - Fix: time the wait on the frame clock, as the parent gate's hold does. The prototype's calm-motion rule drops only confetti, wiggles and pulses.

Slices 5–9 are the review's "Fix next" items that need no design change. Started on the maintainer's "Go ahead" (2026-11-17), in this order.

5. **The gate opens without a finger.** Branch `feat/a11y-gate-actions`. Review item 8.
   - Finding: the hold circle reacts only to touch, so Switch Access and a keyboard cannot open it (`ParentGateScreen.kt:235–246`).
   - Fix: the circle has a long-click semantics action ("open settings") and takes focus. Enter, Space or D-pad centre held for 2 s unlocks; key repeats do not restart the hold. The accessibility long press runs the same 2 s hold. A focused circle shows a white ring.

6. **"Back to play" returns where the gate was opened.** Branch `feat/a11y-gate-return`. Review item 14.
   - Finding: from Paint, the gate replaced the screen, so "Back to play" landed on Toddler home and the painting was gone.
   - Fix: the gate opens on top of the screen, and "Back to play" and system Back pop to it. Paint keeps its round in a back-stack `ViewModel`. This changes plan 05 decision 6 for the gate only: Back on an activity still goes to Toddler home. In Preschool mode "Back to play" also returns to the tab it was opened from (Map, Stickers), not always Home.
   - Find and Look & listen return to themselves but start a new round; only Paint keeps its round, as the review asked.

7. **Results are announced.** Branch `feat/a11y-announce`. Review item 7.
   - Polite live regions on what changes when a result comes: the gate's title and line ("Unlocked. Choose where to go."), the quiz's foot ("Almost! Try again", then Next), Find's foot (phone and tablet: Next appears), Trace's hint bubble (each step and "Well done!"), and Paint's canvas description ("You painted …!").
   - On unlock, focus moves to "Open Parent zone", so the next press does not land on "Back to play".

8. **Dropped labels come back.** Review item 9. Map chips ("alif, learned"); the Trace canvas uses the Latin letter name.

9. **TalkBack stops the countdown, and Arabic is tagged.** Review item 10. Under TalkBack the self-pressing Next waits for a tap; Arabic text carries an Arabic `localeList` for the speech engine.

## Later (needs the prototype first, then `design.sh`)

- Short windows: Lesson, Quiz and Trace minimum heights push the main button off-screen on a 600 dp-tall tablet and a 320 dp phone.
- Toddler layouts at 360 and 320 dp, and on the tablet in landscape: wrapping prompt, play circle over the labels, crayon and eraser row too wide. The same happens at a larger Display size (520 dpi on the phone): Paint's eraser and Trace's pink crayon are clipped at the right edge.
- Fix next, items 11–13: text at 150 % and above, the alif target, and sub-16 sp text. Items 7–10 and 14 are slices 5–9.

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
- Slice 2 follow-up, `feat/a11y-pen-handover`, merged to `main`. Two gaps in the first `Pen`: a tap while a thumb rested left no dot, and a thumb that drifted past slop kept the pen. `check.sh` and `test-fast.sh` PASS, and `PenTest` now has 7 cases.
  - Emulator `huroofi_phone`, Trace ق:
    - One finger: 76,980 px.
    - Thumb resting on the card: 75,925 px.
    - A tap on dot stroke 2 while a thumb rests: the dot turned green.
    - A thumb that drifted 48 px, then rested: the moving finger painted 76,862 px.
  - Paint ز in toddler mode:
    - A thumb resting on the card while a finger painted the body: 62,658 px, and the thumb's spot stayed white.
    - A tap on the dot while the thumb rested: the dot painted.
  - `a11y.sh` PASS on Paint.
- Slice 3, `feat/a11y-config`, merged to `main`. `check.sh` and `test-fast.sh` PASS. New tests:
  - `refitKeepsAPointOnTheSameSpotOfTheLetter`.
  - `inkRefittedToAnotherSizeKeepsItsFinishedStrokes`, over all 28 letters at a larger and a smaller fit. A smaller fit can finish one more stroke (ث's dots at 160 px), as fresh tracing there would.
  - Emulator `huroofi_phone`, same pid throughout (no recreation):
    - Trace ق: font 1.3 → 1.0 and density 520 → 420 keep the ink and the green dots. Before the remap, the dots reset after a font change.
    - Paint ب: font and density changes keep the paint on the same part of the letter.
    - Find: "Where's the strawberry" kept through the font and density changes.
    - Home: density 520 → 420 returns to the original layout. Before `windowHeight`, Trace stayed about 460 px short.
    - Dark and light switches kept ink and progress (checked before the remap).
  - Tablet-sized phone (density 160, sw ≈ 675 dp), rotation to landscape and back mid-round: Paint س keeps its paint on the letter, and Trace ق keeps its ink and green dots.
  - Input after a change, at 520 dpi: Trace stroke 1 inks under the finger and ticks its dots. Find's right tap advances (hen to tree). Paint lands under the finger, ط finishes, and the next page ك finishes too.
  - `a11y.sh` PASS: Home (6), Trace (8), Toddler home (5), Paint (7), Find (5), Look & listen (7), all min 64 dp; parent gate (1, min 48 dp with `--parent`).
  - Not walked: the `huroofi_tablet_small` AVD itself, a locale change since the remap, or the reward screen's `StarBurst`.
- Slice 4, `feat/a11y-countdown`, merged to `main`. `check.sh` and `test-fast.sh` PASS. `countdown` now runs on `withFrameMillis` through `countdownProgress`, not a `tween` that the animator scale shortens. New `CountdownTest`: empty through the 1 s pause, linear fill, full at 5 s and not before.
  - Emulator `huroofi_phone`, all three animation scales at 0: Find moved on between 3.6 s and 6.4 s after the right tap (fox, then duck). It was 0.5 s before. Scales restored to 1.
  - `a11y.sh` PASS on Find with the countdown showing (6 targets, min 64 dp).
  - Paint's countdown is the same component and was not walked separately.
- Slice 5, `feat/a11y-gate-actions`, merged to `main`. `check.sh` and `test-fast.sh` PASS. No unit test: the change is key and semantics wiring, and `HoldGate` is unchanged.
  - Emulator `huroofi_phone`, gate from the Parents tab. The node dump shows the circle as `long-clickable,focusable`, and the second Tab focuses it.
  - Enter tapped, Space tapped, and Enter held 1.2 s each leave it locked. Enter held 2.6 s unlocks it. The first build unlocked on a tapped Enter: the key-up came before the frame that starts the hold. The hold now starts only if the key is still down.
  - The focus ring shows white round the circle (screenshot).
  - `a11y.sh --parent` PASS (2 targets, min 48 dp).
  - Not walked: the long-click action itself. TalkBack was bound, but it ignores gestures injected over adb. The action calls the same hold as the key.
- Slice 6, `feat/a11y-gate-return`, merged to `main`. `check.sh` and `test-fast.sh` PASS. `ToddlerBackTest.gateGoesBackToTheScreenItWasOpenedOn` replaces the gate case of the old home test.
  - Emulator `huroofi_phone`, Toddler mode. Paint ف with 63,357 ink px; gate from the lock, then "Back to play": same letter, 63,357 px. Again with system Back on the gate: same. Back on Paint goes to Toddler home; reopening Paint gives a new letter (ح) with 0 px.
  - From Paint, gate, Parent zone, Back: Toddler home, and Back there stays home.
  - Find: gate and "Back to play" return to Find. Preschool Map tab: Parents, then "Back to play", returns to Map.
  - Not walked: the gate from the Rest screen (its route is unchanged), and a rotation while the gate is over Paint.
- Slice 7, `feat/a11y-announce`, merged to `main`. `check.sh` and `test-fast.sh` PASS. No unit test: semantics only.
  - Emulator `huroofi_phone`: after Enter held 2.6 s on the gate, the focused node is "Open Parent zone" (it was "Back to play" in slice 5's walk). The gate's layout is unchanged: "Open Parent zone" stays at y 1080 px. `a11y.sh --parent` PASS (2 targets).
  - Not verified on a device: the spoken announcements. With TalkBack bound, `uiautomator events` showed no Compose node events at all, so neither the announcement nor its absence could be seen (three attempts). The regions use Compose's `LiveRegionMode.Polite`, the platform's mechanism for this. Check with TalkBack on a real phone.
  - Not walked: Quiz, Find, Trace and Paint after the change (a modifier only, no layout change).
