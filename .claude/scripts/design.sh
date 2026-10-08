#!/bin/sh
# Gate: the review prototype. Builds design-reference/prototype.html, runs the scripted
# click-through (design-reference/prototype/flowtest.js) and regenerates the static
# reference screens in design-reference/screens/ from the prototype's static views.
# Needs node and a Chromium browser (Edge or Chrome; set BROWSER to override).
set -eu
cd "$(dirname "$0")/../.."
P=design-reference/prototype
if [ -z "${BROWSER:-}" ]; then
  for b in "/c/Program Files (x86)/Microsoft/Edge/Application/msedge.exe" "/c/Program Files/Microsoft/Edge/Application/msedge.exe" \
           "/c/Program Files/Google/Chrome/Application/chrome.exe" "$(command -v chromium 2>/dev/null || true)" "$(command -v google-chrome 2>/dev/null || true)"; do
    if [ -n "$b" ] && [ -x "$b" ]; then BROWSER="$b"; break; fi
  done
fi
if [ -z "${BROWSER:-}" ]; then echo "FAIL: no Chromium browser found (set BROWSER)"; exit 1; fi
node "$P/build.js" ${1:+"$1"}
url() { case "$(uname -s)" in MINGW*|MSYS*) echo "file:///$(cd "$(dirname "$1")" && pwd -W)/$(basename "$1")";; *) echo "file://$(cd "$(dirname "$1")" && pwd)/$(basename "$1")";; esac; }
dump() { "$BROWSER" --headless=new --disable-gpu --allow-file-access-from-files --virtual-time-budget="$1" --window-size="$2" --dump-dom "$3" 2>/dev/null; }

# Flow test. Headless virtual time does not run requestAnimationFrame, so the test copy maps it onto timers.
FLOW=design-reference/.flowtest.html
trap 'rm -f "$FLOW"' EXIT
{ sed 's|<head>|<head><script>window.requestAnimationFrame=(f)=>setTimeout(()=>f(performance.now()),16);window.cancelAnimationFrame=clearTimeout;</script>|' design-reference/prototype.html
  printf '<script>\n'; cat "$P/flowtest.js"; printf '</script>\n'; } > "$FLOW"
LOG=$(dump 60000 1500,1000 "$(url "$FLOW")#home" | sed -n '/id="testlog"/,/<\/pre>/p' | sed 's/<[^>]*>//g')
echo "$LOG"
if [ -z "$LOG" ]; then echo "FAIL: flow test did not finish"; exit 1; fi
if echo "$LOG" | grep -qE '^(FAIL|MISSING)'; then echo "FAIL: flow test"; exit 1; fi

# Static reference screens.
KEYS=$(node -e "const s=require('fs').readFileSync('$P/shell.js','utf8');const b=s.slice(s.indexOf('const STATIC = {'));console.log([...b.slice(0,b.indexOf('\n};')).matchAll(/^\s+'?([A-Za-z-]+)'?: \(\) =>/gm)].map(m=>m[1]).join(' '))")
N=0
for k in $KEYS; do
  size=390,844; [ "$k" = ToddlerFindTablet ] && size=1180,820
  dump 8000 "$size" "$(url design-reference/prototype.html)#static-$k" | node "$P/snapshot.js" "$k"
  N=$((N + 1))
done
echo "design.sh: PASS (prototype built, flow test passed, $N reference screens written)"
