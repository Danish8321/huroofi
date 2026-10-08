// ---------- data ----------
const LETTERS = DATA.letters;
const STAGES = DATA.stages;
const STROKES = Object.fromEntries(DATA.strokes.letters.map((l) => [l.index, l.strokes]));
const NON_JOINING = 'أدذرزو';
const ZWJ = '‍';
const L = (i) => LETTERS[i - 1];
const stageOf = (n) => STAGES[n - 1];
const stageLetters = (n) => LETTERS.filter((l) => l.stage === n);
const img = (name) => IMG + name + '.png';
const pick = (arr) => arr[Math.floor(Math.random() * arr.length)];
const shuffle = (arr) => arr.map((v) => [Math.random(), v]).sort((a, b) => a[0] - b[0]).map((x) => x[1]);
const cap = (s) => s.charAt(0).toUpperCase() + s.slice(1);
const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));

// ---------- state ----------
const PRESETS = {
  fresh: { label: 'First day', learned: [] },
  mid: { label: 'Learning ب', learned: [1] },
  stageEnd: { label: 'One letter to a sticker', learned: [1, 2, 3] },
  halfway: { label: 'Halfway (stage 4)', learned: [...Array(12)].map((_, i) => i + 1) },
  done: { label: 'All 28 learned', learned: [...Array(28)].map((_, i) => i + 1) }
};
const S = {
  mode: 'preschool', learned: new Set([1]), stickers: new Set(),
  voice: true, vib: true, minutes: 20, unlockAll: false, traceOnly: false,
  timeUp: false, calm: false, device: 'phone', staticView: false
};
function applyPreset(key) {
  S.learned = new Set(PRESETS[key].learned);
  S.stickers = new Set(STAGES.filter((s) => stageComplete(s.stage)).map((s) => s.stage));
}
const isLearned = (i) => S.learned.has(i);
const stageComplete = (n) => stageLetters(n).every((l) => isLearned(l.index));
// Stage 1 is always open; stage n+1 opens once stage n is complete.
function openStage() {
  for (const s of STAGES) if (!stageComplete(s.stage)) return s.stage;
  return null;
}
function learningLetter() {
  const n = openStage();
  return n ? stageLetters(n).find((l) => !isLearned(l.index)) : null;
}
let reviewPick = null;
function todaysLetter() {
  const l = learningLetter();
  if (l) return { letter: l, review: false };
  if (!reviewPick) reviewPick = pick(LETTERS);
  return { letter: reviewPick, review: true };
}
function stageState(n) {
  if (stageComplete(n)) return 'finished';
  if (n === openStage()) return 'current';
  return S.unlockAll ? 'open' : 'locked';
}
const homeRoute = () => (S.mode === 'toddler' ? 'thome' : 'home');

// ---------- feedback captions (audio is not recorded yet) ----------
let captionTimer;
function say(...clips) {
  const el = document.getElementById('caption');
  if (!el || S.staticView) return;
  const text = clips.filter(Boolean).map((c) => `<span class="clip">${c}</span>`).join('<span class="then">then</span>');
  el.innerHTML = `${ICON.speaker}<span class="cap-label">${S.voice ? 'Plays' : 'Voice off, would play'}</span>${text}`;
  el.classList.remove('flash'); void el.offsetWidth; el.classList.add('flash');
  clearTimeout(captionTimer);
}
function buzz(kind) {
  if (!S.vib || S.staticView) return;
  const el = document.getElementById('buzz');
  if (!el) return;
  el.textContent = kind === 'tick' ? 'Buzz: tick' : 'Buzz: confirm';
  el.classList.remove('show'); void el.offsetWidth; el.classList.add('show');
}
const AR = (s, cls = '') => `<span dir="rtl" class="ar ${cls}">${s}</span>`;
const clipAr = (s) => `<span dir="rtl" class="ar">${s}</span>`;

// ---------- icons ----------
const sv = (d, w = 2.4) => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="${w}" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${d}</svg>`;
const ICON = {
  home: sv('<path d="M3 11l9-7 9 7"/><path d="M5 10v10h14V10"/><path d="M10 20v-5h4v5"/>'),
  lock: sv('<rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V8a4 4 0 0 1 8 0v3"/>'),
  speaker: sv('<path d="M4 9v6h4l5 4V5L8 9H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>'),
  back: sv('<path d="M15 5l-7 7 7 7"/>', 2.8),
  next: sv('<path d="M9 5l7 7-7 7"/>', 2.8),
  close: sv('<path d="M6 6l12 12M18 6L6 18"/>', 2.8),
  check: sv('<path d="M5 12.5l4.5 4.5L19 7.5"/>', 3),
  eye: sv('<path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>'),
  pencil: sv('<path d="M4 20l4-1 11-11-3-3L5 16z"/><path d="M14 6l3 3"/>'),
  play: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 5.5v13l10.5-6.5z" fill="currentColor"/></svg>',
  again: sv('<path d="M3 12a9 9 0 1 0 3-6.7"/><path d="M3 4v5h5"/>'),
  hand: sv('<path d="M9 11V5.5a1.5 1.5 0 0 1 3 0V11"/><path d="M12 10.5V4.5a1.5 1.5 0 0 1 3 0v6"/><path d="M15 10.5V6.5a1.5 1.5 0 0 1 3 0V14a7 7 0 0 1-7 7h-.5a6 6 0 0 1-4.8-2.4L3.4 15a1.6 1.6 0 0 1 2.4-2.1L9 15.5V9.5"/>', 2),
  map: sv('<path d="M9 4L3 6v14l6-2 6 2 6-2V4l-6 2-6-2z"/><path d="M9 4v14M15 6v14"/>', 2.2),
  sticker: sv('<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9z"/>', 2.2),
  minus: sv('<path d="M6 12h12"/>', 3),
  plus: sv('<path d="M12 6v12M6 12h12"/>', 3),
  shield: sv('<path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z"/><path d="M8.5 12l2.5 2.5 4.5-5"/>', 2.2),
  moon: sv('<path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z"/>', 2.2),
  exit: sv('<path d="M15 4h4v16h-4"/><path d="M10 8l-4 4 4 4"/><path d="M6 12h10"/>')
};
const STAR_PATH = 'M12 2.8l2.8 5.8 6.3.9-4.6 4.4 1.1 6.3L12 17.2l-5.6 3 1.1-6.3L2.9 9.5l6.3-.9z';
const starSvg = (size, fill = '#FFC93C') => `<svg width="${size}" height="${size}" viewBox="0 0 24 24" aria-hidden="true"><path d="${STAR_PATH}" fill="${fill}" stroke="#E0A400" stroke-width="1.2" stroke-linejoin="round"/></svg>`;

// Celebration: stars fly out from a point. Only ever shown on success.
const BURST = [[-120, -130, 44], [0, -170, 52], [118, -128, 40], [-140, -20, 36], [140, -30, 46], [-110, 80, 40], [110, 86, 34], [-40, -80, 26], [56, -70, 30], [30, 120, 28]];
function burst(scale = 1, top = '44%') {
  return `<span class="burst" aria-hidden="true" style="--top:${top}">${BURST.map(([dx, dy, s], i) =>
    `<span class="burst-star" style="--dx:${Math.round(dx * scale)}px;--dy:${Math.round(dy * scale)}px;--rot:${(i % 2 ? 1 : -1) * (20 + i * 9)}deg;animation-delay:${(i * 0.05).toFixed(2)}s,1s">${starSvg(Math.round(s * Math.max(scale, 0.6)), i % 3 ? '#FFC93C' : '#FFE07A')}</span>`).join('')}</span>`;
}
function wiggle(el, cls = 'wiggle') {
  if (!el || S.calm) return;
  el.classList.remove(cls); void el.offsetWidth; el.classList.add(cls);
}

// ---------- router ----------
const SCREENS = {};
let route = { name: 'home', p: {} };
let timers = [];
const later = (fn, ms) => { const t = setTimeout(fn, ms); timers.push(t); return t; };
const CHILD_ROUTES = ['thome', 'tcards', 'tfind', 'tpaint', 'home', 'map', 'stickers', 'lesson', 'trace', 'practice', 'quiz', 'reward'];
function go(name, p = {}) {
  timers.forEach(clearTimeout); timers = [];
  route = { name, p };
  render();
}
function render() {
  const phone = document.getElementById('screen');
  let name = route.name;
  if (S.timeUp && CHILD_ROUTES.includes(name)) name = 'rest';
  const scr = SCREENS[name];
  phone.className = 'screen screen-' + name + (S.calm ? ' calm' : '');
  phone.innerHTML = scr.view(route.p);
  phone.scrollTop = 0;
  scr.mount && scr.mount(phone, route.p);
  document.dispatchEvent(new CustomEvent('routed', { detail: { name, p: route.p } }));
}
// What the phone's system Back gesture does on each screen.
function systemBack() {
  const n = S.timeUp && CHILD_ROUTES.includes(route.name) ? 'rest' : route.name;
  const p = route.p;
  const toddler = S.mode === 'toddler';
  const table = {
    thome: null, rest: null, closed: null,
    tcards: ['thome'], tfind: ['thome'], tpaint: ['thome'],
    home: ['gate'], map: ['home'], stickers: ['home'], lesson: ['home'], quiz: ['home'], practice: ['home'],
    trace: ['lesson', { i: p.i }], reward: ['map'],
    gate: [toddler ? 'thome' : 'home'], zone: [null]
  };
  const t = table[n];
  if (!t) return say('Back is ignored here');
  if (n === 'zone') return leaveZone();
  go(t[0], t[1] || {});
}
function leaveZone() {
  reviewPick = null;
  go(homeRoute());
}

// ---------- shared pieces ----------
function bottomNav(active) {
  const item = (id, label, icon, to) => `<button type="button" class="nav-item ${active === id ? 'on' : ''}" data-go="${to}" ${active === id ? 'aria-current="page"' : ''}>${icon}<span>${label}</span></button>`;
  return `<nav class="bottom-nav">${item('home', 'Home', ICON.home, 'home')}${item('map', 'Map', ICON.map, 'map')}${item('stickers', 'Stickers', ICON.sticker, 'stickers')}${item('parents', 'Parents', ICON.lock, 'gate')}</nav>`;
}
function stepBar(step) {
  const steps = [['meet', 'Meet', ICON.eye], ['trace', 'Trace', ICON.pencil], ['play', 'Play', ICON.play]];
  const idx = steps.findIndex((s) => s[0] === step);
  return `<div class="steps" aria-label="Step ${idx + 1} of 3">${steps.map(([id, label, icon], i) => {
    const cls = i < idx ? 'done' : i === idx ? 'now' : 'todo';
    const line = i < steps.length - 1 ? `<span class="step-line ${i < idx ? 'done' : ''}"></span>` : '';
    return `<span class="step ${cls}">${i < idx ? ICON.check : icon}${i === idx ? `<b>${label}</b>` : ''}</span>${line}`;
  }).join('')}</div>`;
}
function stickerBadge(n, size = 120, earned = true) {
  const st = stageOf(n);
  if (!earned) return `<span class="sticker empty" style="width:${size}px;height:${size}px;border-color:${st.border};background:${st.pastel}"><span style="color:${st.accent_dark}">?</span></span>`;
  return `<span class="sticker" style="width:${size}px;height:${size}px;border-color:${st.border};box-shadow:0 6px 0 ${st.accent_dark}"><img src="${img(stageOf(n).picture)}" alt=""></span>`;
}
const pictureWord = (l, cls = '') => `<span dir="rtl" class="ar word ${cls}"><span class="first" style="color:${stageOf(l.stage).accent_dark}">${l.word_first}</span>${l.word_rest}</span>`;
function bindGo(root) {
  root.querySelectorAll('[data-go]').forEach((b) => b.addEventListener('click', () => {
    const to = b.dataset.go;
    go(to, b.dataset.i ? { i: +b.dataset.i } : b.dataset.stage ? { stage: +b.dataset.stage } : {});
  }));
}
