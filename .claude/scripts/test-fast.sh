#!/bin/sh
# Gate: JVM unit tests only. No device needed.
. "$(dirname "$0")/_env.sh"
./gradlew --console=plain testDebugUnitTest
echo "test-fast.sh: PASS (testDebugUnitTest)"
