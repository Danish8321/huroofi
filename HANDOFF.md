# Huroofi (حروفي) — Android build handoff

An Arabic alphabet app for children aged 18 months and up. This file is the source of truth for the build.

The approved look and behaviour is the interactive prototype `design-reference/prototype.html` (approved 2026-10-08). Open it in a browser and tap through it like the app; the right-hand panel lists each screen's rules, and the "Design system" tab shows the tokens and shared controls. `design-reference/screens/*.html` are static snapshots generated from it. Rebuild natively; never wrap either in a WebView.

To change the design, edit the sources in `design-reference/prototype/` and run `.claude/scripts/design.sh`: it rebuilds the prototype, runs a scripted click-through and regenerates the snapshots. Never edit `screens/*.html` by hand.

## 1. Platform and stack

- Android only. Kotlin + Jetpack Compose, Material 3 as the base, single-activity with Navigation Compose.
- Phones in portrait. Tablets in portrait and landscape. Only Toddler Find has its own tablet layout (`ToddlerFindTablet.html`); every other screen uses the phone layout, centred.
- Works fully offline. No ads, no in-app purchases inside child screens, no third-party analytics in child screens. The app will be listed under Google Play's Families programme, so check the current Families policy before adding any SDK.
- App UI language is English (left-to-right). Every Arabic letter, word or sentence is its own right-to-left text run. Do not flip the whole layout to RTL.

## 2. Age modes

The parent picks one mode in the Parent zone (stored locally, default **Toddler**). The mode decides which home screen opens.

| Mode | Ages | Home | Activities |
|---|---|---|---|
| Toddler | 18 months – 3 years | `ToddlerHome` | Look & listen cards, "Where's the…?" game (2 pictures), finger paint |
| Preschool | 3 – 5 years | `Main` | Lesson, forgiving trace, picture quiz with 2–3 options, stage map |
| Early reader | 5 years + | `Main` | Everything, including letter shapes (start/middle/end) and 4-option quiz |

## 3. Screens and navigation

Snapshots are in `design-reference/screens/`; extra states have a suffix (`-done`, `-almost`, `-reader`).

| Screen | Snapshot | Reached from |
|---|---|---|
| Home (preschool / reader) | `Main.html` | App start in those modes; Home tab |
| Meet the letter | `Lesson.html`, `Lesson-reader.html` | Home "Let's learn ب"; Letter map |
| Trace it | `Trace.html`, `Trace-done.html` | Meet "Trace it" |
| Picture game | `Quiz.html`, `Quiz-almost.html`, `Quiz-done.html`, `Quiz-reader.html` | Trace "Play" (tapped, never automatic) |
| Stage complete | `Reward.html` | The game's last round when it completes a stage |
| Letter map | `StageMap.html` | Map tab; Reward |
| Sticker book | `Stickers.html` | Stickers tab; Reward |
| Trace practice | prototype only (Trace without the step bar) | Home or Map while Trace only is on |
| Parent gate | `ParentGate.html` | Every lock, the Parents tab, Back on the preschool home, the lock on Rest |
| Parent zone | `Parents.html` | Parent gate after a 2-second hold |
| Rest | `Rest.html` | Replaces any child screen once today's play time is used up |
| Toddler home | `ToddlerHome.html` | App start in Toddler mode |
| Look & listen | `ToddlerCards.html` | Toddler home |
| Where's the…? | `ToddlerFind.html`, `ToddlerFind-done.html`, `ToddlerFindTablet.html` | Toddler home |
| Finger paint | `ToddlerPaint.html`, `ToddlerPaint-done.html` | Toddler home |

Bottom navigation (preschool/reader only): Home, Map, Stickers, Parents. Toddler mode has no bottom navigation, only a big Home button and a small lock.

System Back: on Toddler home and Rest it does nothing; on other toddler screens it goes to Toddler home; on the preschool Home it opens the parent gate; elsewhere it goes one step back (Trace to Meet, Reward to Map, the rest to Home).

## 4. Content

`data/letters.json` holds all 28 letters in alphabet order: letter, Arabic and Latin name, picture word (with its first letter split out for highlighting), English meaning, stage number, card image path and audio file names. Stages (7 × 4 letters) carry their colours and names.

- Card images: `assets/cards/` (1064 × 1512 PNG). Overview: `design-reference/all_28_cards_overview.png`.
- Picture illustrations: `assets/illustrations/svg/` (originals, import as VectorDrawable where Android Studio accepts them) and `assets/illustrations/png/` (512 px fallback, transparent background).
- Audio is **not recorded yet**. Use the file names in `letters.json` and ship silent placeholders until a native Arabic speaker records them. Also needed: toddler prompts ("أين البطة؟" etc.), "ماذا نلعب؟", "لوّن الباء", praise sounds and a gentle "boing" for wrong taps.

## 5. Design tokens

### Colours
| Token | Hex | Use |
|---|---|---|
| navy | `#13294B` | All main text, big letters |
| muted | `#4A5D7A` | Secondary text |
| sky | `#E8F4FF` | Child screen background |
| card | `#FFFFFF` | Cards, tiles |
| primary | `#1F6FE0` / shadow `#1555B0` | Main buttons |
| sun | `#FFC93C` / shadow `#E0A400` | Sound buttons, stars |
| success | `#158048` / shadow `#0F5F35` | Correct answer, Next after success |
| outline | `#26324A` | Illustration outlines |
| guide | `#C8DCF4` | Trace letter band (navy outline) |
| almost | `#FFE4D6` / text `#8A3B12` | Gentle "Almost!" hint |
| rest | `#24345E` to `#3A4F86` | Rest screen gradient |
| parent bg | `#F4F7FB` | Parent zone background |
| gate bg | `#13294B` | Parent gate background |

Stage colour families (border / pastel / accent for the highlighted first letter) are in `letters.json` → `stages`. Example stage 1: `#F59E0B` / `#FFEDC4` / `#A35400`.

### Type
Fonts are in `assets/fonts/` (both SIL Open Font License; licence files included).

- **Baloo Bhaijaan 2** for all English (weights 500–800).
- **Noto Naskh Arabic Bold** for all Arabic, always one step larger than the English around it.

| Role | Size (sp) | Weight |
|---|---|---|
| Caption (nav labels, tabs, setting descriptions) | 16 | 600–800 |
| Body (instructions, sentences) | 18 | 500–700 |
| Section heading | 20 | 800 |
| Secondary button | 20 | 800 |
| Primary button | 22 | 800 |
| Screen title | 26–36 | 800 |
| Arabic letter chips | 26–34 | Naskh 700 |
| Lesson big letter | 170 | Naskh 700 |

Toddler mode sizes are deliberately larger: Arabic sentences 34–42 (60 on tablet), words under pictures 50–88, letter on cards 110.

Never go below 16 sp. Respect the system font scale; layouts must not clip at 1.3×.

### Shape and size
- Primary button: 64 dp tall, 22 dp corners, solid colour with a 6 dp "3D" bottom shadow in the darker shade.
- Cards and tiles: 26–40 dp corners. Child tiles use a 5–8 dp coloured border.
- Touch targets: at least 48 dp everywhere; **at least 64 dp in Toddler mode** (main controls 84–104 dp), with wide gaps and nothing important within 16 dp of screen edges.

## 6. Screen behaviour

The prototype's notes panel has the full rules per screen. The rules that hold everywhere:

- **Stars only celebrate.** No star counters, meters or points while a child is working, on any screen. Stars burst only when something is finished right. The sticker (one per stage, seven in all) is the only reward; the app stores no child name or profile.
- **Preschool and Early reader: Next waits for the child.** Next is grey and locked ("Finish ب first") until the step is done, and it never moves on by itself. When it unlocks it turns blue or green and pulses gently.
- **Toddler: the next item comes by itself.** After the celebration a visible countdown runs for 4 seconds (Find: the Next button fills left to right on phones, a ring on tablets; Paint: a ring), then the next item starts. Tapping Next skips the wait.
- **No fail states.** A wrong tap gives a soft "boing", a nudge and a wiggle of the right answer. No X, no red, nothing lost.
- **Calm motion.** With the phone's "Remove animations" on, confetti, wiggles, pulses and bursts are dropped.

Per screen:

- **Home:** "Hi there!" and the current stage; a winding path card with the stage's 4 letters (learned green, today's letter big and blue, the rest waiting) and the stage sticker as a "?" at the end. One button: "Let's learn ب" ("Trace ب" with Trace only; a random learned letter once all 28 are learned). Nothing plays on open; tapping today's letter plays it.
- **Meet (Lesson):** step bar Meet / Trace / Play. Plays letter then word on open; the big letter, picture and sound button replay. Picture word with its first letter in the stage accent colour, plus the English meaning. Early reader adds the letter shapes card (non-joining letters show Alone and End only).
- **Trace:** guided trace as built (guide band, dots every 18 dp, numbered coins with 1 in green, end arrows, demo hand, 5 s idle re-demo, Again, 5 paints). A stroke counts only when every one of its dots (100 %) has been covered within 30 dp, in any order or direction, over one or more touches; missed dots stay white so the child can fill them in. Its coin then turns into a star. When every stroke is done: star burst, "You traced ب!", and "Play" unlocks. It never moves on to the game by itself.
- **Picture game (Quiz):** "Which starts with ب?", pictures only: Preschool 3 in a column, Early reader 4 in a 2×2 grid. Three rounds shown as three dots. Wrong: the picture dims, the right one wiggles, "Almost! Try again" plus "Fox (ثعلب) starts with ث". Right: green frame, a star pops, the word appears, praise and vibration, then "Great job! Next", only on tap. The third right answer makes the letter learned; the button then reads "Next letter", or "Get your sticker" when the stage is complete.
- **Stage complete (Reward):** the stage sticker as the hero with rays and confetti, the 4 letters with pictures, the next stage shown as unlocked. "Go to <next stage>" opens the map; the square button opens the sticker book.
- **Letter map:** the current stage open wide with its 4 letters and "Continue with ب"; finished stages with a green tick; locked stages compact with a lock (tap: boing and wiggle). Unlock all swaps the locks for play buttons.
- **Sticker book:** seven slots; earned stickers filled in, the rest gentle dashed slots with "?". Stage stickers: 1 lion, 2 carrot, 3 flower, 4 rocket, 5 bird, 6 pencil, 7 star.
- **Trace practice:** Trace without the step bar; "Next letter" goes to the next letter in the alphabet on tap. Never learns, never gives stickers, never unlocks. It counts toward daily play time.
- **Toddler home:** Leo asks "ماذا نلعب؟"; three tiles that preview their game (Look & listen, Find, Paint); a small lock (a tap; the hold is on the gate).
- **Look & listen:** one big card per letter, all 28, wrapping. Letter then word on arrival; tapping replays and wiggles. 96 dp arrows plus swipe; small pictures of the previous and next cards between the arrows. No page dots.
- **Where's the…?:** toddler words only; 2 big pictures; "أين ال…؟" on open and on the sound button. Right: stars burst from the picture, then the fill-up Next. No star counter, no sets of three.
- **Finger paint:** a random letter, never the same twice in a row; the demo hand plays once. Fat 30 dp brush, 4 crayons, wipe; paint shows only on the letter. After about 400 dp of paint: green frame, star burst, Leo cheers, then the ring countdown to a new letter; "Paint it again" keeps the letter. No star meter.
- **Parent gate:** "Back to play" first and biggest. Hold the circle for 2 seconds; the ring fills and letting go resets it. Then "Open Parent zone" or "Close Huroofi".
- **Parent zone:** Your child (stage, learned / learning / to go bar, all 28 letters); Learning mode (applies on going back to kid mode); Voice & sounds; Vibration (default on); Show vowel marks (shown, locked "Coming soon" until the vowelled text is reviewed); Daily play time 5–60 min, step 5, default 20; Offline pack note with the App pinning tip; Unlock all; Trace only; Start over with an in-page confirm. No weekly report, no download button.
- **Rest:** calm night screen, Leo resting, "وقت الراحة" spoken once. No countdown, no red; only the lock leads anywhere. Raising the limit sends the child back to play.

## 7. Child-safety and toddler rules

1. Sound first: every instruction is spoken. Text is for parents.
2. No fail states for toddlers: no X marks, no red, no lost stars.
3. Two or three choices at most on a toddler screen.
4. Keep children inside the app: use immersive mode, and route the system Back gesture to the in-app Home rather than exiting. Exiting the app or opening settings needs the parent gate.
5. No external links, purchases or sharing from child screens.
6. Sessions are short; the daily limit ends play gently.

## 8. Accessibility

- Content descriptions on every image button (the reference HTML `aria-label`s give the wording).
- Colour is never the only signal (selected crayons also get a thick ring; locked stages also show a lock icon).
- Text contrast meets WCAG AA against its background.

## 9. Changes from the app as built (2026-10-08)

The app already implements most of this file. The approved redesign changes:

1. All screens take the new layouts and visuals from the prototype.
2. Trace no longer advances to the game by itself; the child taps "Play".
3. The game shows "Great job! Next" after each right answer and waits for a tap; the last round reads "Next letter" or "Get your sticker".
4. Finger paint: the star at 400 dp is replaced by the celebration (green frame, star burst, cheering Leo) and a 4-second ring countdown to a new letter, plus "Paint it again".
5. Where's the…?: the sets of three and the header star counter go; one round at a time with the 4-second fill-up Next.
6. Look & listen: page dots replaced by previous / next picture thumbnails.
7. Home: the path card replaces the current layout; the button reads "Let's learn ب".
8. Meet shows the English meaning under the picture word.
9. Trace practice counts toward daily play time (`practice/{index}` joins the child routes).
10. Trace needs 100 % of each stroke's dots, not 70 %.
11. New screen designs: Sticker book, Rest, Trace practice.

## 10. Notes on the illustrations

All 28 illustrations and cards were drawn originally for this project, so the app can use them. The earlier third-party flashcards (with belarabyapps.com branding) must not be used. The words differ from those cards in three places on purpose: ذ uses ذرة (corn), ض uses ضفدع (frog) and ل uses ليمون (lemon).
