# Google Play Families check

**Status: DRAFT.** The maintainer reviews this and fills in Play Console; nothing here has been submitted. Written for plan 08 task 8.2 (decision 12).

Read on **2026-10-05**. None of the three pages shows a last-updated date. Read them again before submitting, because Google changes them without notice.

| Key | Source |
|---|---|
| F | Families Policies: <https://support.google.com/googleplay/android-developer/answer/9893335> |
| T | Target audience and app content: <https://support.google.com/googleplay/android-developer/answer/9867159> |
| D | Data safety section: <https://support.google.com/googleplay/android-developer/answer/10787469> |

Every row below is checked against the code at the commit that adds this file. Rows marked **Open** need the maintainer before release.

## Requirements and Huroofi's answers

| # | Requirement (short quote) | Source | Huroofi's answer | Evidence |
|---|---|---|---|---|
| 1 | "You must indicate the target audience for your app ... by selecting from the list of age groups provided." | F, T | Ages 5 and under, and Ages 6-8. Toddler (18 m – 3 y), Preschool (3 – 5 y) and Early reader (5 y+) are all inside those groups. | `HANDOFF.md` modes; Parent zone mode labels in `ParentZoneScreen.kt` |
| 2 | "Only select more than one age group ... if you have designed your app for ... users within the selected age group(s)." | F | Yes. Each group has its own mode; Early reader covers 6–8. Ages 9-12 is not selected. | Mode switch in the Parent zone |
| 3 | Before the target audience section, the developer must have "added a privacy policy." | T, D | **Open.** Huroofi has no privacy policy yet. A short one stating that no data is collected or shared is needed before the Play Console steps (plan 10). | none yet |
| 4 | Developers must have "declared whether or not your app contains ads." | T | Contains ads: **No**. | `apk-audit.sh`: no ad SDK on the release classpath, no `AD_ID` permission |
| 5 | "Your app's content that is accessible to children must be appropriate for children." | F | Yes. Letters, words and pictures from `letters.json`; no user-generated or external content. | `data/letters.json`; `ContentContractTest` |
| 6 | "Your app must not merely provide a webview of a website ..." | F | Native Compose screens; no WebView anywhere. | No `WebView` in `app/src/main` |
| 7 | "You must disclose the collection of any personal and sensitive information from children ... including through APIs and SDKs." | F | Nothing is collected. Progress and settings stay on the device in DataStore. There is no INTERNET permission, so nothing can leave the device. | `apk-audit.sh` (fails on INTERNET); `allowBackup="false"` and every backup domain excluded in `data_extraction_rules.xml` |
| 8 | Apps that solely target children "must not transmit ... AAID, SIM Serial, Build Serial, BSSID, MAC, SSID, IMEI, and/or IMSI." | F | None are read or sent. | No `TelephonyManager`, `ANDROID_ID`, advertising ID, `WifiManager` or `Build.getSerial` in `app/src/main`; no INTERNET |
| 9 | Apps that solely target children "may not request location permission ..." | F | No location permission. | `apk-audit.sh` fails on any location permission |
| 10 | "Device phone number must not be requested from TelephonyManager ..." | F | Not requested. | No `TelephonyManager` |
| 11 | Bluetooth must go through the Companion Device Manager. | F | Not applicable: no Bluetooth. | No Bluetooth API or permission |
| 12 | Apps that solely target children "must not contain any APIs or SDKs that are not approved for use in primarily child-directed services." | F | Only AndroidX, Kotlin and kotlinx libraries, plus their own transitive helpers (okio, listenablefuture, jspecify, JetBrains annotations). No analytics, ads, crash-reporting or networking SDK. | `apk-audit.sh` with `.claude/scripts/allowed-deps.txt` |
| 13 | AR safety warning; social-app reminders and parental controls; no anonymous chat; adult action before sharing personal information. | F | Not applicable: no AR, no social features, no chat, no sharing. | Feature list in `HANDOFF.md` |
| 14 | The app must comply with "COPPA, ... GDPR, and any other applicable laws." | F | Designed to collect nothing, which is the simplest compliant position. **Open:** the maintainer confirms this for the markets chosen at release (legal, not code). | rows 7–9 |
| 15 | Ads rules: certified ads SDKs only, no personalised ads, no full-screen, launch-interstitial or unclosable ads. | F | Not applicable: no ads. | row 4 |
| 16 | "Not providing a distinction between the use of virtual game coins versus real-life money" is prohibited. | F | Not applicable: no purchases and no virtual currency. Stars and stickers are rewards only. | No billing library on the classpath |
| 17 | "You must accurately answer the questions in the Play Console ... and update those answers." | F | The suggested answers below; update them whenever the release classpath or permissions change. Any such change fails `apk-audit.sh` first. | `check.sh` runs `apk-audit.sh` |

## Suggested Data safety answers (source D)

Play counts data as **collected** only when it is "transmitted ... off a user's device". Data that is "accessed only on-device and not transmitted off the device doesn't require disclosure".

| Play Console question | Suggested answer | Why |
|---|---|---|
| Does your app collect or share any of the required user data types? | **No** | No INTERNET permission, no SDK that sends data, and backups are off. |
| Is all user data encrypted in transit? | Not asked once the answer above is No | — |
| Can users request that their data be deleted? | Not asked once the answer above is No. Uninstalling, or clearing app data, removes progress and settings. | Data lives only in app storage |
| Privacy policy link | **Open:** required even with no data (source D: apps "collecting no user data must complete the form and provide a privacy policy link"). | row 3 |

## Suggested target audience declaration (source T)

- **Age groups:** Ages 5 and under; Ages 6-8.
- **Appeal to children:** yes, by design. The store listing art may show children's themes.
- **Ads:** No.
- **Families Policy:** applies to the whole app, and this check covers it.

## What keeps this true

- `check.sh` runs `apk-audit.sh` on every check. It fails on INTERNET, AD_ID, location, camera, microphone or contacts permissions, and on any dependency outside the allow-list.
- Adding a library or permission means updating this document and the Data safety answers in the same change.
