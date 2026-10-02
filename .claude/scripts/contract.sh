#!/bin/sh
# Gate: content contract (plan 04). data/letters.json is the contract between content and app.
#  1. app copy of letters.json == pack copy (line endings ignored)
#  2. every pack card/illustration/font has its app resource (names per plan 01)
#  3. every audio path in letters.json has a file under app/src/main/assets
#  4. strict-decode + consistency unit tests pass
. "$(dirname "$0")/_env.sh"
fail=0
APP=app/src/main
RES=$APP/res

pack_sum=$(tr -d '\r' < data/letters.json | md5sum)
app_sum=$(tr -d '\r' < $APP/assets/letters.json | md5sum)
if [ "$pack_sum" != "$app_sum" ]; then
  echo "FAIL: $APP/assets/letters.json differs from data/letters.json"; fail=1
fi

for f in assets/cards/*.png; do
  [ -f "$RES/drawable-nodpi/card_$(basename "$f")" ] || { echo "FAIL: missing card resource for $f"; fail=1; }
done
for f in assets/illustrations/png/*.png; do
  [ -f "$RES/drawable-nodpi/pic_$(basename "$f")" ] || { echo "FAIL: missing picture resource for $f"; fail=1; }
done
[ -f "$RES/font/baloo_bhaijaan_2.ttf" ] || { echo "FAIL: missing baloo font resource"; fail=1; }
[ -f "$RES/font/noto_naskh_arabic.ttf" ] || { echo "FAIL: missing naskh font resource"; fail=1; }

for p in $(grep -o '"audio/[^"]*"' data/letters.json | tr -d '"'); do
  [ -f "$APP/assets/$p" ] || { echo "FAIL: missing audio file $p (run tools/make-placeholders.sh)"; fail=1; }
done

if [ "$fail" -ne 0 ]; then echo "contract.sh: FAIL"; exit 1; fi

./gradlew --console=plain testDebugUnitTest --tests 'com.huroofi.app.data.content.*'
echo "contract.sh: PASS (files in sync, content + audio contract tests)"
