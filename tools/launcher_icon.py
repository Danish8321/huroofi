"""Draw the launcher icon's foreground and monochrome layers into app/src/main/res/mipmap-*dpi.

Navy ح with a sun-shadow and two white sparkles, in the 108-unit adaptive-icon space; everything
stays inside the 33-unit safe circle. The sun-yellow background is the vector drawable
app/src/main/res/drawable/ic_launcher_background.xml. The glyph is Noto Naskh Arabic Bold, drawn
by Microsoft Edge headless so it shapes exactly as a browser does.

    python tools/launcher_icon.py

Needs Pillow and Microsoft Edge on the developer machine (not app dependencies). Set EDGE to the
browser's path if it is not in the usual place.
"""
import base64
import io
import json
import os
import re
import subprocess
import sys
import tempfile
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
FONT = ROOT / "assets/fonts/NotoNaskhArabic-Variable.ttf"
RES = ROOT / "app/src/main/res"
EDGE = os.environ.get("EDGE", r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe")
DENSITIES = {"mdpi": 108, "hdpi": 162, "xhdpi": 216, "xxhdpi": 324, "xxxhdpi": 432}

DRAW = r"""
const NAVY = "#13294B", SHADOW = "#E0A400", WHITE = "#FFFFFF";

function glyph(ctx, cx, cy, size, fill) {
  // The glyph's ink box, `size` units tall, centred on cx, cy.
  ctx.save();
  ctx.direction = "rtl"; ctx.textAlign = "left"; ctx.textBaseline = "alphabetic";
  ctx.font = "700 100px Naskh";
  const m = ctx.measureText("ح");
  const h = m.actualBoundingBoxAscent + m.actualBoundingBoxDescent;
  const w = m.actualBoundingBoxLeft + m.actualBoundingBoxRight;
  const k = size / h;
  ctx.translate(cx - (w * k) / 2, cy - (h * k) / 2);
  ctx.scale(k, k);
  ctx.fillStyle = fill;
  ctx.fillText("ح", m.actualBoundingBoxLeft, m.actualBoundingBoxAscent);
  ctx.restore();
}

function sparkle(ctx, cx, cy, r) {
  ctx.fillStyle = WHITE; ctx.beginPath();
  ctx.moveTo(cx, cy - r);
  ctx.quadraticCurveTo(cx, cy, cx + r, cy); ctx.quadraticCurveTo(cx, cy, cx, cy + r);
  ctx.quadraticCurveTo(cx, cy, cx - r, cy); ctx.quadraticCurveTo(cx, cy, cx, cy - r);
  ctx.fill();
}

function layer(px, mono) {
  const c = document.createElement("canvas");
  c.width = c.height = px;
  const ctx = c.getContext("2d");
  ctx.scale(px / 108, px / 108);
  if (mono) {
    glyph(ctx, 52, 54, 44, "#000000");
  } else {
    glyph(ctx, 52, 57.5, 44, SHADOW);
    glyph(ctx, 52, 54, 44, NAVY);
    sparkle(ctx, 74, 36, 5);
    sparkle(ctx, 67, 28, 2.5);
  }
  return c.toDataURL("image/png");
}

document.fonts.load("700 100px Naskh", "ح").then(() => {
  const out = {};
  for (const px of SIZES) out[px] = { fg: layer(px, false), mono: layer(px, true) };
  document.getElementById("out").textContent = JSON.stringify(out);
});
"""


def render():
    font = base64.b64encode(FONT.read_bytes()).decode()
    sizes = json.dumps(sorted(set(DENSITIES.values())))
    html = (
        "<!doctype html><meta charset=utf-8><style>@font-face{font-family:Naskh;"
        f"src:url(data:font/ttf;base64,{font});font-weight:400 700}}</style>"
        f"<pre id=out></pre><script>const SIZES = {sizes};{DRAW}</script>"
    )
    with tempfile.TemporaryDirectory() as tmp:
        page = Path(tmp) / "icon.html"
        page.write_text(html, encoding="utf-8")
        dom = subprocess.run(
            [EDGE, "--headless=new", "--disable-gpu", "--virtual-time-budget=10000", "--dump-dom", page.as_uri()],
            capture_output=True, text=True, encoding="utf-8", check=True,
        ).stdout
    found = re.search(r'<pre id="out">(.*?)</pre>', dom, re.S)
    if not found or not found.group(1):
        sys.exit("Edge returned no icon; is the font path right?")
    data = json.loads(found.group(1))

    def png(url):
        return Image.open(io.BytesIO(base64.b64decode(url.split(",", 1)[1])))

    return {int(px): {k: png(v) for k, v in layers.items()} for px, layers in data.items()}


def main():
    layers = render()
    for density, px in DENSITIES.items():
        folder = RES / f"mipmap-{density}"
        layers[px]["fg"].save(folder / "ic_launcher_foreground.png", optimize=True)
        layers[px]["mono"].save(folder / "ic_launcher_monochrome.png", optimize=True)
        print("wrote", folder.relative_to(ROOT), px, "px")


if __name__ == "__main__":
    main()
