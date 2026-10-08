// ================= Grown-up screens =================
const HOLD_MS = 2000;
let gateOpen = false;
SCREENS.gate = {
  title: 'Parent gate',
  view: () => `<div class="gate-wrap">
    <button type="button" class="back-to-play" data-go="${homeRoute()}"><span class="btp-ico">${ICON.home}</span><b>Back to play</b><img src="${img('lion')}" alt=""></button>
    <div class="gate-body">
      <span class="gate-chip">GROWN-UPS ONLY</span>
      <h1>${gateOpen ? 'Unlocked' : 'Hold to open settings'}</h1>
      <p>${gateOpen ? 'Choose where to go.' : 'Keep your finger on the circle for 2 seconds.'}</p>
      ${gateOpen
        ? `<div class="gate-actions"><button type="button" class="primary-btn sun" data-go="zone">Open Parent zone ${ICON.next}</button>
           <button type="button" class="ghost-btn close-app">${ICON.exit} Close Huroofi</button></div>`
        : `<button type="button" class="hold" aria-label="Press and hold for 2 seconds">
            <svg viewBox="0 0 220 220" aria-hidden="true"><circle cx="110" cy="110" r="100" class="hold-track"/><circle cx="110" cy="110" r="100" class="hold-ring" pathLength="100"/></svg>
            <span class="hold-pad">${ICON.hand}<b class="hold-label">Hold</b></span></button>`}
    </div>
    <p class="gate-foot">${gateOpen ? '' : 'Letting go starts the circle again.'}</p></div>`,
  mount: (root) => {
    bindGo(root);
    root.querySelectorAll('[data-go]').forEach((b) => b.addEventListener('click', () => { gateOpen = false; }, { capture: true }));
    if (gateOpen) {
      root.querySelector('.close-app').addEventListener('click', () => { gateOpen = false; go('closed'); });
      return;
    }
    const pad = root.querySelector('.hold'), ring = root.querySelector('.hold-ring'), label = root.querySelector('.hold-label');
    let t0 = null, raf;
    const set = (f) => { ring.style.strokeDashoffset = String(100 - f * 100); };
    const tick = (ts) => {
      if (t0 === null) t0 = ts;
      const f = Math.min((ts - t0) / HOLD_MS, 1);
      set(f);
      if (f >= 1) { gateOpen = true; buzz('confirm'); render(); return; }
      raf = requestAnimationFrame(tick);
    };
    const start = (e) => { e.preventDefault(); t0 = null; label.textContent = 'Keep holding…'; raf = requestAnimationFrame(tick); };
    const reset = () => { cancelAnimationFrame(raf); set(0); label.textContent = 'Hold'; };
    pad.addEventListener('pointerdown', start);
    ['pointerup', 'pointerleave', 'pointercancel'].forEach((ev) => pad.addEventListener(ev, reset));
    pad.addEventListener('keydown', (e) => { if ((e.key === ' ' || e.key === 'Enter') && !e.repeat) start(e); });
    pad.addEventListener('keyup', reset);
  }
};

// ---------- Parent zone ----------
let zoneMode = null;
let confirmStartOver = false;
SCREENS.zone = {
  title: 'Parent zone',
  view: () => {
    if (!zoneMode) zoneMode = S.mode;
    const learned = S.learned.size, ll = learningLetter();
    const learning = ll ? 1 : 0, togo = 28 - learned - learning;
    const n = openStage() || 7;
    const sw = (id, on, disabled = false) => `<button type="button" role="switch" class="switch ${on ? 'on' : ''}" aria-checked="${on}" data-sw="${id}" ${disabled ? 'disabled' : ''}><span></span></button>`;
    const row = (title, sub, ctl, extra = '') => `<div class="z-row"><span class="z-text"><b>${title}</b><span>${sub}</span>${extra}</span>${ctl}</div>`;
    const modes = [['toddler', 'Toddler', '18m – 3y', 'Picture cards, find-it game, finger paint'], ['preschool', 'Preschool', '3 – 5y', 'Meet, trace and a 3-picture game per letter'], ['reader', 'Early reader', '5y+', 'Adds letter shapes and a 4-picture game']];
    const grid = LETTERS.map((l) => {
      const cls = isLearned(l.index) ? 'learned' : l === ll ? 'learning' : 'togo';
      return `<span class="z-letter ${cls}" aria-label="${l.name_latin}, ${cls === 'togo' ? 'to go' : cls}">${AR(l.letter)}</span>`;
    }).join('');
    return `<div class="zone-wrap">
      <header class="zone-head"><button type="button" class="round-btn zone-back" aria-label="Back to kid mode">${ICON.back}</button><h1>Parent zone</h1>
        <button type="button" class="btn-small zone-back">${ICON.home} Back to kid mode</button></header>
      <section class="z-card child-card">
        <div class="child-row"><span class="avatar small"><img src="${img('lion')}" alt=""></span><span class="z-text"><b>Your child</b><span>Stage ${n} of 7 · ${stageOf(n).name}</span></span></div>
        <div class="z-bar"><span style="width:${(learned / 28) * 100}%;background:#158048"></span><span style="width:${(learning / 28) * 100}%;background:#1F6FE0"></span></div>
        <div class="legend"><span><i class="lg learned"></i>${learned} learned</span><span><i class="lg learning"></i>${learning} learning</span><span><i class="lg togo"></i>${togo} to go</span></div>
        ${zoneMode === 'toddler' ? '<p class="z-note">Toddler play isn\'t tracked. Letters count once lessons start (Preschool mode).</p>' : ''}
        <div class="z-grid" dir="rtl">${grid}</div>
      </section>
      <section class="z-card"><h2>Learning mode</h2><span class="z-sub">Changes what your child sees when you go back to kid mode</span>
        ${modes.map(([id, t, age, what]) => `<button type="button" class="mode ${zoneMode === id ? 'on' : ''}" aria-pressed="${zoneMode === id}" data-mode="${id}"><span class="radio"><span></span></span><span class="z-text"><b>${t} <em>· ${age}</em></b><span>${what}</span></span></button>`).join('')}
      </section>
      <section class="z-card list">
        ${row('Voice & sounds', 'Native-speaker audio for every letter', sw('voice', S.voice))}
        ${row('Vibration', 'A gentle buzz when your child gets it right', sw('vib', S.vib))}
        ${row('Show vowel marks', `Adds vowel marks like ${AR('بَ بِ بُ')} in lessons`, sw('marks', false, true), '<small class="soon">Coming soon: vowel marks arrive in a later update.</small>')}
        ${row('Daily play time', 'A calm “time to rest” screen appears when it is used up', `<span class="stepper"><button type="button" class="step-btn" data-step="-5" aria-label="5 minutes less" ${S.minutes <= 5 ? 'disabled' : ''}>${ICON.minus}</button><b>${S.minutes} min</b><button type="button" class="step-btn" data-step="5" aria-label="5 minutes more" ${S.minutes >= 60 ? 'disabled' : ''}>${ICON.plus}</button></span>`)}
        ${row('Offline pack', 'All content is on this device.', '', '<small>To keep your child in Huroofi, turn on App pinning in your phone\'s Settings.</small>')}
      </section>
      <section class="z-card list"><h2>Lessons</h2>
        ${row('Unlock all', 'Start any stage from the Letter map', sw('unlockAll', S.unlockAll))}
        ${row('Trace only', 'Letters open straight into tracing, as practice', sw('traceOnly', S.traceOnly))}
      </section>
      <section class="z-card list">
        ${confirmStartOver
          ? `<div class="confirm"><b>Start over?</b><span>This clears every learned letter and sticker. It can't be undone.</span><div class="confirm-btns"><button type="button" class="btn-small ghost" data-confirm="no">Cancel</button><button type="button" class="btn-small warn" data-confirm="yes">Start over</button></div></div>`
          : row('Start over', 'Clears every learned letter and sticker', '<button type="button" class="btn-small ghost start-over">Start over</button>')}
      </section>
      <p class="trust">${ICON.shield} No ads · no tracking · works offline</p>
    </div>`;
  },
  mount: (root) => {
    root.querySelectorAll('.zone-back').forEach((b) => b.addEventListener('click', () => { S.mode = zoneMode; zoneMode = null; confirmStartOver = false; leaveZone(); }));
    root.querySelectorAll('[data-mode]').forEach((b) => b.addEventListener('click', () => { zoneMode = b.dataset.mode; render(); }));
    root.querySelectorAll('[data-sw]').forEach((b) => b.addEventListener('click', () => {
      const k = b.dataset.sw;
      if (k === 'marks') return;
      S[k] = !S[k];
      render();
      document.dispatchEvent(new Event('settings'));
    }));
    root.querySelectorAll('[data-step]').forEach((b) => b.addEventListener('click', () => {
      S.minutes = Math.min(60, Math.max(5, S.minutes + +b.dataset.step));
      if (S.timeUp && +b.dataset.step > 0) S.timeUp = false;
      render();
      document.dispatchEvent(new Event('settings'));
    }));
    const so = root.querySelector('.start-over');
    if (so) so.addEventListener('click', () => { confirmStartOver = true; render(); });
    root.querySelectorAll('[data-confirm]').forEach((b) => b.addEventListener('click', () => {
      if (b.dataset.confirm === 'yes') { S.learned.clear(); S.stickers.clear(); }
      confirmStartOver = false;
      render();
      document.dispatchEvent(new Event('settings'));
    }));
  }
};

// ---------- Rest ----------
SCREENS.rest = {
  title: 'Rest',
  view: () => `<div class="rest-wrap">
    <button type="button" class="parent-lock corner" data-go="gate" aria-label="Grown-ups: open the parent gate">${ICON.lock}</button>
    <span class="moon">${ICON.moon}</span>
    <span class="rest-stars" aria-hidden="true">${starSvg(22, '#FFE07A')}${starSvg(16, '#FFE07A')}${starSvg(26, '#FFE07A')}</span>
    <img class="rest-leo" src="${img('lion')}" alt="Leo the lion, resting">
    ${AR('وقت الراحة', 'rest-ar')}
    <p>Time to rest. See you tomorrow!</p></div>`,
  mount: (root) => { bindGo(root); say(clipAr('وقت الراحة')); }
};

// ---------- App closed (prototype only) ----------
SCREENS.closed = {
  title: 'App closed',
  view: () => `<div class="closed-wrap"><p>Huroofi is closed.</p><p class="muted">On a phone this returns to the home screen.</p>
    <button type="button" class="primary-btn" data-go="${homeRoute()}">Open Huroofi again</button></div>`,
  mount: (root) => bindGo(root)
};
