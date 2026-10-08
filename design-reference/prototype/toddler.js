// ================= Toddler mode =================
const toddlerTop = (extra = '') => `<div class="t-top">
  <button type="button" class="t-home" data-go="thome" aria-label="Home">${ICON.home}</button>${extra}
  <button type="button" class="parent-lock" data-go="gate" aria-label="Grown-ups: open the parent gate">${ICON.lock}</button></div>`;

SCREENS.thome = {
  title: 'Toddler home',
  view: () => `<div class="t-wrap">
    <button type="button" class="parent-lock corner" data-go="gate" aria-label="Grown-ups: open the parent gate">${ICON.lock}</button>
    <div class="leo-row">
      <img class="leo" src="${img('lion')}" alt="Leo the lion">
      <button type="button" class="bubble" data-say="prompt" aria-label="What shall we play? Tap to hear again">
        <span class="sun-btn">${ICON.speaker}</span>${AR('ماذا نلعب؟', 'bubble-ar')}
      </button>
    </div>
    <button type="button" class="tile tile-cards" data-go="tcards" aria-label="Look and listen">
      <span class="tile-art"><span class="mini-card tilt-l"><img src="${img('lion')}" alt=""></span><span class="mini-card tilt-r"><img src="${img('duck')}" alt=""></span></span>
      ${AR('اسمع', 'tile-ar')}<span class="tile-go">${ICON.play}</span></button>
    <button type="button" class="tile tile-find" data-go="tfind" aria-label="Find it">
      <span class="tile-art"><span class="mini-pic ok"><img src="${img('duck')}" alt="">${starSvg(28)}</span><span class="mini-pic"><img src="${img('apple')}" alt=""></span></span>
      ${AR('ابحث', 'tile-ar')}<span class="tile-go">${ICON.play}</span></button>
    <button type="button" class="tile tile-paint" data-go="tpaint" aria-label="Paint">
      <span class="tile-art"><span class="mini-paint">${AR('ب')}</span></span>
      ${AR('ارسم', 'tile-ar')}<span class="tile-go">${ICON.play}</span></button>
  </div>`,
  mount: (root) => {
    bindGo(root);
    say(clipAr('ماذا نلعب؟'));
    root.querySelector('[data-say]').addEventListener('click', () => say(clipAr('ماذا نلعب؟')));
  }
};

// ---------- Look & listen ----------
let cardIndex = 2;
SCREENS.tcards = {
  title: 'Look & listen',
  view: () => {
    const l = L(cardIndex), st = stageOf(l.stage);
    const prev = L(((cardIndex + 26) % 28) + 1), next = L((cardIndex % 28) + 1);
    return `<div class="t-wrap" style="background:${st.pastel}">
      ${toddlerTop()}
      <div class="big-card" style="border-color:${st.border};box-shadow:0 10px 0 ${st.accent_dark}">
        <button type="button" class="card-letter" style="background:${st.pastel};color:${st.accent_dark}" aria-label="Letter ${l.name_latin}. Tap to hear">${AR(l.letter)}</button>
        <button type="button" class="card-pic" aria-label="${cap(l.meaning_en)}. Tap to hear the word"><img src="${img(l.picture)}" alt=""></button>
        ${pictureWord(l, 'card-word')}
        <button type="button" class="sun-btn card-sound" aria-label="Hear the word">${ICON.speaker}</button>
      </div>
      <div class="card-nav">
        <button type="button" class="arrow prev" aria-label="Previous picture" style="color:${st.accent_dark}">${ICON.back}</button>
        <span class="peek" aria-hidden="true"><img src="${img(prev.picture)}" alt=""><img src="${img(next.picture)}" alt=""></span>
        <button type="button" class="arrow next" aria-label="Next picture">${ICON.next}</button>
      </div></div>`;
  },
  mount: (root) => {
    bindGo(root);
    const l = L(cardIndex);
    const letterClip = () => say(clipAr(l.name_ar)), wordClip = () => say(clipAr(l.word_ar));
    say(clipAr(l.name_ar), clipAr(l.word_ar));
    const pic = root.querySelector('.card-pic');
    pic.addEventListener('click', () => { wiggle(pic); wordClip(); });
    root.querySelector('.card-sound').addEventListener('click', () => { wiggle(pic); wordClip(); });
    const chip = root.querySelector('.card-letter');
    chip.addEventListener('click', () => { wiggle(chip); letterClip(); });
    const step = (d) => { cardIndex = ((cardIndex - 1 + d + 28) % 28) + 1; render(); };
    root.querySelector('.prev').addEventListener('click', () => step(-1));
    root.querySelector('.next').addEventListener('click', () => step(1));
    let x0 = null;
    const card = root.querySelector('.big-card');
    card.addEventListener('pointerdown', (e) => { x0 = e.clientX; });
    card.addEventListener('pointerup', (e) => { if (x0 !== null && Math.abs(e.clientX - x0) > 60) step(e.clientX < x0 ? 1 : -1); x0 = null; });
  }
};

// ---------- Where's the…? ----------
const TODDLER_POOL = LETTERS.filter((l) => l.toddler_word);
let find = null;
function newFindRound() {
  const prev = find && find.target;
  const target = pick(TODDLER_POOL.filter((l) => l !== prev));
  const other = pick(TODDLER_POOL.filter((l) => l !== target));
  find = { target, options: shuffle([target, other]), solved: false, wrongOnce: false };
}
const wherePrompt = (l) => `أين ال${l.word_ar}؟`;
function findCard(l, tablet) {
  const ok = find.solved && l === find.target;
  return `<button type="button" class="find-card ${ok ? 'right' : ''} ${find.solved && !ok ? 'faded' : ''}" data-i="${l.index}" aria-label="${cap(l.meaning_en)}">
    <img src="${img(l.picture)}" alt="">${ok ? burst(tablet ? 0.9 : 0.75, '50%') : ''}</button>`;
}
function findNext(tablet) {
  if (!find.solved) return tablet ? '' : `<p class="t-hint">${ICON.hand}<span>Tap a picture</span></p>`;
  if (tablet) return ringNext('Next. It starts by itself in a moment');
  return `<button type="button" class="fill-next" aria-label="Next. It starts by itself in a moment"><span class="fill"></span><span class="fill-icon">${ICON.next}</span></button>`;
}
function ringNext(label) {
  return `<button type="button" class="ring-next" aria-label="${label}">
    <svg viewBox="0 0 120 120" aria-hidden="true"><circle cx="60" cy="60" r="54" class="ring-track"/><circle cx="60" cy="60" r="54" class="ring-fill" pathLength="100"/></svg>
    <span class="ring-btn">${ICON.next}</span></button>`;
}
SCREENS.tfind = {
  title: "Where's the…?",
  view: () => {
    if (!find) newFindRound();
    const tablet = S.device === 'tablet';
    const bubble = `<button type="button" class="bubble find-bubble" aria-label="Hear the question again">${AR(wherePrompt(find.target), 'bubble-ar')}<span class="sun-btn">${ICON.speaker}</span></button>`;
    if (tablet) {
      return `<div class="t-wrap tablet-find">
        <div class="tf-side">${toddlerTop()}${bubble}<div class="tf-foot"><img class="leo" src="${img('lion')}" alt="Leo the lion">${findNext(true)}</div></div>
        <div class="tf-cards">${find.options.map((l) => findCard(l, true)).join('')}</div></div>`;
    }
    return `<div class="t-wrap">${toddlerTop()}
      <div class="leo-row"><img class="leo" src="${img('lion')}" alt="Leo the lion">${bubble}</div>
      <div class="find-cards">${find.options.map((l) => findCard(l, false)).join('')}</div>
      <div class="find-foot">${findNext(false)}</div></div>`;
  },
  mount: (root) => {
    bindGo(root);
    const prompt = () => say(clipAr(wherePrompt(find.target)));
    if (!find.solved) prompt();
    root.querySelector('.find-bubble').addEventListener('click', prompt);
    root.querySelectorAll('.find-card').forEach((c) => c.addEventListener('click', () => {
      if (find.solved) return;
      const l = L(+c.dataset.i);
      if (l === find.target) {
        find.solved = true;
        say(pick(['praise_1', 'praise_2', 'praise_3']).replace(/^/, 'praise: '));
        buzz('confirm');
        render();
        return;
      }
      wiggle(c, 'nudge');
      const right = root.querySelector(`.find-card[data-i="${find.target.index}"]`);
      later(() => wiggle(right), 300);
      if (!find.wrongOnce) { find.wrongOnce = true; say('boing', clipAr(wherePrompt(find.target))); } else say('boing');
    }));
    const nextBtn = root.querySelector('.fill-next, .ring-next');
    if (nextBtn) {
      const advance = () => { newFindRound(); render(); };
      nextBtn.addEventListener('click', advance);
      later(advance, 5000); // 1 s to enjoy the stars, then a 4 s countdown
    }
  }
};

// ---------- Finger paint ----------
const CRAYONS = [['Orange', '#FF7A59', '#D9573A'], ['Blue', '#3B8CF0', '#1F64C0'], ['Green', '#3BAA5C', '#22803F'], ['Purple', '#8A6CE8', '#6347C4']];
let paint = null;
function newPaint() {
  const prev = paint && paint.letter;
  paint = { letter: pick(LETTERS.filter((l) => l !== prev)), crayon: paint ? paint.crayon : 1, strokes: [], dist: 0, done: false, demoShown: false };
}
const PAINT_GOAL = 400; // px painted on the letter
const PAINT_W = 334, PAINT_H = 470;
SCREENS.tpaint = {
  title: 'Finger paint',
  view: () => {
    if (!paint) newPaint();
    const l = paint.letter, done = paint.done;
    const g = fitGlyph(l.letter, PAINT_W, PAINT_H, 0.78);
    const ink = paint.strokes.map((s) => `<path d="${svgPath(s.pts)}" stroke="${s.color}" stroke-width="30" fill="none" stroke-linecap="round" stroke-linejoin="round"/>`).join('');
    return `<div class="t-wrap paint-wrap ${done ? 'done' : ''}">
      ${toddlerTop(`<button type="button" class="sun-btn big paint-say" aria-label="Hear: paint the letter ${l.name_latin}">${ICON.speaker}</button>`)}
      <div class="paint-card">
        <svg class="paint-svg" viewBox="0 0 ${PAINT_W} ${PAINT_H}" aria-label="${done ? `You painted ${l.name_latin}!` : `Paint the letter ${l.name_latin} with your finger`}">
          <defs><clipPath id="letterClip">${g.svgText('')}</clipPath></defs>
          ${g.svgText('fill="#FFE7F0" stroke="#F2B5CC" stroke-width="4" stroke-dasharray="3 10" stroke-linecap="round"')}
          <g clip-path="url(#letterClip)" class="ink">${ink}</g>
          ${HAND_SVG}
        </svg>
        ${done ? burst(1) + `<img class="cheer-leo" src="${img('lion')}" alt="">` : ''}
      </div>
      ${done ? `<div class="paint-done-row">
          <button type="button" class="round-btn paint-again" aria-label="Paint it again">${ICON.again}</button>
          ${ringNext('Next letter. It starts by itself in a moment')}</div>`
        : `<div class="crayons" role="radiogroup" aria-label="Crayon colour">${CRAYONS.map(([n, c, d], k) =>
          `<button type="button" role="radio" class="crayon ${k === paint.crayon ? 'on' : ''}" aria-checked="${k === paint.crayon}" aria-label="${n}" data-k="${k}" style="background:${c};box-shadow:0 5px 0 ${d}"></button>`).join('')}</div>
        <button type="button" class="wipe" aria-label="Wipe and paint again">${ICON.again}</button>`}
    </div>`;
  },
  mount: (root) => {
    bindGo(root);
    const l = paint.letter;
    const prompt = () => say(clipAr('لوّن ال' + l.name_ar));
    root.querySelector('.paint-say').addEventListener('click', prompt);
    if (paint.done) {
      const again = () => { paint.strokes = []; paint.dist = 0; paint.done = false; render(); };
      root.querySelector('.paint-again').addEventListener('click', again);
      const next = () => { newPaint(); render(); };
      root.querySelector('.ring-next').addEventListener('click', next);
      later(next, 5000);
      return;
    }
    if (!paint.strokes.length) prompt();
    const svg = root.querySelector('.paint-svg');
    const g = fitGlyph(l.letter, PAINT_W, PAINT_H, 0.78);
    const strokes = STROKES[l.index] || [];
    let stopDemo = null;
    if (!paint.demoShown) {
      paint.demoShown = true;
      later(() => { stopDemo = demoHand(svg.querySelector('.demo-hand'), strokes.map((s) => s.points.map(g.at))); }, 900);
    }
    root.querySelectorAll('.crayon').forEach((b) => b.addEventListener('click', () => { paint.crayon = +b.dataset.k; render(); }));
    root.querySelector('.wipe').addEventListener('click', () => { paint.strokes = []; paint.dist = 0; render(); });
    const inkG = svg.querySelector('.ink');
    let cur = null, last = null;
    svg.addEventListener('pointerdown', (e) => {
      stopDemo && stopDemo();
      try { svg.setPointerCapture(e.pointerId); } catch (err) { /* pointer already gone */ }
      const p = svgPoint(svg, e);
      cur = { color: CRAYONS[paint.crayon][1], pts: [p] };
      paint.strokes.push(cur);
      cur.el = document.createElementNS('http://www.w3.org/2000/svg', 'path');
      Object.entries({ stroke: cur.color, 'stroke-width': 30, fill: 'none', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }).forEach(([k, v]) => cur.el.setAttribute(k, v));
      inkG.appendChild(cur.el);
      cur.el.setAttribute('d', svgPath([p, p]));
      last = p;
    });
    svg.addEventListener('pointermove', (e) => {
      if (!cur) return;
      const p = svgPoint(svg, e);
      if (g.near(p[0], p[1]) && g.near(last[0], last[1])) paint.dist += Math.hypot(p[0] - last[0], p[1] - last[1]);
      cur.pts.push(p); last = p;
      cur.el.setAttribute('d', svgPath(cur.pts));
    });
    const up = () => {
      if (!cur) return;
      cur = null;
      if (paint.dist >= PAINT_GOAL && !paint.done) {
        paint.done = true;
        say('praise: ' + pick(['praise_1', 'praise_2', 'praise_3']));
        buzz('confirm');
        render();
      }
    };
    svg.addEventListener('pointerup', up);
    svg.addEventListener('pointercancel', up);
  }
};
