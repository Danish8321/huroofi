// Scripted click-through of the prototype. Appended to a scratch copy; results go to #testlog.
(function () {
  const log = [], errs = [];
  window.addEventListener('error', (e) => errs.push(e.message + ' @' + e.lineno));
  const wait = (ms) => new Promise((r) => setTimeout(r, ms));
  const $ = (s) => document.querySelector('#screen ' + s);
  const ok = (c, m) => log.push((c ? 'PASS ' : 'FAIL ') + m);
  const click = (s) => { const el = typeof s === 'string' ? $(s) : s; if (!el) { log.push('MISSING ' + s); return; } el.click(); };
  function drawStrokes(sel, strokes) {
    strokes.forEach((pts) => {
      const svg = $(sel), m = svg.getScreenCTM();
      const scr = resample(pts.length === 1 ? [pts[0], [pts[0][0] + 1, pts[0][1]]] : pts, 4).map(([x, y]) => new DOMPoint(x, y).matrixTransform(m));
      const ev = (t, p) => svg.dispatchEvent(new PointerEvent(t, { bubbles: true, clientX: p.x, clientY: p.y, pointerId: 1, isPrimary: true }));
      ev('pointerdown', scr[0]); scr.forEach((p) => ev('pointermove', p)); ev('pointerup', scr[scr.length - 1]);
    });
  }
  async function solveQuiz(i) {
    for (let r = 1; r <= ROUNDS; r++) {
      if (r === 1) {
        const wrong = [...document.querySelectorAll('#screen .q-card')].find((c) => +c.dataset.i !== i);
        click(wrong); ok($('.almost'), 'quiz wrong answer shows Almost'); ok(!$('.q-next'), 'no Next after wrong');
      }
      click(`.q-card[data-i="${i}"]`); ok($('.q-next'), `quiz round ${r} shows Next`);
      await wait(3000); ok(route.name === 'quiz' && quiz && quiz.round === r, `quiz round ${r} waits for tap`);
      click('.q-next');
    }
  }
  async function run() {
    await wait(1500);
    ok(route.name === 'home', 'starts on home');
    click('.go-btn'); ok(route.name === 'lesson' && route.p.i === 2, 'Let\'s learn opens lesson ب');
    click('.lesson-next'); ok(route.name === 'trace', 'Trace it opens trace');
    ok($('.primary-btn.locked'), 'trace Next locked');
    await wait(500);
    const g = fitGlyph(L(2).letter, TRACE_W, TRACE_H, 0.8);
    const body = STROKES[2][0].points.map(g.at);
    drawStrokes('.trace-svg', [resample(body, 4).slice(0, -12)]);
    ok(!trace.finished[0] && $('.primary-btn.locked'), 'stroke with missed dots stays unfinished');
    // Follow every stroke a little off-centre: all dots go green, but part of the letter stays unpainted.
    drawStrokes('.trace-svg', STROKES[2].map((s) => s.points.map(g.at).map(([x, y]) => [x, y + 10])));
    const gaps = trace.area.filter((v) => !v).length;
    ok(trace.finished.every(Boolean) && gaps > 0 && !trace.done && $('.primary-btn.locked') && $('.tgap'), `dots alone do not finish the letter (${gaps} of ${trace.area.length} spots unpainted, gaps shown)`);
    drawStrokes(".trace-svg", trace.areaPts.filter((p, k) => !trace.area[k]).map((p) => [p]));
    ok(trace.done && $('.trace-next'), 'painting the whole letter unlocks Play');
    await wait(4000); ok(route.name === 'trace', 'trace does not auto-advance');
    click('.trace-next'); ok(route.name === 'quiz', 'Play opens quiz');
    await solveQuiz(2);
    ok(S.learned.has(2) && route.name === 'lesson' && route.p.i === 3, 'quiz learns ب and opens ت');
    // sticker path
    applyPreset('stageEnd'); quiz = null; go('quiz', { i: 4 });
    await solveQuiz(4);
    ok(route.name === 'reward' && S.stickers.has(1), 'last letter of stage earns sticker');
    click('.reward-foot .primary-btn'); ok(['lesson', 'map'].includes(route.name), 'reward primary goes on (' + route.name + ')');
    // toddler find
    S.mode = 'toddler'; go('tfind');
    const t1 = find.target;
    click(`.find-card[data-i="${find.options.find((o) => o !== t1).index}"]`); ok(!find.solved, 'find wrong tap no fail');
    click(`.find-card[data-i="${t1.index}"]`); ok(find.solved && $('.fill-next'), 'find right shows fill Next');
    await wait(5600); ok(!find.solved, 'find auto-advances after countdown');
    ok(TODDLER_POOL.every((l) => l.toddler_word), 'find pool toddler words only');
    // toddler paint
    paint = null; go('tpaint');
    await wait(300);
    const pg = fitGlyph(paint.letter.letter, PAINT_W, PAINT_H, 0.78);
    drawStrokes('.paint-svg', STROKES[paint.letter.index].map((s) => s.points.map(pg.at)).concat(STROKES[paint.letter.index].map((s) => s.points.map(pg.at))));
    ok(paint.done, 'painting the letter completes (dist ' + Math.round(paint.dist) + ')');
    // gate
    go('gate');
    $('.hold').dispatchEvent(new PointerEvent('pointerdown', { bubbles: true }));
    await wait(2300); ok(gateOpen, 'gate opens after 2 s hold');
    click('.primary-btn.sun'); ok(route.name === 'zone', 'gate opens zone');
    click('[data-mode="preschool"]'); click('.btn-small.zone-back'); ok(route.name === 'home' && S.mode === 'preschool', 'mode applies on leaving zone');
    go('gate'); $('.hold').dispatchEvent(new PointerEvent('pointerdown', { bubbles: true })); await wait(800);
    $('.hold').dispatchEvent(new PointerEvent('pointerup', { bubbles: true })); await wait(1800); ok(!gateOpen, 'early release keeps gate shut');
    // rest
    S.timeUp = true; go('home'); ok(document.querySelector('#screen').classList.contains('screen-rest'), 'time up shows rest');
    S.timeUp = false; go('home');
    for (const r of ['map', 'stickers', 'practice', 'thome', 'tcards']) { go(r, routeParams(r)); }
    go('practice', { i: 5 }); ok($('.practice-title'), 'practice route renders');
    for (const i of [5, 8, 26]) {
      trace = null; go('practice', { i });
      const gi = fitGlyph(L(i).letter, TRACE_W, TRACE_H, 0.8);
      drawStrokes('.trace-svg', STROKES[i].map((st) => st.points.map(gi.at)));
      ok(trace.done, 'tracing down the middle of ' + L(i).letter + ' paints the whole letter');
    }
    systemBack(); ok(route.name === 'home', 'back from practice to home');
    ok(errs.length === 0, 'no JS errors ' + errs.join(' | '));
    const pre = document.createElement('pre'); pre.id = 'testlog'; pre.textContent = log.join('\n'); document.body.appendChild(pre);
  }
  document.addEventListener('DOMContentLoaded', () => {});
  setTimeout(run, 0);
})();
