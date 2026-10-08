// ---------- letter glyph fitting (same idea as GlyphFit.kt: centre the ink box, fill a share of the canvas) ----------
let FONT_AR = 'Noto Naskh Arabic';
async function chooseArabicFont() {
  for (const fam of ['HNaskh', 'Noto Naskh Arabic']) {
    try {
      const faces = await document.fonts.load(`700 100px "${fam}"`, 'ب');
      if (faces.length) { FONT_AR = fam; return; }
    } catch (e) { /* try the next family */ }
  }
}
function fitGlyph(ch, w, h, share = 0.8) {
  const c = document.createElement('canvas');
  c.width = Math.ceil(w); c.height = Math.ceil(h);
  const ctx = c.getContext('2d');
  ctx.direction = 'ltr'; ctx.textAlign = 'left'; ctx.textBaseline = 'alphabetic';
  ctx.font = `700 200px "${FONT_AR}"`;
  const probe = ctx.measureText(ch);
  const pw = probe.actualBoundingBoxLeft + probe.actualBoundingBoxRight;
  const ph = probe.actualBoundingBoxAscent + probe.actualBoundingBoxDescent;
  const px = 200 * Math.min((w * share) / pw, (h * share) / ph);
  ctx.font = `700 ${px}px "${FONT_AR}"`;
  const m = ctx.measureText(ch);
  const inkW = m.actualBoundingBoxLeft + m.actualBoundingBoxRight;
  const inkH = m.actualBoundingBoxAscent + m.actualBoundingBoxDescent;
  const left = (w - inkW) / 2, top = (h - inkH) / 2;
  const x = left + m.actualBoundingBoxLeft, y = top + m.actualBoundingBoxAscent;
  ctx.fillStyle = '#000';
  ctx.fillText(ch, x, y);
  const alpha = ctx.getImageData(0, 0, c.width, c.height).data;
  const inside = (px2, py2) => {
    const ix = Math.round(px2), iy = Math.round(py2);
    if (ix < 0 || iy < 0 || ix >= c.width || iy >= c.height) return false;
    return alpha[(iy * c.width + ix) * 4 + 3] > 40;
  };
  return {
    px, x, y, left, top, inkW, inkH,
    at: ([nx, ny]) => [left + nx * inkW, top + ny * inkH],
    // On the letter, with a little slop so a finger on the edge still counts.
    near: (px2, py2, slop = 8) => inside(px2, py2) || [[slop, 0], [-slop, 0], [0, slop], [0, -slop]].some(([dx, dy]) => inside(px2 + dx, py2 + dy)),
    svgText: (attrs) => `<text x="${x.toFixed(1)}" y="${y.toFixed(1)}" font-family="${FONT_AR}" font-weight="700" font-size="${px.toFixed(1)}" ${attrs}>${ch}</text>`
  };
}
// Points every `step` px along a polyline.
function resample(pts, step) {
  if (pts.length === 1) return [pts[0]];
  const out = [pts[0]];
  let carry = 0;
  for (let k = 1; k < pts.length; k++) {
    const [ax, ay] = pts[k - 1], [bx, by] = pts[k];
    const seg = Math.hypot(bx - ax, by - ay);
    let d = step - carry;
    while (d <= seg) { out.push([ax + ((bx - ax) * d) / seg, ay + ((by - ay) * d) / seg]); d += step; }
    carry = seg - (d - step);
  }
  const last = pts[pts.length - 1], tail = out[out.length - 1];
  if (Math.hypot(last[0] - tail[0], last[1] - tail[1]) > step / 2) out.push(last);
  return out;
}
const svgPath = (pts) => pts.map((p, k) => `${k ? 'L' : 'M'}${p[0].toFixed(1)} ${p[1].toFixed(1)}`).join(' ');
// Pointer position in the SVG's own units.
function svgPoint(svg, ev) {
  // Screen CTM covers the letterboxing from preserveAspectRatio and the shell's device scale.
  const p = new DOMPoint(ev.clientX, ev.clientY).matrixTransform(svg.getScreenCTM().inverse());
  return [p.x, p.y];
}
// Moves a hand along each path in turn; returns a stop function.
function demoHand(hand, paths, onEnd) {
  let stopped = false, raf;
  const speed = 0.18; // px per ms
  const seq = paths.map((p) => (p.length === 1 ? { pts: p, len: 0 } : { pts: p, len: p.slice(1).reduce((a, q, k) => a + Math.hypot(q[0] - p[k][0], q[1] - p[k][1]), 0) }));
  let si = 0, t0 = null;
  const pos = (s, d) => {
    let acc = 0;
    for (let k = 1; k < s.pts.length; k++) {
      const a = s.pts[k - 1], b = s.pts[k], seg = Math.hypot(b[0] - a[0], b[1] - a[1]);
      if (acc + seg >= d) { const f = (d - acc) / seg; return [a[0] + (b[0] - a[0]) * f, a[1] + (b[1] - a[1]) * f]; }
      acc += seg;
    }
    return s.pts[s.pts.length - 1];
  };
  hand.style.display = 'block';
  const frame = (ts) => {
    if (stopped) return;
    if (t0 === null) t0 = ts;
    const s = seq[si];
    const dur = Math.max(s.len / speed, 500);
    const d = Math.min((ts - t0) / dur, 1);
    const [x, y] = s.len ? pos(s, d * s.len) : s.pts[0];
    hand.setAttribute('transform', `translate(${x.toFixed(1)} ${y.toFixed(1)})`);
    if (d >= 1) { si++; t0 = null; if (si >= seq.length) { hand.style.display = 'none'; onEnd && onEnd(); return; } }
    raf = requestAnimationFrame(frame);
  };
  raf = requestAnimationFrame(frame);
  return () => { stopped = true; cancelAnimationFrame(raf); hand.style.display = 'none'; };
}
const HAND_SVG = `<g class="demo-hand" style="display:none"><circle r="16" fill="#FFC93C" opacity="0.55"/><g transform="translate(-6 -2) scale(1.9)" color="#13294B"><rect x="1" y="2" width="20" height="20" rx="10" fill="#FFFFFF" opacity="0.9"/>${ICON.hand.replace('<svg ', '<svg width="22" height="22" ')}</g></g>`;
