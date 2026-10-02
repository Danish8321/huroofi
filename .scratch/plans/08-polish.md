# 08 Tablet, immersive, accessibility, Families check (outline) — HANDOFF step 6

1. **Tablet layouts**: portrait + landscape via window size classes; `ToddlerFindTablet` pattern; Arabic sentence 60 sp on tablet.
2. **Immersive mode** on child screens; system bars return in Parent zone. Back handling already centralised (plan 05 slice 6).
3. **Accessibility pass**: content description audit (lint rule + a unit test over component API), contrast AA check of all token pairs (pure test), layouts at font scale 1.3 (needs Compose UI tests or device: open question 2), TalkBack order sanity.
4. **Families-policy check**: read current Google Play Families policy at that time; confirm dependency list (Jetpack/Kotlin only), no INTERNET permission, no advertising ID, data-safety answers, target-audience declaration. Output: `docs/families-check.md` with the sources and date.
5. **Illustrations**: decide SVG→VectorDrawable vs PNG (PNG used by default, APK-size check).
6. **Release prep**: signing config stays out of the repo; app icon from approved art (none supplied: ask).
