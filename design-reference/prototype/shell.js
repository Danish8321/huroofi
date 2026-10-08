// ================= Review shell: screen list, notes, controls, design system =================
const GROUPS = [
  ['Toddler mode', ['thome', 'tcards', 'tfind', 'tpaint']],
  ['Preschool and Early reader', ['home', 'lesson', 'trace', 'quiz', 'reward', 'map', 'stickers', 'practice']],
  ['Grown-ups and limits', ['gate', 'zone', 'rest']]
];
const NOTES = {
  thome: { file: 'ToddlerHome', from: 'App start in Toddler mode; Home button on toddler screens.', rules: ['Leo asks “ماذا نلعب؟” on open; tapping the bubble asks again.', 'Three activity tiles, each a preview of its game. Every tile is far above 64 dp.', 'The small lock is a tap; the hold happens on the parent gate.', 'System Back does nothing here.'] },
  tcards: { file: 'ToddlerCards', from: 'Toddler home, “اسمع” tile.', rules: ['All 28 letters in alphabet order; wraps at both ends.', 'On arrival: letter, then word. Tap the letter for the letter, the picture or sound button for the word; both wiggle.', '96 dp arrows plus swipe. The two small pictures between the arrows show what comes before and after.', 'No page dots: 28 dots are noise to a toddler.'] },
  tfind: { file: 'ToddlerFind', from: 'Toddler home, “ابحث” tile.', rules: ['Asks only for toddler words (21 of 28). Two pictures; the target never repeats the last one.', 'Wrong tap: that picture nudges, the right one wiggles, a soft “boing”. The first wrong tap of a round also repeats the question. No X, no red, nothing lost.', 'Right tap: stars burst from the picture, praise plays, vibration.', 'No star counter and no sets of three. After the celebration the Next button fills up for 4 seconds, then the next round starts by itself; tapping Next skips the wait.', 'Tablets in landscape get the side-by-side layout (switch Device to Tablet).'] },
  tpaint: { file: 'ToddlerPaint', from: 'Toddler home, “ارسم” tile.', rules: ['A random letter from all 28, never the same twice in a row. “لوّن ال…” plays on each new letter.', 'A hand shows the strokes once; the first touch stops it.', 'Fat 30 dp brush, 4 crayons, wipe. Paint only shows on the letter.', 'No star meter. After about 400 dp of paint on the letter: green frame, star burst, Leo cheers.', 'Then a ring counts down 4 seconds and a new letter starts by itself; “Paint it again” keeps the same letter.'] },
  home: { file: 'Main', from: 'App start in Preschool or Early reader mode; Home tab.', rules: ['No name and no star count: the app stores no profile, and stickers are the only reward.', 'The stage path shows this stage’s 4 letters: learned green, today’s big and blue, the rest waiting. The stage sticker waits at the end of the path.', 'Tapping today’s letter or picture plays it; nothing plays on open.', 'One button: “Let’s learn ب”. With Trace only on it reads “Trace ب”; once all 28 are learned it offers a random letter to practise.', 'System Back opens the parent gate.'] },
  lesson: { file: 'Lesson', from: 'Home button; Map; after the previous letter’s game.', rules: ['Step bar: Meet, Trace, Play.', 'Plays letter, then word, on open. The big letter, the picture and the sound button replay.', 'Picture word with its first letter in the stage colour, plus the English meaning for parents.', 'Early reader adds the letter shapes card (Alone, Start, Middle, End; non-joining letters show Alone and End only). Switch mode in the controls to see it.', '“Trace it” goes on; Back goes home.'] },
  trace: { file: 'Trace', from: 'Meet, “Trace it”.', rules: ['Plays the letter, then the spoken hint, then a hand shows every stroke in order.', 'Guide: pale blue letter with a navy outline, dots every 18 dp, numbered start coins (1 is green), arrows at stroke ends. The next stroke is bright; later strokes are faded.', 'Dots turn green under the finger (30 dp tolerance). A stroke counts only when every one of its dots is green (100 %), in any order or direction; its coin turns into a star. Missed dots stay white, and green ones stay green, so the child can go back and fill in the gaps.', 'The letter is done only when the paint also covers the whole letter shape, not just the dotted line (a fine grid inside the letter; the soft 4 dp edge does not count). The brush is as wide as the letter, so a trace down the middle fills it; wandering off the middle leaves gaps. Once every stroke is followed, any unpainted spots pulse yellow and Leo says “Now colour in the yellow spots!”.', 'No stars while tracing and no “I did it!” button. Next stays grey and locked (“Finish ب first”) until every stroke is done.', 'When done: star burst, “You traced ب!”, and Next turns blue and pulses. It never moves on by itself.', 'After 5 s without a touch the hand shows the next stroke again. Again clears the ink and replays the demo.'] },
  practice: { file: 'Trace (practice)', from: 'Home or Map while Trace only is on.', rules: ['Same tracing as Trace, without the step bar.', 'Practice never makes a letter learned, never gives a sticker and never unlocks a stage.', 'Next letter goes to the following letter in the alphabet, only when tapped.'] },
  quiz: { file: 'Quiz', from: 'Trace, “Play”.', rules: ['Asks “Which starts with ب?” on open; the bubble asks again.', 'Pictures only. Preschool 3 in a column; Early reader 4 in a 2×2 grid. The right picture never sits in the same place two rounds running.', 'Three rounds, shown as three dots. No star counter.', 'Wrong: soft “boing”, the picture dims, the right one wiggles, and an orange “Almost! Try again” explains what the tapped picture starts with. Unlimited tries.', 'Right: green frame, a star pops, the word appears, praise and vibration. Then “Great job! Next”, only when tapped.', 'The third right answer makes the letter learned. The button then reads “Next letter”, or “Get your sticker” when the stage is complete.'] },
  reward: { file: 'Reward', from: 'The game’s last round when it completes a stage.', rules: ['The stage sticker is the hero; tapping it says the stage’s letters. Confetti unless calm motion is on.', 'The four letters with their pictures, then the next stage shown as unlocked.', 'No stars or points: the sticker is the only reward.', '“Go to Carrot Farm” opens the map; the square button opens the sticker book. “Back to map” when nothing new unlocked.'] },
  map: { file: 'StageMap', from: 'Map tab; Reward.', rules: ['Seven stages in a list. The current stage is open wide with its 4 letters and “Continue with ب”.', 'Finished stages get a green tick; tapping one replays Meet for a random letter of that stage.', 'Locked stages are compact with a lock; tapping one gives a “boing” and a wiggle.', 'With Unlock all on, unfinished stages get a play button instead of a lock.'] },
  stickers: { file: 'Stickers', from: 'Stickers tab; Reward.', rules: ['One sticker per stage, seven in all.', 'Earned stickers are filled in; tapping one wiggles and says its letters.', 'The rest are gentle empty slots with a question mark, not greyed-out failures.'] },
  gate: { file: 'ParentGate', from: 'Every lock, the Parents tab, Back on the preschool home, the lock on Rest.', rules: ['“Back to play” comes first and is the biggest thing on screen.', 'Press and hold the circle for 2 seconds; the ring fills. Letting go resets it.', 'Once unlocked: “Open Parent zone” or “Close Huroofi”.'] },
  zone: { file: 'Parents', from: 'Parent gate only.', rules: ['Your child: stage, a progress bar with learned / learning / to go, and all 28 letters.', 'Learning mode applies when you go back to kid mode.', 'Voice & sounds, Vibration, vowel marks (shown, but locked until the vowelled text is reviewed), daily play time 5–60 min in steps of 5.', 'Lessons: Unlock all and Trace only. Start over asks first, inside the page.', 'No child name, no weekly report, no download button: everything is already on the device.'] },
  rest: { file: 'Rest', from: 'Replaces any child screen once today’s play time is used up.', rules: ['Calm night colours, Leo resting, “وقت الراحة” is spoken once.', 'No countdown and no red. Only the lock leads anywhere.', 'Raising the limit in the Parent zone sends the child back to play. Trace practice counts as play too.'] },
  closed: { file: '', from: 'Parent gate, “Close Huroofi”.', rules: ['Prototype stand-in for leaving the app.'] }
};

// Fixed states used for the static snapshots in design-reference/screens/.
const g2 = () => L(2);
const STATIC = {
  Main: () => { S.mode = 'preschool'; applyPreset('mid'); return ['home']; },
  Lesson: () => { S.mode = 'preschool'; return ['lesson', { i: 2 }]; },
  'Lesson-reader': () => { S.mode = 'reader'; return ['lesson', { i: 2 }]; },
  Trace: () => { newTrace(2, false); trace.demoDone = true; return ['trace', { i: 2 }]; },
  'Trace-done': () => { fillTrace(2); return ['trace', { i: 2 }]; },
  'Trace-fill': () => { fillTrace(2, 10); return ['trace', { i: 2 }]; },
  Quiz: () => { quiz = { i: 2, round: 2, options: [L(4), g2(), L(3)], slot: 1, solved: false, wrong: null, saved: null }; return ['quiz', { i: 2 }]; },
  'Quiz-almost': () => { quiz = { i: 2, round: 2, options: [L(4), g2(), L(3)], slot: 1, solved: false, wrong: L(4), saved: null }; return ['quiz', { i: 2 }]; },
  'Quiz-done': () => { quiz = { i: 2, round: 2, options: [L(4), g2(), L(3)], slot: 1, solved: true, wrong: null, saved: null }; return ['quiz', { i: 2 }]; },
  'Quiz-reader': () => { S.mode = 'reader'; quiz = { i: 2, round: 1, options: [L(4), g2(), L(3), L(11)], slot: 1, solved: false, wrong: null, saved: null }; return ['quiz', { i: 2 }]; },
  Reward: () => { applyPreset('halfway'); S.learned = new Set([1, 2, 3, 4]); S.stickers = new Set([1]); return ['reward', { stage: 1 }]; },
  StageMap: () => { applyPreset('halfway'); S.learned = new Set([1, 2, 3, 4, 5]); S.stickers = new Set([1]); return ['map']; },
  Stickers: () => { S.learned = new Set([...Array(8)].map((_, k) => k + 1)); S.stickers = new Set([1, 2]); return ['stickers']; },
  ParentGate: () => { gateOpen = false; return ['gate']; },
  Parents: () => { S.mode = 'preschool'; applyPreset('mid'); return ['zone']; },
  ToddlerHome: () => { S.mode = 'toddler'; return ['thome']; },
  ToddlerCards: () => { cardIndex = 2; return ['tcards']; },
  ToddlerFind: () => { find = { target: g2(), options: [g2(), L(3)], solved: false, wrongOnce: false }; return ['tfind']; },
  'ToddlerFind-done': () => { find = { target: g2(), options: [g2(), L(3)], solved: true, wrongOnce: false }; return ['tfind']; },
  ToddlerFindTablet: () => { S.device = 'tablet'; find = { target: g2(), options: [g2(), L(3)], solved: true, wrongOnce: false }; return ['tfind']; },
  ToddlerPaint: () => { paint = { letter: g2(), crayon: 1, strokes: [], dist: 0, done: false, demoShown: true }; return ['tpaint']; },
  'ToddlerPaint-done': () => { fillPaint(2); return ['tpaint']; },
  Rest: () => { S.timeUp = true; return ['home']; }
};
// shift > 0: every stroke followed that far off-centre, so part of the letter is still unpainted.
function fillTrace(i, shift = 0) {
  newTrace(i, false);
  const g = fitGlyph(L(i).letter, TRACE_W, TRACE_H, 0.8);
  const strokes = STROKES[i].map((s) => s.points.map(g.at));
  trace.ink = strokes.map((pts) => ({ color: PAINTS[0][1], pts: pts.map(([x, y]) => [x, y + shift]) }));
  trace.covered = strokes.map((s) => resample(s, DOT_STEP).map(() => true));
  trace.finished = strokes.map(() => true);
  letterArea(g, strokes);
  const ink = trace.ink.flatMap((s) => resample(s.pts, 2));
  trace.area = trace.areaPts.map((q) => ink.some((p) => Math.hypot(p[0] - q[0], p[1] - q[1]) <= trace.reach));
  trace.done = trace.area.every(Boolean); trace.demoDone = true;
}
function fillPaint(i) {
  const g = fitGlyph(L(i).letter, PAINT_W, PAINT_H, 0.78);
  const strokes = STROKES[i].map((s) => s.points.map(g.at));
  paint = { letter: L(i), crayon: 1, strokes: strokes.map((pts, k) => ({ color: CRAYONS[k % 2 ? 1 : 0][1], pts: pts.length === 1 ? [pts[0], pts[0]] : pts })), dist: 999, done: true, demoShown: true };
}

// ---------- shell ----------
function screenList() {
  return GROUPS.map(([g, ids]) => `<div class="nav-group"><h3>${g}</h3>${ids.map((id) => `<button type="button" class="nav-link" data-route="${id}"><span>${NOTES[id] && SCREENS[id].title}</span></button>`).join('')}</div>`).join('');
}
function routeParams(id) {
  if (['lesson', 'trace', 'quiz', 'practice'].includes(id)) return { i: todaysLetter().letter.index };
  if (id === 'reward') return { stage: Math.max(1, ...[...S.stickers], 1) };
  return {};
}
function syncShell(name) {
  document.querySelectorAll('.nav-link').forEach((b) => b.classList.toggle('on', b.dataset.route === name));
  const n = NOTES[name] || NOTES.closed;
  document.getElementById('notes').innerHTML = `<h2>${SCREENS[name].title}</h2>
    ${n.file ? `<p class="note-file">Reference: <code>screens/${n.file}.html</code></p>` : ''}
    <p class="note-from"><b>Reached from</b> ${n.from}</p><ul>${n.rules.map((r) => `<li>${r}</li>`).join('')}</ul>`;
  const tablet = S.device === 'tablet' && name === 'tfind';
  document.getElementById('device').classList.toggle('tablet', tablet);
  document.getElementById('device-note').hidden = !(S.device === 'tablet' && name !== 'tfind');
  fitDevice();
  try { history.replaceState(null, '', '#' + name); } catch (e) { /* sandboxed */ }
  syncControls();
}
function syncControls() {
  const set = (id, v) => { const el = document.getElementById(id); if (el) el.value = v; };
  set('ctl-mode', S.mode);
  set('ctl-device', S.device);
  const t = document.getElementById('ctl-time'); if (t) t.checked = S.timeUp;
  const c = document.getElementById('ctl-calm'); if (c) c.checked = S.calm;
  document.getElementById('progress-line').textContent = `${S.learned.size} of 28 learned · ${S.stickers.size} of 7 stickers`;
}
function fitDevice() {
  const stage = document.getElementById('stage');
  const dev = document.getElementById('device');
  const holder = document.getElementById('device-holder');
  const w = dev.classList.contains('tablet') ? 1180 : 390, h = dev.classList.contains('tablet') ? 820 : 844;
  const availW = stage.clientWidth - 8;
  const availH = window.innerWidth > 900 ? window.innerHeight - 150 : 4000;
  const scale = Math.min(1, availW / (w + 24), availH / (h + 24));
  dev.style.transform = `scale(${scale})`;
  holder.style.width = (w + 24) * scale + 'px';
  holder.style.height = (h + 24) * scale + 'px';
}
function designSystem() {
  const sw = (name, hex, use) => `<div class="swatch"><span style="background:${hex}"></span><b>${name}</b><code>${hex}</code><small>${use}</small></div>`;
  return `<div class="ds">
    <section><h2>Colour</h2><div class="swatches">
      ${sw('navy', '#13294B', 'Text, big letters')}${sw('muted', '#4A5D7A', 'Secondary text')}${sw('sky', '#E8F4FF', 'Child background')}
      ${sw('primary', '#1F6FE0', 'Main buttons')}${sw('primary shadow', '#1555B0', '3D edge')}${sw('sun', '#FFC93C', 'Sound buttons, stars')}
      ${sw('success', '#158048', 'Right answer, done')}${sw('guide', '#C8DCF4', 'Trace letter band')}${sw('almost', '#FFE4D6', 'Gentle wrong hint')}
      ${sw('parent bg', '#F4F7FB', 'Parent zone')}${sw('gate bg', '#13294B', 'Parent gate')}${sw('rest', '#24345E', 'Rest screen')}</div></section>
    <section><h2>Stage families</h2><div class="stage-fams">${STAGES.map((s) => `<div class="fam" style="background:${s.pastel};border-color:${s.border}"><img src="${img(STAGE_PIC[s.stage])}" alt=""><b style="color:${s.accent_dark}">${s.stage}. ${s.name}</b><span dir="rtl" class="ar">${s.letters.join(' ')}</span></div>`).join('')}</div></section>
    <section><h2>Type</h2><div class="type-scale">
      <p style="font:800 30px/1.1 var(--app-en)">Screen title 26–36</p><p style="font:800 22px/1.2 var(--app-en)">Primary button 22</p><p style="font:800 20px/1.2 var(--app-en)">Section heading 20</p><p style="font:600 18px/1.3 var(--app-en)">Body 18 · never below 16</p>
      <p><span dir="rtl" class="ar" style="font-size:42px">أين البطة؟</span> <small>Toddler sentence 34–42</small></p><p><span dir="rtl" class="ar" style="font-size:96px;line-height:1.3">ب</span> <small>Lesson letter (170 on device)</small></p></div></section>
    <section><h2>Buttons and controls</h2><div class="ds-demo screen">
      <button type="button" class="primary-btn">Let’s learn ${AR('ب', 'btn-ar')}${ICON.next}</button>
      <button type="button" class="primary-btn success">Great job! Next ${ICON.next}</button>
      <button type="button" class="primary-btn locked" disabled>${ICON.lock} Finish ${AR('ب', 'btn-ar')} first</button>
      <div class="ds-row"><button type="button" class="sun-btn big" aria-label="Sound">${ICON.speaker}</button><button type="button" class="round-btn" aria-label="Back">${ICON.back}</button><button type="button" class="square-btn" aria-label="Again">${ICON.again}</button><button type="button" class="parent-lock" aria-label="Parent lock">${ICON.lock}</button>${ringNext('Countdown Next')}</div>
      <button type="button" class="fill-next" aria-label="Fill Next"><span class="fill"></span><span class="fill-icon">${ICON.next}</span></button></div></section>
    <section><h2>Rules every screen follows</h2><ul class="ds-rules">
      <li>Stars appear only to celebrate a success. No counters or meters while a child is working.</li>
      <li>Preschool and Early reader: Next is locked until the step is done and never moves on by itself.</li>
      <li>Toddler: after the celebration the next item starts by itself, with a visible countdown; tapping Next skips the wait.</li>
      <li>Every instruction is spoken. No X, no red, nothing lost on a wrong tap.</li>
      <li>Touch targets at least 48 dp, and 64 dp in Toddler mode. Text never below 16 sp.</li>
      <li>Calm motion (the phone’s “Remove animations”) drops confetti, wiggles and pulses.</li></ul></section></div>`;
}
function buildShell() {
  document.getElementById('screen-list').innerHTML = screenList();
  document.querySelectorAll('.nav-link').forEach((b) => b.addEventListener('click', () => { showView('screens'); go(b.dataset.route, routeParams(b.dataset.route)); }));
  document.getElementById('ds').innerHTML = designSystem();
  const on = (id, ev, fn) => document.getElementById(id).addEventListener(ev, fn);
  on('ctl-mode', 'change', (e) => { S.mode = e.target.value; reviewPick = null; quiz = null; go(homeRoute()); });
  on('ctl-progress', 'change', (e) => { applyPreset(e.target.value); reviewPick = null; render(); });
  on('ctl-time', 'change', (e) => { S.timeUp = e.target.checked; render(); });
  on('ctl-calm', 'change', (e) => { S.calm = e.target.checked; render(); });
  on('ctl-device', 'change', (e) => { S.device = e.target.value; if (S.device === 'tablet' && route.name !== 'tfind') { S.mode = 'toddler'; go('tfind'); } else render(); });
  on('ctl-back', 'click', systemBack);
  on('ctl-reset', 'click', () => { Object.assign(S, { mode: 'preschool', voice: true, vib: true, minutes: 20, unlockAll: false, traceOnly: false, timeUp: false, calm: false, device: 'phone' }); applyPreset('mid'); trace = quiz = find = paint = null; gateOpen = false; go('home'); });
  document.querySelectorAll('[data-view]').forEach((b) => b.addEventListener('click', () => showView(b.dataset.view)));
  document.addEventListener('routed', (e) => syncShell(e.detail.name));
  document.addEventListener('settings', syncControls);
  window.addEventListener('resize', fitDevice);
  document.getElementById('ctl-progress').innerHTML = Object.entries(PRESETS).map(([k, v]) => `<option value="${k}">${v.label}</option>`).join('');
  document.getElementById('ctl-progress').value = 'mid';
}
function showView(v) {
  document.querySelectorAll('[data-view]').forEach((b) => b.setAttribute('aria-pressed', String(b.dataset.view === v)));
  document.getElementById('stage-screens').hidden = v !== 'screens';
  document.getElementById('ds').hidden = v !== 'ds';
  if (v === 'screens') fitDevice();
}

async function boot() {
  await chooseArabicFont();
  await document.fonts.ready;
  const hash = decodeURIComponent(location.hash.slice(1));
  if (hash.startsWith('static-')) {
    // Snapshot mode: only the device, no review shell. Used to write design-reference/screens/*.html.
    S.staticView = true;
    const key = hash.slice(7);
    const [name, p] = STATIC[key]();
    document.body.classList.add('static');
    document.getElementById('shell').remove();
    const dev = document.createElement('div');
    dev.id = 'screen';
    document.body.appendChild(dev);
    route = { name, p: p || {} };
    const tablet = S.device === 'tablet';
    let n = name;
    if (S.timeUp && CHILD_ROUTES.includes(n)) n = 'rest';
    dev.className = `screen screen-${n} static-${tablet ? 'tablet' : 'phone'}`;
    dev.innerHTML = SCREENS[n].view(route.p);
    document.title = 'Huroofi – ' + key;
    document.body.dataset.ready = '1';
    return;
  }
  buildShell();
  showView('screens');
  const start = SCREENS[hash] && hash !== 'closed' ? hash : 'home';
  if (['thome', 'tcards', 'tfind', 'tpaint'].includes(start)) S.mode = 'toddler';
  go(start, routeParams(start));
}
boot();
