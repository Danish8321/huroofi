#!/bin/sh
# Gate: build + lint. Any lint error fails. No baselines, no suppressions (plan 04).
. "$(dirname "$0")/_env.sh"
if [ -e app/lint-baseline.xml ] || [ -e lint-baseline.xml ] || [ -e lint.xml ] || [ -e app/lint.xml ]; then
  echo "FAIL: lint baseline / lint.xml is forbidden"; exit 1
fi
./gradlew --console=plain assembleDebug lintDebug compileReleaseKotlin
echo "check.sh: PASS (assembleDebug + lintDebug + compileReleaseKotlin)"
