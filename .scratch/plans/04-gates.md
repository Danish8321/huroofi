# 04 Verification gates

CLAUDE.md: no "done/works/fixed" without a script under `.claude/scripts/`. This repo has none yet. This plan creates the ones that can exist and states the ones that cannot. Build **first**, before 01 task 5.

| Script | Exists? | Does |
|---|---|---|
| `check.sh` | create | `./gradlew assembleDebug lintDebug`, fails on any lint error. Lint baseline files forbidden (no silencing). |
| `test-fast.sh` | create | `./gradlew testDebugUnitTest` (JVM only). |
| `contract.sh` | create | Runs a Python/Kotlin-free check: `data/letters.json` == `app/src/main/assets/letters.json` byte-for-byte; all 28 card PNGs, 28 illustration PNGs and both fonts in the pack have a matching app resource; every audio path in the JSON has a file under `assets/audio` (placeholder or real). Plus the strict-decode unit test (03). |
| `test-full.sh` | **not yet** | Needs instrumented/Compose UI tests: blocked on open question 2 (Robolectric vs emulator). Until then no claim may cite it. |
| `schema.sh` | **n/a** | No database. DataStore key rules are in plan 03 and enforced by unit tests; there is no migration tool to wrap. Revisit if Room is ever added (needs your approval, new dependency). |
| `e2e.sh` | **not yet** | No emulator or device. Needs `emulator` + system image, or a connected phone, then `connectedDebugAndroidTest`. |

## Rules

- Scripts: POSIX `sh`, run from repo root, fail non-zero, print the decisive line last. Work in Git Bash on Windows (`./gradlew` via `sh`, `ANDROID_HOME` auto-detected from `%LOCALAPPDATA%/Android/Sdk` if unset).
- A gate that cannot run says so in my report: e.g. "assembleDebug passes; behaviour on a device unverified".
- No `@Suppress`, `lint.xml` blanket ignore, `abortOnError false`, or baseline to make a gate pass. Fix cause or escalate.

## Tasks

1. `check.sh`, `test-fast.sh` (need 01 skeleton to be meaningful; create scripts with 01 task 3).
2. `contract.sh` (with 03 task 1).
3. Optional CI later (GitHub Actions running the same scripts) — only on request.
