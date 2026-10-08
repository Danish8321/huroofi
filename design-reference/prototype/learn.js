// ================= Preschool / Early reader =================
const startLetter = (l) => (S.traceOnly ? go('practice', { i: l.index }) : go('lesson', { i: l.index }));

// ---------- Home ----------
const SLOTS = [[90, 430], [200, 330], [250, 200], [110, 92]];
SCREENS.home = {
  title: 'Home',
  view: () => {
    const { letter: today, review } = todaysLetter();
    const n = today.stage, st = stageOf(n);
    const letters = stageLetters(n);
    const learnedCount = letters.filter((l) => isLearned(l.index)).length;
    const nodes = letters.map((l, k) => {
      const [x, y] = SLOTS[k];
      if (l === today) {
        const picLeft = x > 170 ? x - 150 : x + 62;
        return `<button type="button" class="node today" style="left:${x - 56}px;top:${y - 56}px" aria-label="Today's letter ${l.name_latin}. Tap to hear">${AR(l.letter)}</button>
          <span class="today-chip" style="left:${x - 30}px;top:${y + 60}px">Today</span>
          <button type="button" class="node-pic" style="left:${picLeft}px;top:${y - 44}px" aria-label="${cap(l.meaning_en)}. Tap to hear"><img src="${img(l.picture)}" alt=""></button>`;
      }
      const learned = isLearned(l.index);
      return `<span class="node ${learned ? 'learned' : 'togo'}" style="left:${x - 34}px;top:${y - 34}px;${learned ? '' : `border-color:${st.border};color:${st.accent_dark}`}" aria-label="${l.name_latin}, ${learned ? 'learned' : 'coming up'}">${AR(l.letter)}</span>`;
    }).join('');
    const label = S.traceOnly ? 'Trace' : review ? 'Practise' : "Let's learn";
    return `<div class="learn-wrap">
      <header class="home-head">
        <span class="avatar"><img src="${img('lion')}" alt=""></span>
        <span class="home-hello"><b>${review ? "Let's practise a letter!" : 'Hi there!'}</b><span>Stage ${n} · ${st.name} · ${learnedCount} of 4</span></span>
      </header>
      <div class="path-card" style="background:${st.pastel};border-color:${st.border}">
        <svg viewBox="0 0 340 500" preserveAspectRatio="none" aria-hidden="true"><path d="M90 430 C 40 380, 120 340, 200 330 S 320 250, 250 200 S 60 150, 110 92" fill="none" stroke="#FFFFFF" stroke-width="30" stroke-linecap="round"/><path d="M90 430 C 40 380, 120 340, 200 330 S 320 250, 250 200 S 60 150, 110 92" fill="none" stroke="${st.border}" stroke-opacity="0.45" stroke-width="4" stroke-linecap="round" stroke-dasharray="2 14"/></svg>
        ${nodes}
        <span class="prize" aria-label="${st.name} sticker${S.stickers.has(n) ? ', earned' : ', waiting at the end of this stage'}">${stickerBadge(n, 70, S.stickers.has(n))}</span>
      </div>
      <button type="button" class="primary-btn go-btn">${label} ${AR(today.letter, 'btn-ar')}${ICON.next}</button>
      ${bottomNav('home')}</div>`;
  },
  mount: (root) => {
    bindGo(root);
    const { letter: l } = todaysLetter();
    const hear = (el) => { wiggle(el); say(clipAr(l.name_ar), clipAr(l.word_ar)); };
    root.querySelector('.node.today').addEventListener('click', (e) => hear(e.currentTarget));
    root.querySelector('.node-pic').addEventListener('click', (e) => hear(e.currentTarget));
    root.querySelector('.go-btn').addEventListener('click', () => startLetter(l));
  }
};

// ---------- Letter map ----------
let lastFinishedPick = null;
SCREENS.map = {
  title: 'Letter map',
  view: () => {
    const learned = S.learned.size;
    const cards = STAGES.map((s) => {
      const state = stageState(s.stage);
      const letters = stageLetters(s.stage);
      const pic = `<span class="map-pic"><img src="${img(s.picture)}" alt=""></span>`;
      const name = `<span class="map-name"><span class="map-num" style="color:${state === 'current' ? s.accent_dark : ''}">Stage ${s.stage}</span><b>${s.name}</b>${state !== 'current' ? `<span dir="rtl" class="ar map-chars">${letters.map((l) => l.letter).join(' ')}</span>` : ''}</span>`;
      if (state === 'current') {
        const ll = learningLetter();
        const chips = letters.map((l) => {
          const cls = isLearned(l.index) ? 'learned' : l === ll ? 'learning' : 'togo';
          return `<span class="map-chip ${cls}" aria-label="${l.name_latin}, ${cls === 'learned' ? 'learned' : cls === 'learning' ? 'today' : 'coming up'}">${AR(l.letter)}</span>`;
        }).join('');
        return `<div class="map-card current" style="background:${s.pastel};border-color:${s.border};box-shadow:0 6px 0 ${s.accent_dark}">
          <div class="map-row">${pic}${name}<span class="map-count" style="color:${s.accent_dark}">${letters.filter((l) => isLearned(l.index)).length}/4</span></div>
          <div class="map-chips" dir="rtl">${chips}</div>
          <button type="button" class="primary-btn small map-go" data-stage="${s.stage}">${S.traceOnly ? 'Trace' : 'Continue with'} ${AR(S.traceOnly ? letters[0].letter : ll.letter, 'btn-ar')}</button></div>`;
      }
      if (state === 'finished') {
        return `<button type="button" class="map-card finished" data-stage="${s.stage}" aria-label="Stage ${s.stage}, ${s.name}, finished. Tap to play a letter again">
          <div class="map-row">${pic}${name}<span class="map-badge done">${ICON.check}</span></div></button>`;
      }
      if (state === 'open') {
        return `<button type="button" class="map-card open" data-stage="${s.stage}" aria-label="Stage ${s.stage}, ${s.name}. Start">
          <div class="map-row">${pic}${name}<span class="map-badge play">${ICON.play}</span></div></button>`;
      }
      return `<button type="button" class="map-card locked" data-stage="${s.stage}" aria-label="Stage ${s.stage}, ${s.name}, locked">
        <div class="map-row">${pic}${name}<span class="map-badge">${ICON.lock}</span></div></button>`;
    }).join('');
    return `<div class="learn-wrap scroll">
      <header class="page-head"><h1>Letter map</h1><span>${learned} of 28 letters learned · ${S.unlockAll ? 'start any stage' : 'finish one stage to unlock the next'}</span></header>
      <div class="map-list"><span class="map-line" aria-hidden="true"></span>${cards}</div>
      ${bottomNav('map')}</div>`;
  },
  mount: (root) => {
    bindGo(root);
    root.querySelectorAll('.map-card[data-stage], .map-go').forEach((c) => c.addEventListener('click', () => {
      const n = +c.dataset.stage;
      const letters = stageLetters(n);
      const state = stageState(n);
      if (state === 'locked') { wiggle(c); say('boing'); return; }
      if (S.traceOnly) return go('practice', { i: letters[0].index });
      if (state === 'current') return go('lesson', { i: learningLetter().index });
      if (state === 'open') return go('lesson', { i: letters.find((l) => !isLearned(l.index)).index });
      const again = pick(letters.filter((l) => l !== lastFinishedPick));
      lastFinishedPick = again;
      go('lesson', { i: again.index });
    }));
  }
};

// ---------- Sticker book ----------
SCREENS.stickers = {
  title: 'Sticker book',
  view: () => `<div class="learn-wrap scroll">
    <header class="page-head"><h1>Sticker book</h1><span>${S.stickers.size} of 7 stickers · finish a stage to earn one</span></header>
    <div class="sticker-grid">${STAGES.map((s) => {
      const earned = S.stickers.has(s.stage);
      return earned
        ? `<button type="button" class="sticker-slot" data-stage="${s.stage}" aria-label="${s.name} sticker. Tap to hear its letters">${stickerBadge(s.stage, 112, true)}<b>${s.name}</b></button>`
        : `<div class="sticker-slot empty" aria-label="${s.name} sticker, not earned yet">${stickerBadge(s.stage, 112, false)}<span>${s.name}</span></div>`;
    }).join('')}</div>
    ${bottomNav('stickers')}</div>`,
  mount: (root) => {
    bindGo(root);
    root.querySelectorAll('.sticker-slot[data-stage]').forEach((b) => b.addEventListener('click', () => {
      wiggle(b.querySelector('.sticker'));
      say(...stageLetters(+b.dataset.stage).map((l) => clipAr(l.name_ar)));
    }));
  }
};

// ---------- Meet (Lesson) ----------
function shapes(l) {
  const ch = l.letter;
  const all = [['Alone', ch], ['Start', ch + ZWJ], ['Middle', ZWJ + ch + ZWJ], ['End', ZWJ + ch]];
  return NON_JOINING.includes(ch) ? [all[0], all[3]] : all;
}
SCREENS.lesson = {
  title: 'Meet the letter',
  view: ({ i }) => {
    const l = L(i), st = stageOf(l.stage);
    const reader = S.mode === 'reader';
    return `<div class="learn-wrap lesson ${reader ? 'reader' : ''}">
      <div class="flow-head"><button type="button" class="round-btn" data-go="home" aria-label="Back to home">${ICON.back}</button>${stepBar('meet')}</div>
      <div class="letter-card" style="background:${st.pastel};border-color:${st.border};box-shadow:0 8px 0 ${st.accent_dark}">
        <span class="letter-name"><b>${l.name_latin}</b> ${AR(l.name_ar)}</span>
        <button type="button" class="sun-btn lesson-sound" aria-label="Hear ${l.name_latin} and ${l.meaning_en}">${ICON.speaker}</button>
        <button type="button" class="big-letter" aria-label="Letter ${l.name_latin}. Tap to hear">${AR(l.letter)}</button>
      </div>
      <button type="button" class="word-card" aria-label="${cap(l.meaning_en)}. Tap to hear">
        <span class="word-pic"><img src="${img(l.picture)}" alt=""></span>
        <span class="word-text">${pictureWord(l, 'lesson-word')}<span class="meaning">${l.meaning_en}</span></span>
      </button>
      ${reader ? `<div class="shapes-card"><div class="shapes-head"><b>${AR(l.letter)} in words</b><span class="reader-chip">Early reader</span></div>
        <div class="shapes" dir="rtl">${shapes(l).map(([n, s], k) => `<span class="shape ${k === 0 ? 'alone' : ''}" aria-label="${n} shape">${AR(s)}<small dir="ltr">${n}</small></span>`).join('')}</div></div>` : ''}
      <button type="button" class="primary-btn lesson-next" data-go="trace" data-i="${i}">Trace it ${ICON.next}</button>
    </div>`;
  },
  mount: (root, { i }) => {
    bindGo(root);
    const l = L(i);
    say(clipAr(l.name_ar), clipAr(l.word_ar));
    root.querySelector('.lesson-sound').addEventListener('click', () => say(clipAr(l.name_ar), clipAr(l.word_ar)));
    const big = root.querySelector('.big-letter');
    big.addEventListener('click', () => { wiggle(big); say(clipAr(l.name_ar)); });
    const wc = root.querySelector('.word-card');
    wc.addEventListener('click', () => { wiggle(wc.querySelector('.word-pic')); say(clipAr(l.word_ar)); });
  }
};

// ---------- Trace (and Trace practice) ----------
const TRACE_W = 350, TRACE_H = 420, DOT_STEP = 18, TOLERANCE = 30, COVER = 1, BRUSH_SLACK = 4;
const PAINTS = [...CRAYONS, ['Pink', '#FF6FA5', '#D94C84']];
let trace = null;
function newTrace(i, practice) {
  trace = { i, practice, ink: [], paint: 0, covered: null, finished: [], areaPts: null, area: null, done: false, demoDone: false };
}
// The letter's area, and a brush as wide as the letter: a trace down the middle of every stroke
// paints the whole letter; wandering off the middle leaves gaps to colour in.
function letterArea(g, strokes) {
  trace.areaPts = g.area();
  trace.area = trace.areaPts.map(() => false);
  const line = strokes.flatMap((s) => resample(s, 2));
  const reach = Math.max(...trace.areaPts.map((q) => Math.min(...line.map((p) => Math.hypot(p[0] - q[0], p[1] - q[1])))));
  trace.reach = Math.max(13, reach + BRUSH_SLACK);
}
function traceView({ i }, practice) {
  if (!trace || trace.i !== i || trace.practice !== practice) newTrace(i, practice);
  const l = L(i), st = stageOf(l.stage);
  const g = fitGlyph(l.letter, TRACE_W, TRACE_H, 0.8);
  const strokes = (STROKES[i] || []).map((s) => ({ ...s, pts: s.points.map(g.at) }));
  const dots = strokes.map((s) => resample(s.pts, DOT_STEP));
  if (!trace.covered) { trace.covered = dots.map((d) => d.map(() => false)); trace.finished = dots.map(() => false); }
  if (!trace.areaPts) letterArea(g, strokes.map((s) => s.pts));
  const nextStroke = trace.finished.indexOf(false);
  const done = trace.done;
  // Every stroke followed but some of the letter still unpainted: show the gaps.
  const filling = !done && nextStroke < 0;
  const gapEls = filling ? trace.areaPts.map((p, k) => trace.area[k] ? '' : `<circle class="tgap" data-a="${k}" cx="${p[0].toFixed(1)}" cy="${p[1].toFixed(1)}" r="7"/>`).join("") : "";
  const dotEls = dots.map((ds, si) => ds.map((p, k) => `<circle class="tdot ${trace.covered[si][k] ? 'on' : ''} ${si === nextStroke || nextStroke < 0 ? "" : "later"}" data-s="${si}" data-k="${k}" cx="${p[0].toFixed(1)}" cy="${p[1].toFixed(1)}" r="${trace.covered[si][k] ? 6 : 4.5}"/>`).join('')).join('');
  const arrows = strokes.map((s, si) => {
    if (s.pts.length < 2) return '';
    const [ax, ay] = s.pts[s.pts.length - 1], [bx, by] = s.pts[s.pts.length - 2];
    const a = Math.atan2(ay - by, ax - bx), r = 13;
    const pt = (t) => `${(ax + r * Math.cos(a + t)).toFixed(1)},${(ay + r * Math.sin(a + t)).toFixed(1)}`;
    return `<polygon class="tarrow ${si === nextStroke ? '' : 'later'}" points="${pt(0)} ${pt(2.5)} ${pt(-2.5)}" fill="${si === 0 ? '#158048' : '#1F6FE0'}"/>`;
  }).join('');
  const coins = strokes.map((s, si) => {
    const [x, y] = s.pts[0];
    if (trace.finished[si]) return `<g transform="translate(${(x - 15).toFixed(1)} ${(y - 15).toFixed(1)})">${starSvg(30).replace('<svg ', '<svg x="0" y="0" ')}</g>`;
    return `<g class="coin ${si === nextStroke ? 'next' : 'later'}" transform="translate(${x.toFixed(1)} ${y.toFixed(1)})"><circle r="15" fill="${si === 0 ? '#158048' : '#1F6FE0'}" stroke="#FFFFFF" stroke-width="3"/><text y="6" text-anchor="middle" font-size="17" font-weight="800" fill="#FFFFFF" font-family="Baloo Bhaijaan 2, system-ui">${si + 1}</text></g>`;
  }).join('');
  const ink = trace.ink.map((s) => `<path d="${svgPath(s.pts.length === 1 ? [s.pts[0], s.pts[0]] : s.pts)}" stroke="${s.color}" stroke-width="${(trace.reach * 2).toFixed(1)}" fill="none" stroke-linecap="round" stroke-linejoin="round"/>`).join('');
  const hint = done ? (practice ? 'Well done! Tap Next for another letter' : 'Well done! Tap Play for the picture game') : filling ? 'Now colour in the yellow spots!' : 'Start at the green 1, then follow the dots!';
  const head = practice
    ? `<div class="flow-head"><button type="button" class="round-btn" data-go="home" aria-label="Back to home">${ICON.back}</button><span class="practice-title">Trace practice</span></div>`
    : `<div class="flow-head"><button type="button" class="round-btn" data-go="lesson" data-i="${i}" aria-label="Back to Meet">${ICON.back}</button>${stepBar('trace')}</div>`;
  const nextBtn = done
    ? `<button type="button" class="primary-btn ready trace-next">${practice ? 'Next letter' : 'Play'} ${ICON.next}</button>`
    : `<button type="button" class="primary-btn locked" disabled aria-label="Next is locked until ${l.name_latin} is traced">${ICON.lock} Finish ${AR(l.letter, 'btn-ar')} first</button>`;
  return `<div class="learn-wrap trace ${done ? 'done' : ''}">
    ${head}
    <button type="button" class="hint-bubble ${done ? 'ok' : ''}" aria-label="${hint} Tap to hear and see how">${ICON.speaker.replace('<svg', '<svg class="hint-ico"')}<span>${hint}</span></button>
    <div class="trace-card ${done ? 'done' : ''}">
      <svg class="trace-svg" viewBox="0 0 ${TRACE_W} ${TRACE_H}" aria-label="${done ? `You traced ${l.name_latin}` : `Trace the letter ${l.name_latin}`}">
        <defs><clipPath id="traceClip">${g.svgText('')}</clipPath></defs>
        ${g.svgText('fill="#C8DCF4" stroke="#13294B" stroke-width="3" stroke-linejoin="round"')}
        <g clip-path="url(#traceClip)" class="ink">${ink}</g>
        <g class="dots">${dotEls}</g>${arrows}${coins}<g class="gaps">${gapEls}</g>
        ${HAND_SVG}
      </svg>
      ${done ? burst(1) + `<span class="done-badge"><img src="${img('lion')}" alt=""><span>You traced ${AR(l.letter)}!</span></span>` : ''}
    </div>
    <div class="paints" role="radiogroup" aria-label="Paint colour">${PAINTS.map(([n, c, d], k) => `<button type="button" role="radio" class="paint ${k === trace.paint ? 'on' : ''}" aria-checked="${k === trace.paint}" aria-label="${n}" data-k="${k}" style="background:${c}"></button>`).join('')}</div>
    <div class="trace-foot"><button type="button" class="square-btn trace-again" aria-label="Again: clear and show me how">${ICON.again}</button>${nextBtn}</div>
  </div>`;
}
function traceMount(root, { i }, practice) {
  bindGo(root);
  const l = L(i);
  const svg = root.querySelector('.trace-svg');
  const g = fitGlyph(l.letter, TRACE_W, TRACE_H, 0.8);
  const strokes = (STROKES[i] || []).map((s) => s.points.map(g.at));
  const dots = strokes.map((s) => resample(s, DOT_STEP));
  const hand = svg.querySelector('.demo-hand');
  let stopDemo = null, idle = null;
  const stop = () => { stopDemo && stopDemo(); stopDemo = null; clearTimeout(idle); };
  const demo = (which) => { stop(); stopDemo = demoHand(hand, which, armIdle); };
  // After 5 s without a touch, show only the next unfinished stroke again.
  const armIdle = () => { clearTimeout(idle); if (trace.done) return; idle = later(() => { const n = trace.finished.indexOf(false); if (n >= 0) demo([strokes[n]]); else say('fill_hint'); }, 5000); };
  root.querySelectorAll('.paint').forEach((b) => b.addEventListener('click', () => { trace.paint = +b.dataset.k; render(); }));
  root.querySelector('.trace-again').addEventListener('click', () => { newTrace(i, practice); render(); });
  const hintClip = () => say(trace.done ? 'praise' : trace.finished.every(Boolean) ? 'fill_hint' : 'trace_hint');
  root.querySelector('.hint-bubble').addEventListener('click', () => { hintClip(); if (!trace.done) demo(strokes); });
  if (trace.done) {
    root.querySelector('.trace-next').addEventListener('click', () => {
      if (practice) go('practice', { i: (i % 28) + 1 }); else { quiz = null; go('quiz', { i }); }
    });
    return;
  }
  if (!trace.demoDone && !trace.ink.length) {
    trace.demoDone = true;
    say(clipAr(l.name_ar), 'trace_hint');
    later(() => demo(strokes), 1400);
  } else armIdle();

  const inkG = svg.querySelector('.ink');
  let cur = null;
  const coverArea = (a, b) => {
    // Everything within the brush of the segment a–b is painted.
    const len = Math.hypot(b[0] - a[0], b[1] - a[1]) || 1;
    trace.areaPts.forEach((q, k) => {
      if (trace.area[k]) return;
      const t = Math.max(0, Math.min(1, ((q[0] - a[0]) * (b[0] - a[0]) + (q[1] - a[1]) * (b[1] - a[1])) / (len * len)));
      if (Math.hypot(q[0] - (a[0] + t * (b[0] - a[0])), q[1] - (a[1] + t * (b[1] - a[1]))) > trace.reach) return;
      trace.area[k] = true;
      const el = svg.querySelector(`.tgap[data-a="${k}"]`);
      if (el) el.remove();
    });
  };
  const cover = (p) => {
    dots.forEach((ds, si) => ds.forEach((d, k) => {
      if (trace.covered[si][k] || Math.hypot(d[0] - p[0], d[1] - p[1]) > TOLERANCE) return;
      trace.covered[si][k] = true;
      const c = svg.querySelector(`.tdot[data-s="${si}"][data-k="${k}"]`);
      if (c) { c.classList.add('on'); c.setAttribute('r', 6); }
    }));
  };
  svg.addEventListener('pointerdown', (e) => {
    stop();
    try { svg.setPointerCapture(e.pointerId); } catch (err) { /* pointer already gone */ }
    const p = svgPoint(svg, e);
    cur = { color: PAINTS[trace.paint][1], pts: [p] };
    trace.ink.push(cur);
    cur.el = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    Object.entries({ stroke: cur.color, 'stroke-width': trace.reach * 2, fill: 'none', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }).forEach(([k, v]) => cur.el.setAttribute(k, v));
    cur.el.setAttribute('d', svgPath([p, p]));
    inkG.appendChild(cur.el);
    cover(p);
    coverArea(p, p);
  });
  svg.addEventListener('pointermove', (e) => {
    if (!cur) return;
    const p = svgPoint(svg, e);
    coverArea(cur.pts[cur.pts.length - 1], p);
    cur.pts.push(p);
    cur.el.setAttribute('d', svgPath(cur.pts));
    cover(p);
  });
  // Completion is checked when the finger lifts: a stroke counts once every one of its dots is covered, any order or direction.
  // Covered dots stay green across touches, so a child can go back and fill in the ones they missed.
  const up = () => {
    if (!cur) return;
    cur = null;
    let changed = false;
    trace.covered.forEach((cv, si) => {
      if (!trace.finished[si] && cv.filter(Boolean).length / cv.length >= COVER) { trace.finished[si] = true; changed = true; buzz('tick'); }
    });
    // Done only when every stroke is followed and the whole letter is painted.
    if (trace.finished.every(Boolean) && trace.area.every(Boolean)) {
      trace.done = true;
      buzz('confirm');
      say('cheer', 'praise');
      render();
      return;
    }
    if (changed) { if (trace.finished.every(Boolean)) say('fill_hint'); render(); } else armIdle();
  };
  svg.addEventListener('pointerup', up);
  svg.addEventListener('pointercancel', up);
}
SCREENS.trace = { title: 'Trace it', view: (p) => traceView(p, false), mount: (r, p) => traceMount(r, p, false) };
SCREENS.practice = { title: 'Trace practice', view: (p) => traceView(p, true), mount: (r, p) => traceMount(r, p, true) };

// ---------- Play (Quiz) ----------
const ROUNDS = 3;
let quiz = null;
function newQuizRound(i, round) {
  const l = L(i);
  const n = S.mode === 'reader' ? 4 : 3;
  const others = shuffle(LETTERS.filter((x) => x.word_first !== l.letter && x.index !== i)).slice(0, n - 1);
  let options;
  do { options = shuffle([l, ...others]); } while (quiz && quiz.i === i && options.indexOf(l) === quiz.slot && n > 1);
  quiz = { i, round, options, slot: options.indexOf(l), solved: false, wrong: null, saved: quiz && quiz.i === i ? quiz.saved : null };
}
SCREENS.quiz = {
  title: 'Picture game',
  view: ({ i }) => {
    if (!quiz || quiz.i !== i) { quiz = null; newQuizRound(i, 1); }
    const l = L(i);
    const reader = S.mode === 'reader';
    const dots = [...Array(ROUNDS)].map((_, k) => {
      const r = k + 1;
      const cls = r < quiz.round || (r === quiz.round && quiz.solved) ? 'done' : r === quiz.round ? 'now' : '';
      return `<span class="qdot ${cls}"></span>`;
    }).join('');
    const cards = quiz.options.map((o) => {
      const right = quiz.solved && o === l;
      const dim = quiz.wrong === o && !quiz.solved;
      return `<button type="button" class="q-card ${right ? 'right' : ''} ${dim ? 'dim' : ''} ${quiz.solved && !right ? 'faded' : ''}" data-i="${o.index}" aria-label="${cap(o.meaning_en)}">
        <img src="${img(o.picture)}" alt="">${right ? pictureWord(o, 'q-word') + `<span class="q-star">${starSvg(34)}</span>` : ''}</button>`;
    }).join('');
    let foot;
    if (quiz.solved) {
      const last = quiz.round === ROUNDS;
      const label = !last ? 'Great job! Next' : quiz.saved === 'sticker' ? 'Get your sticker' : quiz.saved === 'review' ? 'Back home' : quiz.saved === 'all' ? 'See your sticker' : 'Next letter';
      foot = `<button type="button" class="primary-btn success q-next">${label} ${ICON.next}</button>`;
    } else if (quiz.wrong) {
      const w = quiz.wrong;
      foot = `<div class="almost" role="status"><b>Almost! Try again</b><span>${cap(w.meaning_en)} (${AR(w.word_ar)}) starts with ${AR(w.word_first)}</span></div>`;
    } else foot = `<p class="q-hint">${ICON.hand}<span>Listen, then tap a picture</span></p>`;
    return `<div class="learn-wrap quiz ${reader ? 'four' : ''}">
      <div class="quiz-head"><button type="button" class="round-btn" data-go="home" aria-label="Leave game">${ICON.close}</button><span class="qdots" aria-label="Question ${quiz.round} of ${ROUNDS}">${dots}</span><span class="head-gap"></span></div>
      <div class="leo-row small"><img class="leo" src="${img('lion')}" alt="Leo the lion">
        <button type="button" class="bubble q-bubble" aria-label="Hear the question"><span class="sun-btn">${ICON.speaker}</span><span class="q-ask">Which starts with</span><span class="q-letter">${AR(l.letter)}</span></button></div>
      <div class="q-cards">${cards}</div>
      <div class="q-foot">${foot}</div></div>`;
  },
  mount: (root, { i }) => {
    bindGo(root);
    const l = L(i);
    const ask = () => say('which_starts_with', clipAr(l.name_ar));
    if (!quiz.solved && !quiz.wrong) ask();
    root.querySelector('.q-bubble').addEventListener('click', ask);
    root.querySelectorAll('.q-card').forEach((c) => c.addEventListener('click', () => {
      if (quiz.solved) return;
      const o = L(+c.dataset.i);
      if (o === l) {
        quiz.solved = true;
        say('praise', clipAr(l.word_ar));
        buzz('confirm');
        if (quiz.round === ROUNDS) {
          // The letter is learned on the third right answer; a stage's sticker is given in the same step.
          if (isLearned(i)) quiz.saved = 'review';
          else {
            S.learned.add(i);
            if (stageComplete(l.stage)) { S.stickers.add(l.stage); quiz.saved = S.learned.size === 28 ? 'all' : 'sticker'; }
            else quiz.saved = 'next';
          }
        }
        render();
        return;
      }
      quiz.wrong = o;
      say('boing');
      render();
      const right = document.querySelector(`.q-card[data-i="${i}"]`);
      later(() => wiggle(right), 300);
    }));
    const nx = root.querySelector('.q-next');
    if (nx) nx.addEventListener('click', () => {
      if (quiz.round < ROUNDS) { newQuizRound(i, quiz.round + 1); render(); return; }
      const saved = quiz.saved;
      quiz = null; reviewPick = null;
      if (saved === 'sticker' || saved === 'all') return go('reward', { stage: l.stage });
      if (saved === 'review') return go('home');
      go('lesson', { i: learningLetter().index });
    });
  }
};

// ---------- Stage complete (Reward) ----------
SCREENS.reward = {
  title: 'Stage complete',
  view: ({ stage }) => {
    const n = stage || 1, st = stageOf(n), next = STAGES[n];
    const unlocked = next && stageState(next.stage) !== 'locked';
    const all = S.learned.size === 28;
    const conf = ['#EC4F8C', '#3BAA5C', '#1F6FE0', '#8A6CE8', '#FFC93C', '#F07D2A'].map((c, k) => `<span class="confetti" style="background:${c};left:${8 + k * 16}%;animation-delay:${k * 0.2}s"></span>`).join('');
    const after = all
      ? `<div class="unlock-card" style="border-color:#158048;background:#E3F5EA"><span class="unlock-pic">${starSvg(56)}</span><span class="unlock-text"><small style="color:#0F5F35">ALL DONE</small><b>All 28 letters learned!</b></span></div>`
      : unlocked
        ? `<div class="unlock-card" style="border-color:${next.border};background:${next.pastel}"><span class="unlock-pic"><img src="${img(next.picture)}" alt=""></span><span class="unlock-text"><small style="color:${next.accent_dark}">UNLOCKED</small><b>${next.name}</b></span>${AR(stageLetters(next.stage).map((l) => l.letter).join(' '), 'unlock-ar')}</div>`
        : '';
    return `<div class="learn-wrap reward" style="background:${st.pastel}">
      ${S.calm ? '' : conf}
      <h1 class="reward-title">${st.name} done!</h1>
      <button type="button" class="medal" aria-label="${st.name} sticker. Tap to hear its letters"><span class="rays" style="--ray:${st.border}"></span>${stickerBadge(n, 190, true)}<span class="new-chip">NEW STICKER</span></button>
      <div class="reward-letters" dir="rtl">${stageLetters(n).map((l) => `<span class="reward-letter">${AR(l.letter)}<img src="${img(l.picture)}" alt=""></span>`).join('')}</div>
      <p class="reward-sub">4 letters learned · added to your sticker book</p>
      ${after}
      <div class="reward-foot"><button type="button" class="square-btn" data-go="stickers" aria-label="Open the sticker book">${ICON.sticker}</button>
        <button type="button" class="primary-btn" data-go="map">${unlocked && !all ? `Go to ${next.name}` : 'Back to map'} ${ICON.next}</button></div></div>`;
  },
  mount: (root, { stage }) => {
    bindGo(root);
    say('cheer');
    buzz('confirm');
    const m = root.querySelector('.medal');
    m.addEventListener('click', () => { wiggle(m.querySelector('.sticker')); say(...stageLetters(stage || 1).map((l) => clipAr(l.name_ar))); });
  }
};
