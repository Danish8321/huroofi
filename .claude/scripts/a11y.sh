#!/bin/sh
# Gate: accessibility of the screen the emulator shows now (plan 08 decision 11).
# Fails on a clickable / long-clickable node with no label (its own text or content-desc,
# or a labelled child), and on touch targets under 64 dp (child screens) or 48 dp (--parent).
# Usage: a11y.sh [--parent]   — one emulator or device attached, app on screen.
. "$(dirname "$0")/_env.sh"
MIN=64
if [ "${1:-}" = "--parent" ]; then MIN=48; fi
ADB="$ANDROID_HOME/platform-tools/adb"
PY=python3; "$PY" -c "" 2>/dev/null || PY=python
DIR=$(mktemp -d)
trap 'rm -rf "$DIR"' EXIT
MSYS_NO_PATHCONV=1 "$ADB" shell rm -f /sdcard/a11y.xml
OUT=$(MSYS_NO_PATHCONV=1 "$ADB" shell uiautomator dump /sdcard/a11y.xml 2>&1 || true)
case "$OUT" in *dumped*) ;; *) echo "FAIL: uiautomator dump: $OUT"; exit 1 ;; esac
MSYS_NO_PATHCONV=1 "$ADB" exec-out cat /sdcard/a11y.xml > "$DIR/ui.xml"
DENSITY=$("$ADB" shell wm density | tail -1 | tr -dc '0-9')
"$PY" - "$DIR/ui.xml" "$DENSITY" "$MIN" <<'PY'
import re, sys
import xml.etree.ElementTree as ET

path, density, min_dp = sys.argv[1], int(sys.argv[2]), int(sys.argv[3])
APP = "com.huroofi.app"
# The parent lock is small on purpose so a toddler does not hit it (plan 05 decision 1); it keeps 48 dp.
EXEMPT = {"Grown-ups: press and hold to open settings": 48}


def box(n):
    return tuple(int(v) for v in re.findall(r"\d+", n.get("bounds")))


def label(n):
    return (n.get("text") or "").strip() or (n.get("content-desc") or "").strip()


def labelled(n):
    return bool(label(n)) or any(labelled(c) for c in n)


def name(n):
    own = label(n)
    if own:
        return own
    for c in n.iter():
        if label(c):
            return label(c) + " (child)"
    return "<no label>"


bad, checked, clipped = [], 0, []


def walk(n, scroller):
    global checked
    if n.get("package") == APP and (n.get("clickable") == "true" or n.get("long-clickable") == "true"):
        checked += 1
        b = box(n)
        w, h = (b[2] - b[0]) * 160 / density, (b[3] - b[1]) * 160 / density
        # A node cut by its scroll container has off-screen children missing from the dump and a
        # size that is not its own; scroll it into view to check it.
        if scroller and (b[1] <= scroller[1] or b[3] >= scroller[3] or b[0] < scroller[0] or b[2] > scroller[2]):
            clipped.append(f"{name(n)} {n.get('bounds')}")
            return
        if not labelled(n):
            bad.append(f"no label        {n.get('class')} {n.get('bounds')}")
        need = min(min_dp, EXEMPT.get(name(n).removesuffix(" (child)"), min_dp))
        if w < need or h < need:
            bad.append(f"{w:.0f}x{h:.0f} dp < {need}  {name(n)} {n.get('bounds')}")
    if n.get("scrollable") == "true":
        scroller = box(n)
    for c in n:
        walk(c, scroller)


walk(ET.parse(path).getroot(), None)
for c in clipped:
    print(f"skipped, clipped by scroll: {c}")
for b in bad:
    print(f"FAIL: {b}")
if checked == 0:
    print(f"FAIL: no clickable {APP} node on screen")
    sys.exit(1)
if bad:
    sys.exit(1)
print(f"a11y.sh: PASS ({checked} targets, min {min_dp} dp)")
PY
