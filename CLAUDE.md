# Huroofi – project guide for Claude Code

- Read `HANDOFF.md` before writing code. It is the source of truth for screens, tokens and rules.
- Target: Android, Kotlin + Jetpack Compose, Material 3, Navigation Compose. Offline, no ads, no third-party analytics in child screens.
- Look and behaviour: match `design-reference/screens/*.html` (approved prototypes). Rebuild natively; never embed them in a WebView.
- Content: load letters, words, stages and colours from `data/letters.json`. Do not hard-code letter lists.
- Fonts: `assets/fonts/` — Baloo Bhaijaan 2 for English, Noto Naskh Arabic Bold for every Arabic string.
- Toddler mode rules are strict: sound first, no fail states, max 2–3 choices, touch targets ≥ 64 dp, parent gate (3-second hold) in front of settings and exit.
- When the design changes, the updated `HANDOFF.md` and reference screens will be dropped into this folder again; re-read them and update the code to match.
