# 08 Tablet, immersive, accessibility, Families check — HANDOFF step 6

Approved 2026-10-04 (decisions 1–14 below). Task list: `08-polish-tasks.md`. The numbered outline is kept for history; where it differs (lint rule, release prep scope), the decisions win.

1. **Tablet layouts** (all screens, incl. toddler; plan 05 ships phone only with a 480 dp centred fallback): portrait + landscape via window size classes; `ToddlerFindTablet` pattern; Arabic sentence 60 sp on tablet.
2. **Immersive mode** on child screens; system bars return in Parent zone. Back handling already centralised (plan 05 slice 6).
3. **Accessibility pass**: content description audit (lint rule + a unit test over component API), contrast AA check of all token pairs (pure test), layouts at font scale 1.3 (needs Compose UI tests or device: open question 2), TalkBack order sanity.
4. **Families-policy check**: read current Google Play Families policy at that time; confirm dependency list (Jetpack/Kotlin only), no INTERNET permission, no advertising ID, data-safety answers, target-audience declaration. Output: `docs/families-check.md` with the sources and date.
5. **Illustrations**: decide SVG→VectorDrawable vs PNG (PNG used by default, APK-size check).
6. **Release prep**: signing config stays out of the repo; app icon from approved art (none supplied: ask).

## Decisions (grilling 2026-10-04)

1. Tablet scope: only "Where's the…?" gets its own landscape layout, from `ToddlerFindTablet.html`. Every other screen keeps its phone layout in a centred column (width: decision 4), works in portrait and landscape, and scrolls where landscape height runs out. No new tablet designs.
2. Orientation: at start the activity reads the smallest screen width. Below 600 dp it is a phone and is locked to portrait; at 600 dp or more it rotates freely. Foldables follow the current screen. No dependency. Already in place: `MainActivity` reads `R.bool.portrait_only` (false in `values-sw600dp`).
3. Find tablet layout: used when the window is at least 840 dp wide and wider than tall. Laid out at prototype sizes, then scaled uniformly by min(width/1180, height/820, 1); touch targets never below 64 dp after scaling. Tablet portrait uses the phone layout in the centred column.
4. Column width: one shared max content width of 480 dp for every screen except the Find tablet layout. Backgrounds fill the full window; only content is capped.
5. The 60 sp tablet sentence applies only in the Find tablet layout (and scales with it). Other toddler sentences keep phone sizes inside the column on tablets.
6. Tablet testing: two AVDs from the installed google_apis x86_64 image, `huroofi_tablet` (pixel_tablet, ~1280x800 dp) and `huroofi_tablet_small` (small_tablet, ~960x600 dp). Every changed screen checked portrait and landscape on both: `lp.sh`, an off-screen/clipping check, screenshots.
7. Immersive: every child-reachable screen (toddler, Learn, rest, parent gate) hides status and navigation bars; an edge swipe shows them transiently. The Parent zone shows the bars; leaving it hides them again. The swipe-home gesture cannot be blocked by an app.
8. Parent zone gets one muted line under Offline pack: "To keep your child in Huroofi, turn on App pinning in your phone's Settings." Wording only, no link.
9. Contrast: each screen spec declares the text/background pairs it draws; one test checks them all, including every stage's pairs from letters.json. Thresholds: 4.5:1 normal text; 3:1 for large text (>= 24 sp, or >= 18.66 sp bold) and non-text cues (rings, icons). A failing pair is brought to the user before any colour changes.
10. Font scale: English text and Arabic words follow the system text-size setting; single letters in fixed tiles stay at their designed size. Every screen checked on the phone emulator at 1.3x; clipping fixed by wrapping or scrolling, never by shrinking text.
11. Accessibility gate: new `.claude/scripts/a11y.sh` dumps the emulator's current screen and fails on any clickable/long-clickable node without a label, child-screen touch targets under 64 dp, or Parent zone targets under 48 dp. Run on every screen per mode, phone and tablet. TalkBack order checked by hand on Toddler Home, Lesson, Quiz, Parent zone. No lint module.
12. Families: `docs/families-check.md` records current policy text with links and date read, Huroofi's answer per requirement, suggested Data safety answers and the target-audience declaration (draft; the user fills Play Console). `check.sh` gains an aapt2 step on the built APK that fails on INTERNET, AD_ID, location, camera, microphone or contacts permissions, or on a dependency outside the allow-list.
13. Illustrations stay PNG (no SVG sources supplied). Release build turns on R8 and resource shrinking; the release APK size is measured and recorded. Lossless WebP only if art is a large share, with numbers shown to the user first.
14. Release prep in plan 08 is only the release build config (R8, resource shrinking, kotlinx-serialization keep rules), checked running on the emulator. Signing, store listing and Play Console move to a later plan 09 after the demo. The launcher icon stays.

## Release size (task 7.2, measured 2026-10-05 at 35ee241)

| APK | File | Code (dex) | Images | Audio | Fonts |
|---|---|---|---|---|---|
| debug | 37.1 MB | 32.0 MB (88%) | 3.1 MB (9%) | 391 KB (1%) | 287 KB (1%) |
| release (R8 + resource shrinking) | 3.9 MB | 2.2 MB (57%) | 821 KB (21%) | 391 KB (10%) | 287 KB (7%) |

Sizes are compressed bytes inside the APK. The release drops the 28 unused `card_*` images (2.4 MB in source); the 28 `pic_*` pictures and launcher icons are the 821 KB. Art is about a fifth of the release, so no WebP change (decision 13).
