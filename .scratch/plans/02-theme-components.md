# 02 Theme and shared components

Goal: tokens from HANDOFF §5 as code, plus the seven shared components from §9 step 1, each visible in a debug-only component gallery screen so they can be exercised on a device. Depends on 01.

## Theme (`com.huroofi.app.ui.theme`)

- `HuroofiColors`: navy `#13294B`, muted `#4A5D7A`, sky `#E8F4FF`, card `#FFFFFF`, primary `#1F6FE0` (shadow `#1555B0`), sun `#FFC93C` (`#E0A400`), success `#158048` (`#0F5F35`), outline `#26324A`, parentBg `#F4F7FB`, gateBg `#13294B`. Mapped onto a Material 3 `ColorScheme` (light only; child app has no dark theme). Extra tokens (shadows, parentBg) via a `CompositionLocal` `LocalHuroofiColors`.
- Stage colours are **data**, not tokens: read from `letters.json` `stages` (plan 03) and exposed as `StageColors(border, pastel, accent)`.
- Fonts: `BalooBhaijaan2` (variable, weights 500–800) and `NotoNaskhArabic` (Bold 700) as `FontFamily`.
- Type scale from §5 table as named styles: `caption 16`, `body 18`, `sectionHeading 20`, `buttonSecondary 20`, `buttonPrimary 22`, `screenTitle 26–36`, `arabicChip 26–34`, `lessonLetter 170`; toddler set: `toddlerSentence 34–42 (60 tablet)`, `toddlerWord 50–88`, `toddlerCardLetter 110`. Rule: never under 16 sp, sizes in `sp` so font scale applies.
- `ArabicText(text, size, …)`: forces its own RTL text run (`LayoutDirection.Rtl` / paragraph direction) inside an LTR layout, always Naskh Bold, size = English neighbour +1 step. **No** whole-app RTL flip.
- Shape/size tokens: primary button height 64 dp, corner 22 dp, bottom shadow 6 dp, tile corners 26–40 dp, tile border 5–8 dp, `MinTouch = 48.dp`, `ToddlerMinTouch = 64.dp`, `ToddlerMainControl = 84..104.dp`, `EdgeSafe = 16.dp`.

## Components (`ui.components`)

| Component | Notes |
|---|---|
| `PrimaryButton` | 64 dp tall, 22 dp corners, solid colour + 6 dp darker "3D" bottom shadow, pressed state sinks 6 dp. Variants: primary/blue, success/green, sun/yellow. |
| `RoundIconButton` | Circle, required `contentDescription` (no default, no null), `toddler` flag raises min size to ≥ 64 dp (default 84). |
| `LetterChip` | Arabic letter in a chip, optional stage colours, selected state also draws thick ring (colour never sole signal). |
| `PictureTile` | Illustration + optional word, 5–8 dp stage-colour border, `onClick`, wiggle/nudge state hooks (animation state hoisted for the games). |
| `SpeechBubble` | Prompt bubble with the Arabic sentence and a sun-coloured sound button. |
| `ParentLock` | Small lock icon button, fires `onRequestParentZone`; **does not** open anything itself (the gate is plan 05). Min target ≥ 48 dp, 64 dp in toddler. |
| `StepTabs` | Lesson/Trace/Quiz step indicator, not colour-only. |

Rules enforced in code, not by convention: icon buttons cannot be built without a description; a `Modifier.toddlerTouchTarget()` guarantees ≥ 64 dp.

## Tasks

1. Theme files + font wiring + `ArabicText`.
2. Components one at a time, each with a `@Preview` (incl. font scale 1.3 and Arabic).
3. Debug-only gallery destination (`debug` source set) listing every component.
4. Pure unit tests where logic exists (touch-target helper, type-scale floor ≥ 16 sp asserted over all styles, colour token values vs HANDOFF).

## Verification

`check.sh` green; `test-fast.sh` green (type floor, token values). Visual check = previews / gallery on a device; no automated visual gate until open question 2 is decided. I will say "not visually verified" until someone runs the gallery.
