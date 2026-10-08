// Builds design-reference/prototype.html from these sources and data/*.json.
// Usage: node build.js [artifactDir]  — artifactDir also gets a copy with images under img/ (for claude.ai).
const fs = require('fs'), path = require('path');
const here = __dirname, repo = path.resolve(here, '../..');
const read = (f) => fs.readFileSync(path.join(here, f), 'utf8');
const json = (f) => JSON.parse(fs.readFileSync(path.join(repo, f), 'utf8'));
const letters = json('data/letters.json');
const data = JSON.stringify({ letters: letters.letters, stages: letters.stages, strokes: json('data/strokes.json') });
const js = ['core.js', 'glyph.js', 'toddler.js', 'learn.js', 'parent.js', 'shell.js'].map(read).join('\n');
const face = (fam, file) => `@font-face { font-family: '${fam}'; src: url('${file}') format('truetype'); font-weight: 100 900; font-display: block }`;
function build(out, { doctype, img, fontBase }) {
  const fonts = fontBase ? [face('HNaskh', fontBase + 'NotoNaskhArabic-Variable.ttf'), face('HBaloo', fontBase + 'BalooBhaijaan2-Variable.ttf')].join('\n') : '';
  const fill = { DOCTYPE: doctype ? '<!doctype html>\n' : '', FONTFACE: fonts, CSS: read('style.css'), DATA: data.replace(/<\//g, '<\/'), IMG: JSON.stringify(img), JS: js };
  const html = read('template.html').replace(/%%(\w+)%%/g, (m, k) => fill[k]);
  fs.writeFileSync(out, html);
  console.log('wrote', path.relative(repo, out) || out);
}
build(path.join(repo, 'design-reference/prototype.html'), { doctype: true, img: '../assets/illustrations/png/', fontBase: '../assets/fonts/' });
const art = process.argv[2];
if (art) {
  const src = path.join(repo, 'assets/illustrations/png');
  fs.mkdirSync(path.join(art, 'img'), { recursive: true });
  build(path.join(art, 'huroofi-prototype.html'), { doctype: false, img: 'img/', fontBase: null });
  for (const f of fs.readdirSync(src)) fs.copyFileSync(path.join(src, f), path.join(art, 'img', f));
}
