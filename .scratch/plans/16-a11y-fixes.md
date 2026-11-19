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

8. **Dropped labels come back.** Branch `feat/a11y-labels`. Review item 9. Map chips read "alif, learned", "…, today" or "…, coming up" (learn.js:63). The Trace canvas reads "Trace the letter alif" and "You traced alif" (learn.js:217), so `TraceScreen` takes the Latin name.

9. **TalkBack stops the countdown, and Arabic is tagged.** Review item 10. Under TalkBack the self-pressing Next waits for a tap; Arabic text carries an Arabic `localeList` for the speech engine. The countdown also stretches to the phone's "Time to take action" setting (`calculateRecommendedTimeoutMillis`), and under TalkBack the label drops "starts by itself".

10. **The whole letter card is the letter's button.** Branch `feat/a11y-letter-card`. Review item 12.
   - Finding: the Lesson's big letter was only as wide as its glyph; alif measured 48 × 226 dp against the 64 dp bar (`LessonScreen.kt:200–217`).
   - Fix: the tap target fills the letter card, under the sound button, which keeps its own action. Only the glyph wiggles. The prototype does the same (`.big-letter` fills `.letter-card`).

11. **No text under 16 sp.** Branch `feat/a11y-text-floor`. Review item 13.
   - The gate chip goes from 15 to 16 sp. On the tablet, Find's English line scales through `minText`, so on the 960 × 600 tablet it stays at 16 sp, not 14.6. In the prototype, the gate chip goes from 15 to 16 px and Reward's unlock tag from 14 to 16 px; the app already used 16 for the tag.

12. **Large text above 130 % does not clip.** Branch `feat/a11y-large-text`. Review item 11.
   - The Parent zone title and its "Back to kid mode" button share a wrapping row: the button drops under the title instead of squeezing it to "Pare / nt / zone". The prototype's `.zone-head` wraps too.
   - The progress legend wraps (the prototype's already did). At 200 % "7 to go" was squeezed to 9 px, one letter per line; the review had not listed it.
   - The gate's "Back to play" card is at least 84 dp tall, not exactly 84, so the label fits at 200 %. Same in the prototype.
   - Home's "Today" chip is placed by its measured size, centred under the letter, and its text stops growing at 130 % (`CapFontScale`). Larger, it covered half the letter. The letter itself is what TalkBack reads. The prototype centres the chip too (it sat 30 px left of the centre point).
   - Left as is: at 200 % the "Daily play time" row is narrow (a word or two per line) but readable and unclipped.

13. **The main button stays on screen in a short window.** Branch `feat/a11y-short-windows`. Review item 5.
   - Finding: fixed minimum heights of 760, 740 and 680 dp scrolled the whole Lesson, Quiz and Trace in shorter windows, so "Trace it", Next and Again/Next sat below the edge on the 600 dp-tall tablet and on a 320 dp phone.
   - Fix: the header and the main button stay put, and the middle of the screen (`FillColumn`) gives its big card the height left. The card never goes below a floor: the Lesson letter card 184 dp (the letter shrinks to 96 sp in it), the Trace canvas 200 dp (the letter refits), the Quiz grid 72 dp a row. Only below that does the middle scroll. The three `MinHeight`s and `CappedWidth`'s scroll are gone.
   - The Lesson letter is now placed by its ink (`CenteredLetter`), like every other single letter.
   - Prototype unchanged: its frame is fixed at 390 × 844, where no card reaches its floor.

14. **Toddler screens fit a narrow phone and a larger Display size.** Branch `feat/a11y-toddler-layouts`. The review's "Later" toddler-layouts item.
   - Toddler home: the play circle sits under its label, not over the tile's corner, so a narrow or short tile never covers the word. Prototype: `.tile-side`.
   - Toddler home: the prompt stays two lines, broken between the words ("ماذا / نلعب؟"); a narrow phone shrinks it, never splits a word. Leo is a quarter of the width, 56 to 96 dp, so the bubble keeps room at 320 dp. `ScaleDownToFit` now measures its content unwrapped (all three callers are on this screen).
   - Paint: the wipe button sits under the four crayons, as in the prototype, and the prompt pill shrinks its words rather than pushing out the speaker.
   - Trace: five crayons in one row when 5 × 64 dp fits, else three over two, centred. All stay 64 dp.

## Later

- Fix next: done. Items 7–10 and 14 are slices 5–9; items 12, 13 and 11 are slices 10, 11 and 12. Item 5 is slice 13; the toddler-layouts item is slice 14.

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
- Slice 8, `feat/a11y-labels`, merged to `main`. `check.sh` and `test-fast.sh` PASS. No unit test: labels only.
  - Emulator `huroofi_phone`, Preschool: the Map's stage 6 chips read "qaaf, today", "kaaf, coming up", "laam, coming up" and "miim, coming up"; the Trace canvas reads "Trace the letter qaaf". `a11y.sh` PASS on Trace (8 targets) and Map (9 targets).
  - Not walked: the "You traced …" wording after a finished trace, or a learned chip (none on the current stage).
- Slice 9, `feat/a11y-talkback-wait`, merged to `main`. `check.sh` and `test-fast.sh` PASS. New tests: `CountdownTest.aLongerTimeToTakeActionStretchesTheFill`, and `ArabicLocaleTest` (a run, `withArabic` and `highlightedWord` tag only the Arabic).
  - Emulator `huroofi_phone`, Toddler Find, right card tapped and then screenshots only: with TalkBack on (`touchExplorationEnabled=true`), the round stayed on "frog" for 15 s with the bar empty. With TalkBack off, it moved on by itself to "duck".
  - Walking note: `uiautomator dump` suppresses TalkBack while it runs, so a dump taken under TalkBack shows the non-TalkBack label and restarts the countdown. Check this state with screenshots, not dumps.
  - Not verified on a device: the TalkBack label "Next", and whether the speech engine switches to Arabic on the tagged runs (neither can be heard on the emulator). Not walked: Paint's ring under TalkBack, or a longer "Time to take action".
- Slice 10, `feat/a11y-letter-card`, merged to `main`. `design.sh`, `check.sh` and `test-fast.sh` PASS. No unit test: a modifier move only.
  - Prototype: the Lesson reference screen looks unchanged (screenshot).
  - Walked later the same day, on `huroofi_phone`, Lesson ق: "Letter qaaf. Tap to hear" is one node covering the whole card ([53,396][1027,1716] px), and "Hear qaaf and pencil" stays its own node on top. A tap at the card's lower left, away from the glyph, wiggled the letter (4,812 px of the glyph area changed). `a11y.sh` PASS (5 targets, min 64 dp).
  - Not walked: alif itself (the emulator's progress is on stage 6), or the sound button's audio (silent placeholders).
- Slice 11, `feat/a11y-text-floor`, merged to `main`. `design.sh`, `check.sh` and `test-fast.sh` PASS. New tests: `ZoneSpecTest.gateTextIsAtLeast16sp` (the gate's `textSizes` was never checked, which is how 15 sp got through), and `minText` cases in `FindLayoutTest`.
  - Walked in slice 12 on `huroofi_phone`: the 16 sp gate chip fits on one line at 100 % and 150 %.
  - Not walked: Find's English line on the small tablet in landscape.
- Slice 12, `feat/a11y-large-text`, merged to `main`. `design.sh`, `check.sh` and `test-fast.sh` PASS. No unit test: layout only.
  - Emulator `huroofi_phone`, before and after at 100, 150 and 200 %:
    - Parent zone header: one line at 100 %; the button under the title at 150 and 200 % (before: "Parent / zone", "Pare / nt / zone").
    - Legend at 200 %: "7 to go" on its own line. The whole zone was scrolled at 200 % with nothing clipped.
    - Gate at 200 %: "Back to play" on two lines inside a taller card (before: "play" cut off).
    - Preschool Home, stage 6, ق today: the chip is whole at 100, 130 and 200 %. At 200 % it touches the bottom of ق, because the larger header makes the path card shorter.
  - `a11y.sh` PASS: Home at 100 % and 200 % (6 targets, min 64 dp); Parent zone at 200 % with `--parent`.
  - The emulator is left in Preschool mode at 100 % text. Its daily limit was used up, so its clock was moved a day on to keep walking, then set back to automatic.
- Slice 13, `feat/a11y-short-windows`, merged to `main`. `check.sh` and `test-fast.sh` PASS. No unit test: layout only, walked instead.
  - Emulator `huroofi_tablet_small`, landscape (1920 × 1200 px, 320 dpi):
    - Preschool: Lesson alif, Trace alif and the 3-picture Quiz all fit with the button on screen (before: "Trace it" and Again/Next below the edge). Quiz `a11y.sh` PASS (6 targets).
    - Early reader: the letter card sits at its 184 dp floor with "Trace it" on screen, and the shapes card scrolls under it. Trace's canvas is 208 dp. The 4-picture Quiz fits. `a11y.sh` PASS on all three (5, 8, 6 targets).
    - Early reader at 200 % text: all three buttons on screen; on Trace the crayon row is the part that scrolls. `a11y.sh` PASS on all three.
  - Emulator `huroofi_phone`:
    - Design size, Early reader Lesson ق, screenshot against `main`'s build: only the glyph's pixels differ (it now centres by ink, same size); the cards and gaps are identical.
    - 320 dp (`wm density 540`), Early reader: "Trace it" and Again/Next on screen; Lesson `a11y.sh` PASS. Trace still fails Purple and Pink (52 and 48 dp wide); that is the Toddler-layouts item below, not this slice.
  - Not walked: Quiz on the 320 dp phone (it is 711 dp tall, more than the tablet's 550). Both emulators had used up their daily limit (the tablet's to real play time over the long walk), so their clocks were moved on, then set back to automatic.
- Slice 14, `feat/a11y-toddler-layouts`, merged to `main`. `check.sh`, `test-fast.sh` and `design.sh` PASS. No unit test: layout only, walked instead.
  - Emulator `huroofi_phone` at the design size, 360 dp (`wm density 480`) and 320 dp (`wm density 540`):
    - Toddler home: the circle is under each label; the prompt is two whole words at all three. `a11y.sh` PASS at all three (5 targets). Before, at 320 dp the prompt ran one letter per line and squeezed the tiles to strips.
    - Paint: four crayons in a row, wipe under them, at all three; `a11y.sh` PASS (7 targets). At 320 dp the prompt pill keeps its speaker.
    - Find and Cards at 320 dp: fit; `a11y.sh` PASS (5 and 7 targets).
    - Early reader Trace ق: five crayons in a row at the design size and 360 dp, three over two at 320 dp; `a11y.sh` PASS at all three (8 targets). Before, Purple and Pink failed at 320 dp.
  - Emulator `huroofi_tablet_small`, landscape: Toddler home, Paint, Find and Cards fit; `a11y.sh` PASS on all four.
  - The phone is left in Early reader mode at its design size; the tablet in Toddler mode.
