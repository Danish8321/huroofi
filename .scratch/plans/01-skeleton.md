# 01 Project skeleton

Goal: an empty Compose app that builds, installs-ready, with project layout fixed. Thinnest slice: `assembleDebug` succeeds and a single `HuroofiApp()` shows "Huroofi" in the right fonts. Persistence/API tiers: none yet.

## Decisions

- Gradle project at repo root, one module `:app`, package `com.huroofi.app`, Kotlin DSL, version catalog `gradle/libs.versions.toml`.
- Single activity `MainActivity` → `HuroofiApp()` → `NavHost` (one placeholder route for now).
- `minSdk 29` (Android 10+), `targetSdk` = `compileSdk` = 36 (see index open question 1).
- App name "Huroofi", launcher label English, portrait on phones, free rotation on tablets (`screenOrientation` handled by `sw600dp` resource qualifier for a boolean).
- Dependencies, all first-party Jetpack/Kotlin: Compose BOM, material3, navigation-compose, activity-compose, lifecycle-runtime-compose, datastore-preferences, kotlinx-serialization-json, kotlinx-coroutines. Test: junit4, kotlinx-coroutines-test. **No** Media3, no Hilt, no image loader (assets are local drawables). Exact versions: look up latest stable at implementation time (Context7/Maven), pin in the catalog.
- Permissions: none. No INTERNET permission (offline rule, and it makes Families review easier).
- Backup: `android:allowBackup="false"` (child data stays on device).

## Asset placement (one-time copy, verified by `contract.sh`)

| Pack path | App path |
|---|---|
| `data/letters.json` | `app/src/main/assets/letters.json` |
| `assets/cards/*.png` | `app/src/main/res/drawable-nodpi/card_<nn>_<name>.png` (lowercase, `_`) |
| `assets/illustrations/png/*.png` | `app/src/main/res/drawable-nodpi/pic_<name>.png` |
| `assets/illustrations/svg/*.svg` | kept in pack only; PNG used first (SVG→VectorDrawable conversion is lossy risk, revisit in 08) |
| `assets/fonts/*.ttf` | `app/src/main/res/font/baloo_bhaijaan_2.ttf`, `noto_naskh_arabic.ttf` |

Why copy instead of a Gradle copy task: simplest, visible in review. `contract.sh` guards drift (plan 04).

## Tasks

1. `git` hygiene: `.gitignore` (Android, `.gradle`, `local.properties`, `build/`, `.idea`, scratchpad).
2. Bootstrap Gradle wrapper (download Gradle distribution to scratchpad, run `gradle wrapper`), commit wrapper files.
3. `settings.gradle.kts`, root + app `build.gradle.kts`, catalog, manifest, `MainActivity`, `HuroofiApp`.
4. Copy assets per table; `local.properties` with `sdk.dir` (not committed).
5. First gate run: `check.sh` passes (plan 04 must exist first, so plan 04 task 1 precedes this).

## Verification

`check.sh` → `./gradlew assembleDebug lintDebug` green. Evidence = command output.

## Out of scope

Real screens, navigation graph, audio, storage.
