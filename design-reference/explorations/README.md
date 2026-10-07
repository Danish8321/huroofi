# Design explorations (not approved)

Proposals made on 2026-10-07. They are **not** part of the approved set in `../screens/` — do not build from them until one is approved and moved there.

| File | What it proposes |
|---|---|
| `ToddlerWellDone.html` | Toddler praise moment: stars, Leo cheering, sticker earned, spoken "أحسنت!" |
| `ToddlerIntro.html` | Toddler "meet the letter": big letter, picture word, prev / sound / next |
| `HomeRedesign.html` | Preschool home as a winding stage path instead of tiles |
| `ParentProgress.html` | Parent zone weekly report: minutes per day, letters met, stage progress (sample data) |
| `AppIcon.html` | Three app icon concepts |
| `FeatureGraphic.html` | Play Store feature graphic, 1024 × 500 |
| `StoreShot.html` | Play Store screenshot 1, "No wrong answers." |

Redesigns of every screen in `../screens/` (Main is `HomeRedesign.html` above):

| File | Replaces | What changes |
|---|---|---|
| `LessonRedesign.html` | Lesson | Meet / Trace / Play step bar; whole letter card taps to speak; letter shapes marked Reader-only |
| `TraceRedesign.html` | Trace | No stars while tracing; star burst and "You traced ب!" when the letter and dot are complete; Next stays locked until then and never advances by itself (preschool and reader). Tap the canvas to preview the done state; screenshot `TraceRedesign-done.png` |
| `QuizRedesign.html` | Quiz | Leo asks the question; 3 tall picture cards; a wrong tap wiggles the right card, never red; progress is dots (green done, wide blue current), no star counter; stars pop only on the right answer. Screenshot `QuizRedesign-done.png` |
| `RewardRedesign.html` | Reward | Sticker medallion, the 4 letters with pictures, next stage shown as unlocked |
| `StageMapRedesign.html` | StageMap | Current stage expanded with today's letter and Continue; locked stages compact |
| `ParentGateRedesign.html` | ParentGate | "Back to play" first and biggest; 2-second hold ring with a plain-language hint |
| `ParentsRedesign.html` | Parents | Learning modes explained in one line each; vowel-mark preview; play-time chips |
| `ToddlerHomeRedesign.html` | ToddlerHome | Leo speaks the prompt; 3 activity tiles that preview the game; small hold-lock for grown-ups |
| `ToddlerCardsRedesign.html` | ToddlerCards | One huge card that speaks when tapped; 96 dp arrows; dot progress |
| `ToddlerFindRedesign.html` | ToddlerFind | 2 big pictures; no star counter; stars on success, a wiggle hint otherwise; green Next only after success, filling left to right, then the next word starts by itself (or tap Next). Screenshot `ToddlerFindRedesign-done.png` |
| `ToddlerPaintRedesign.html` | ToddlerPaint | Wide paint guide, start tick, pulsing dot, 72 dp crayons, no star meter; on completion a star burst and cheering Leo, then the next letter starts by itself (ring countdown) or on tapping Next. Screenshot `ToddlerPaintRedesign-done.png` |
| `ToddlerFindTabletRedesign.html` | ToddlerFindTablet | Leo and the prompt on the left, pictures side by side; no star counter; after success a 120 dp Next with a ring countdown, same as Paint |

Rules used across these redesigns:
- No star counters or meters while a child is working. Stars appear only as a celebration of success.
- Preschool and reader: Next is locked until the letter or question is done, and never advances by itself.
- Toddler: after the celebration the next item starts by itself, with a visible countdown (ring or filling button); tapping Next skips the wait.

Open questions:
- Arabic copy needs native-speaker review. "أحسنت!" is the masculine form; "أحسنتِ" is feminine.
- Parent zone data (child name, letters, minutes) is sample data.
- `screenshots/` are approximate renders (static expansion, headless Edge), not the design canvas runtime.
