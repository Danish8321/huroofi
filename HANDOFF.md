# Huroofi (حروفي) — Android build handoff

An Arabic alphabet app for children aged 18 months and up. This file is the source of truth for the build. The HTML files in `design-reference/screens/` show the approved look and behaviour; rebuild them natively, do not wrap them in a WebView.

## 1. Platform and stack

- Android only. Kotlin + Jetpack Compose, Material 3 as the base, single-activity with Navigation Compose.
- Phones in portrait. Tablets in portrait and landscape (see `ToddlerFindTablet.html` for the landscape layout pattern).
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

Reference files are in `design-reference/screens/`.

| Screen | File | Reached from |
|---|---|---|
| Home (preschool / reader) | `Main.html` | App start in those modes |
| Letter map | `StageMap.html` | Home → Map, bottom nav |
| Meet the letter | `Lesson.html` | Home "Let's go!", map play button |
| Trace it | `Trace.html` | Lesson "Next" |
| Picture game | `Quiz.html` | Trace "I did it!" (replace with auto-check, see §7) |
| Stage complete | `Reward.html` | Finishing the 4th letter of a stage |
| Parent gate | `ParentGate.html` | Every lock icon and the Parents tab |
| Parent zone | `Parents.html` | Parent gate after a 3-second hold |
| Toddler home | `ToddlerHome.html` | App start in Toddler mode |
| Look & listen | `ToddlerCards.html` | Toddler home |
| Where's the…? | `ToddlerFind.html` / `ToddlerFindTablet.html` | Toddler home |
| Finger paint | `ToddlerPaint.html` | Toddler home |

Bottom navigation (preschool/reader only): Home, Map, Stickers, Parents. Toddler mode has no bottom navigation, only a big Home button and a small lock.

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

## 6. Screen behaviour notes

- **Lesson:** play letter sound then word sound automatically on open; replay on tapping the sound button or the picture. Letter shapes row (alone/start/middle/end) only in Early reader mode.
- **Trace:** forgiving path tolerance (about 30 dp either side). Detect completion automatically; the "I did it!" button is a fallback, not the only way forward.
- **Quiz:** options per mode — Preschool 2–3, Early reader 4. Wrong answer: gentle sound, the correct picture wiggles as a hint. Keep the soft orange "Almost!" banner for Preschool+ only.
- **Reward:** stars, sticker, cheering sound. Progress unlocks the next stage.
- **Toddler cards:** tap picture → wiggle + word audio. Big previous/next arrows; swipe also works.
- **Where's the…?:** 2 pictures, spoken prompt plays on open and on tapping the yellow sound button. Wrong tap: small nudge of the tapped picture, the right one wiggles. Never show an X or red. Right tap: stars pop, green Next appears.
- **Finger paint:** free painting with a fat brush (30 dp) over the dashed letter; 4 colours plus wipe. A star appears after a short amount of painting. Nothing can be "wrong".
- **Parent gate:** press and hold 3 seconds with a filling ring; release resets. Only then show "Open Parent zone".
- **Parent zone:** progress grid of all 28 letters, learning mode switch, voice toggle, vowel marks (ḥarakāt) toggle, daily time limit (5–60 min, step 5, default 20) with a friendly "time to rest" screen, offline pack.

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

## 9. Suggested build order

1. Project setup, theme (colours, fonts, type scale), shared components: PrimaryButton, RoundIconButton, LetterChip, PictureTile, SpeechBubble, ParentLock, StepTabs.
2. Content layer: load `letters.json`, audio player with placeholders, progress storage (DataStore).
3. Toddler mode: ToddlerHome, Look & listen, Where's the…?, Finger paint, Parent gate.
4. Parent zone with mode switch and time limit.
5. Preschool/reader: Home, Letter map, Lesson, Trace, Quiz, Reward.
6. Tablet layouts, immersive mode, accessibility pass, Families-policy check.

## 10. Notes on the illustrations

All 28 illustrations and cards were drawn originally for this project, so the app can use them. The earlier third-party flashcards (with belarabyapps.com branding) must not be used. The words differ from those cards in three places on purpose: ذ uses ذرة (corn), ض uses ضفدع (frog) and ل uses ليمون (lemon).
