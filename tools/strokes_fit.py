"""Check and snap data/strokes.json onto the letters as Noto Naskh Arabic Bold draws them (plan 09).

Points are fractions of the glyph's ink box, as in the app (GlyphFit.toCanvas). Line strokes are
snapped onto the glyph's centre line (its skeleton); dot strokes onto the centre of the nearest dot.

    python tools/strokes_fit.py           # report how far each letter's strokes sit off the centre line
    python tools/strokes_fit.py --write   # snap them and rewrite data/strokes.json and the app copy

Needs Pillow and numpy on the developer machine (not app dependencies).
"""
import json
import sys
from collections import deque
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent.parent
FONT = ROOT / "assets/fonts/NotoNaskhArabic-Variable.ttf"
DATA = ROOT / "data/strokes.json"
APP_COPY = ROOT / "app/src/main/assets/strokes.json"
LETTERS = ROOT / "data/letters.json"
SIZE = 500  # font px; the ink box comes out a few hundred px across
STEP = 0.01  # densify line strokes every 1 % of the ink box
KEEP = 0.06  # keep a point about every 6 % of the ink box along a snapped stroke
OFF_LIMIT = 0.06  # report: a stroke is "off" when its mean distance to the centre line exceeds this share


def glyph_mask(letter):
    font = ImageFont.truetype(str(FONT), SIZE)
    font.set_variation_by_axes([700])
    img = Image.new("L", (SIZE * 3, SIZE * 3), 0)
    ImageDraw.Draw(img).text((SIZE, SIZE), letter, font=font, fill=255)
    a = np.array(img) > 127
    ys, xs = np.nonzero(a)
    return a[ys.min():ys.max() + 1, xs.min():xs.max() + 1]


def thin(mask):
    """Zhang-Suen thinning, numpy only."""
    img = np.pad(mask.astype(np.uint8), 1)
    changed = True
    while changed:
        changed = False
        for step in (0, 1):
            p = [np.roll(np.roll(img, -dy, 0), -dx, 1) for dy, dx in
                 ((-1, 0), (-1, 1), (0, 1), (1, 1), (1, 0), (1, -1), (0, -1), (-1, -1))]
            b = sum(p)
            seq = p + [p[0]]
            a = sum(((seq[i] == 0) & (seq[i + 1] == 1)).astype(np.uint8) for i in range(8))
            if step == 0:
                c = p[0] * p[2] * p[4]
                d = p[2] * p[4] * p[6]
            else:
                c = p[0] * p[2] * p[6]
                d = p[0] * p[4] * p[6]
            kill = (img == 1) & (b >= 2) & (b <= 6) & (a == 1) & (c == 0) & (d == 0)
            if kill.any():
                img[kill] = 0
                changed = True
    return img[1:-1, 1:-1].astype(bool)


def components(mask):
    labels = np.zeros(mask.shape, int)
    comps = []
    for y, x in zip(*np.nonzero(mask)):
        if labels[y, x]:
            continue
        n = len(comps) + 1
        labels[y, x] = n
        q, pix = deque([(y, x)]), []
        while q:
            cy, cx = q.popleft()
            pix.append((cy, cx))
            for dy in (-1, 0, 1):
                for dx in (-1, 0, 1):
                    ny, nx = cy + dy, cx + dx
                    if 0 <= ny < mask.shape[0] and 0 <= nx < mask.shape[1] and mask[ny, nx] and not labels[ny, nx]:
                        labels[ny, nx] = n
                        q.append((ny, nx))
        comps.append(np.array(pix, float))
    return comps


def densify(points, step):
    out = [points[0]]
    for a, b in zip(points, points[1:]):
        n = max(1, int(np.ceil(np.linalg.norm(b - a) / step)))
        out += [a + (b - a) * k / n for k in range(1, n + 1)]
    return np.array(out)


def nearest(cloud, pts):
    d = ((pts[:, None, :] - cloud[None, :, :]) ** 2).sum(-1)
    i = d.argmin(1)
    return cloud[i], np.sqrt(d[np.arange(len(pts)), i])


def resample(path, every):
    lengths = np.linalg.norm(np.diff(path, axis=0), axis=1)
    total = lengths.sum()
    n = max(2, int(round(total / every)) + 1)
    at = np.concatenate([[0], np.cumsum(lengths)])
    return np.array([[np.interp(t, at, path[:, 0]), np.interp(t, at, path[:, 1])] for t in np.linspace(0, total, n)])


def fit_letter(letter, strokes):
    mask = glyph_mask(letter)
    h, w = mask.shape
    scale = np.array([w - 1, h - 1], float)
    skel = np.argwhere(thin(mask))[:, ::-1].astype(float) / scale  # x, y in 0..1
    comps = components(mask)
    biggest = max(len(c) for c in comps)
    dots = [c[:, ::-1].mean(0) / scale for c in comps if len(c) < 0.2 * biggest]
    out, report = [], []
    for s in strokes:
        pts = np.array(s["points"], float)
        if s["dot"]:
            if dots:
                snapped, dist = nearest(np.array(dots), pts)
                dots = [d for d in dots if not np.allclose(d, snapped[0])]  # each dot used once
            else:
                snapped, dist = pts, np.zeros(1)
            new = snapped
        else:
            dense = densify(pts, STEP)
            snapped, dist = nearest(skel, dense)
            # A light smoothing pass stops jumps between neighbouring skeleton pixels.
            smooth = snapped.copy()
            smooth[1:-1] = (snapped[:-2] + snapped[1:-1] + snapped[2:]) / 3
            new = resample(smooth, KEEP)
        report.append((s["order"], s["dot"], float(dist.mean()), float(dist.max())))
        out.append({**s, "points": [[round(float(x), 3), round(float(y), 3)] for x, y in np.clip(new, 0, 1)]})
    return out, report


def dump(doc):
    lines = ["{", '  "letters": [']
    for i, l in enumerate(doc["letters"]):
        lines.append(f'    {{ "index": {l["index"]}, "strokes": [')
        for j, s in enumerate(l["strokes"]):
            pts = ", ".join(f"[{p[0]}, {p[1]}]" for p in s["points"])
            comma = "," if j < len(l["strokes"]) - 1 else ""
            lines.append(f'      {{ "order": {s["order"]}, "dot": {str(s["dot"]).lower()}, "points": [{pts}] }}{comma}')
        lines.append("    ] }" + ("," if i < len(doc["letters"]) - 1 else ""))
    lines += ["  ]", "}", ""]
    return "\n".join(lines)


def main():
    write = "--write" in sys.argv
    doc = json.loads(DATA.read_text(encoding="utf-8"))
    glyphs = {l["index"]: l["letter"] for l in json.loads(LETTERS.read_text(encoding="utf-8"))["letters"]}
    worst = 0.0
    for l in doc["letters"]:
        new, report = fit_letter(glyphs[l["index"]], l["strokes"])
        line = " ".join(f'{o}{"d" if d else ""}:{m:.3f}/{x:.3f}' for o, d, m, x in report)
        off = [o for o, d, m, x in report if not d and m > OFF_LIMIT]
        worst = max([worst] + [m for o, d, m, x in report])
        print(f'{l["index"]:2} {glyphs[l["index"]]}  {line}{"  OFF " + str(off) if off else ""}')
        if write:
            l["strokes"] = new
    print(f"worst mean distance to centre line: {worst:.3f} of the ink box")
    if write:
        text = dump(doc)
        DATA.write_text(text, encoding="utf-8", newline="\n")
        APP_COPY.write_text(text, encoding="utf-8", newline="\n")
        print("wrote", DATA.relative_to(ROOT), "and", APP_COPY.relative_to(ROOT))


if __name__ == "__main__":
    main()
