# Huroofi build plans

Source of truth: `HANDOFF.md`. Plans follow its §9 build order. Each plan is one reviewable section; nothing is built until it is approved.

| # | Plan | Handoff step | Status |
|---|---|---|---|
| 01 | [Project skeleton](01-skeleton.md) | 1 | draft |
| 02 | [Theme and shared components](02-theme-components.md) | 1 | draft |
| 03 | [Content layer, audio, progress](03-content-layer.md) | 2 | draft |
| 04 | [Verification gates](04-gates.md) | all | draft |
| 05 | [Toddler mode](05-toddler.md) — [task list](05-toddler-tasks.md) | 3 | built (slices 1–6 on `feat/toddler-mode`); checked on a phone emulator (no sound), phrases pending native review, daily limit in 06 |
| 06 | [Parent zone](06-parent-zone.md) — [task list](06-parent-zone-tasks.md) | 4 | built (slices 1–5 on `feat/parent-zone`); checked on a phone emulator (no sound), rest phrase pending native review |
| 07 | [Preschool / reader flow](07-preschool-reader.md) — [task list](07-preschool-reader-tasks.md) | 5 | built (slices 1–6 on `feat/preschool-reader`); checked on a phone emulator (no sound) |
| 08 | [Tablet, immersive, a11y, Families check](08-polish.md) — [task list](08-polish-tasks.md) | 6 | built (slices 1–8 on `feat/polish`); checked on phone and two tablet emulators: `a11y.sh` on every screen in all three modes, text size 1.3x, release build (Toddler and Preschool, lesson to quiz, gate, zone), `apk-audit.sh` in `check.sh`. Not verified: sound, real devices, Reward screen and Early reader on the release build, TalkBack walked by hand (focus order read from the accessibility tree). Families answers are a draft; privacy policy open (plan 10) |
| 09 | [Guided tracing](09-guided-trace.md) — [task list](09-guided-trace-tasks.md) | — (issue 014) | built, stroke order pending review. Verified: `check.sh`, `test-fast.sh`, `contract.sh` PASS. Emulator (2026-10-06): phone (Paint ذ, Trace ش) and `huroofi_tablet` both orientations (Paint ظ, Trace alif: demo on entry, touch stops it, idle replay, Again and helper replay, star) and `huroofi_tablet_small` (Trace alif, text size 1.3x); `a11y.sh` PASS on each. Found and fixed: idle replay never played (`01edbc2`). Not verified: Trace letters other than ش and alif (dot strokes untraced), sound (silent placeholder), real devices, stroke order not signed off; hamza coin 2 crowds its dots; small tablet (600 dp tall) scrolls to reach Again (plan 08 design). 2026-10-06: "I did it!" removed, trace completes only on its own (decision 3 changed); Meet card grows into spare height; stroke points snapped onto the font (`tools/strokes_fit.py`), `AllLettersTraceTest`: all 28 finish when traced, wrong lines and skipped dots do not |
| 10 | Store: signing, listing, Play Console, privacy policy | — | outline only (moved from 09 on 2026-10-05; plan 08 decision 14) |
| 11 | [Kid review fixes](11-kid-review.md) | — (screen review 2026-10-07) | built, merged to `main` 2026-10-07 (`273e4c7`). Verified: `check.sh`, `test-fast.sh`, `contract.sh` PASS (`QuizRoundTest`, `TraceCoinsTest`, `LetterPathTest` replay, `CardAudioTest`). Emulator `huroofi_phone` (2026-10-07): Home one button; Lesson letter wiggle and step track; Trace ص band and ب dot coin; Map finished-card replay; Sticker tap; Quiz answer moved slot every round (two runs), plain hint, balanced heading; Paint crayons under canvas, Next below them; Find pictures beside words; Reward hero, tap. At text size 1.3x: Home, Lesson, Trace, Quiz, Reward, Find, Paint unclipped and `a11y.sh` PASS on each. Reward reached by writing test progress into the emulator datastore. Not verified: sound (silent placeholders), tablet layouts of the changed screens, real devices |
| 12 | [Trace hand](12-trace-hand.md) | — (maintainer sample, 2026-10-07) | built, merged to `main` 2026-10-07. Verified: `check.sh`, `test-fast.sh`, `contract.sh` PASS (`PaintModelTest.paintOffTheLetterEarnsNothing`, renamed `DemoHandTest`). Emulator `huroofi_phone` (2026-10-07): demo hand on Paint ظ and Trace ت (taps the dot strokes); ص covered dots turn green; ت later strokes' dots faint; Navy outline; ص ink off the letter hidden; Paint ن zigzag paint only on the letter, ~2,500 dp of scribble beside the letter left no paint and no star; `a11y.sh` PASS on Paint. Not verified: sound, tablet, real devices |
| 13 | [Parent options](13-parent-options.md) | — (maintainer, 2026-10-07) | built, merged to `main` 2026-10-07 (c30977f). Verified: `check.sh`, `test-fast.sh`, `contract.sh` PASS on merged `main`; `a11y.sh --parent` PASS (zone top, Lessons + Start over, confirm dialog). Emulator: Start over, Unlock all (stage 3 opens Meet ذ), Trace only (أ then ب, nothing learned; Map opens ذ). Not verified: Reward pill hidden for a stage finished ahead (unit tests only), sound, tablet, real devices |
| 14 | [Design polish](14-design-polish.md) | — (component review, 2026-10-07) | merged to `main` (6257bd3). Gates PASS; emulator checks and gaps in the plan's Built section |
| 15 | [Redesign](15-redesign.md) | §9 (prototype approved 2026-10-08) | slices 1 (trace), 2 (quiz waits for a tap), 3 (practice counts as play), 4 (Find counts down to Next) and 5 (Paint celebrates and counts down) and 6a (star burst), 6b (Home / Map / Stickers), 6c (Meet / Trace / Quiz) built; rest of 6 to come |

Plans 01–04 are detailed (the agreed scope: steps 1+2). Plans 05–08 are outlines, to be sharpened with `grill-with-docs` before their step starts.

## Environment facts (checked 2026-10-02)

- JDK 17.0.12, Android SDK with `platforms/android-37.0`, build-tools 36.0.0 and 37.0.0, cmdline-tools present.
- No Gradle on PATH, no `ffmpeg`, **no emulator and no device known**. So instrumented tests and `e2e.sh` cannot run locally yet.
- Repo: `github.com/Danish8321/huroofi`, branch `main`, handoff pack committed.

## Open questions (need an answer before the plan that owns them)

1. **compileSdk** (01): use 37 (the only installed platform, preview-style numbering) or install 36 via `sdkmanager`? Default: install 36, target 36.
2. **Compose UI tests** (02, 04): no emulator. Options: (a) Robolectric as a dependency for JVM-run Compose tests, (b) install emulator + system image, (c) previews and pure-logic tests only. Needs your call: new dependency or new tooling. Default: (c) until you choose.
3. **Silent audio placeholders** (03): no ffmpeg. Default: commit one hand-built minimal silent MP3 and copy it to each file name via a script.
4. **Unit tests for Android types** (03): DataStore tests run on the JVM with a temp file, no extra dependency.
5. **Toddler prompt audio names** (03, 05): HANDOFF lists the prompts but no file names. Default naming proposed in plan 03.

## Decisions (2026-10-02, user)

1. compileSdk/targetSdk = 36 (install platform 36 via sdkmanager).
2. UI testing: previews and pure-logic tests only; no Robolectric. Visual behaviour reported as unverified.
3. Prompt audio names in plan 03 accepted.
4. Harakat default off.
5. Plans 01-04 approved; scope of first build = HANDOFF steps 1+2.
6. Parent zone shows a static Offline mode row (plan 06 slice 3); no download logic.
7. Device: emulator (to be installed locally: emulator + system image; unblocks test-full.sh/e2e.sh).
8. Play Console / signing: last stage, after demo.
9. App icon: build the best we can (design pass, user approves).
10. Audio recordings: user supplies later; silent placeholders until then.
11. Trace stroke paths: superseded by plan 07 decision 3 (trace over the letter glyph; no path data).
12. Mode switch: applies on leaving the Parent zone, which opens the stored mode's home (amended 2026-10-04, was next app start).
13. Vowelled data: plan 06 stores the ḥarakāt setting only. Plan 07 defers vowelled data to a later plan (needs a native-reviewed list); the zone notes this under the toggle. Letter shapes: built from tatweel in code (plan 07 decision 2).
