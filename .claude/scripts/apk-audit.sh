#!/bin/sh
# Gate: Families audit of the built APK (plan 08 decision 12). Called by check.sh.
# Fails on a permission a kids' app must not ask for, and on a release runtime dependency
# outside .claude/scripts/allowed-deps.txt.
. "$(dirname "$0")/_env.sh"
AAPT2=$(ls -d "$ANDROID_HOME"/build-tools/* | sort -V | tail -1)/aapt2
# The newest APK built; check.sh has just built debug, a later assembleRelease wins.
APK=$(ls -t app/build/outputs/apk/release/app-release.apk app/build/outputs/apk/debug/app-debug.apk 2>/dev/null | head -1)
if [ -z "$APK" ]; then echo "FAIL: no APK built"; exit 1; fi
FAIL=0

PERMS=$("$AAPT2" dump permissions "$APK" | sed -n "s/^uses-permission: name='\([^']*\)'.*/\1/p")
for p in $PERMS; do
  case "$p" in
    android.permission.INTERNET | com.google.android.gms.permission.AD_ID | \
    android.permission.ACCESS_FINE_LOCATION | android.permission.ACCESS_COARSE_LOCATION | \
    android.permission.ACCESS_BACKGROUND_LOCATION | android.permission.CAMERA | \
    android.permission.RECORD_AUDIO | android.permission.READ_CONTACTS | \
    android.permission.WRITE_CONTACTS | android.permission.GET_ACCOUNTS)
      echo "FAIL: permission $p in $APK"; FAIL=1 ;;
    android.permission.ACCESS_NETWORK_STATE)
      if echo "$PERMS" | grep -qx android.permission.INTERNET; then
        echo "FAIL: permission $p with INTERNET in $APK"; FAIL=1
      fi ;;
  esac
done

DEPS=$(./gradlew -q --console=plain :app:dependencies --configuration releaseRuntimeClasspath |
  grep -oE '[-+\\]--- [A-Za-z0-9_.-]+:[A-Za-z0-9_.-]+' | sed 's/^.--- //' | sort -u)
set -f  # the allow-list lines are case patterns, not file globs
ALLOWED=$(grep -v '^#' .claude/scripts/allowed-deps.txt | grep -v '^[[:space:]]*$')
if [ -z "$DEPS" ]; then echo "FAIL: no dependencies listed for releaseRuntimeClasspath"; exit 1; fi
for d in $DEPS; do
  ok=0
  for a in $ALLOWED; do
    case "$d" in $a) ok=1; break ;; esac
  done
  if [ $ok = 0 ]; then echo "FAIL: dependency $d is not in allowed-deps.txt"; FAIL=1; fi
done

if [ $FAIL = 1 ]; then exit 1; fi
echo "apk-audit.sh: PASS ($(echo "$PERMS" | grep -c .) permissions, $(echo "$DEPS" | wc -l | tr -d ' ') dependencies, $APK)"
