# Sourced by the gate scripts. Runs from repo root; locates the Android SDK.
set -eu
cd "$(dirname "$0")/../.."
if [ -z "${ANDROID_HOME:-}" ]; then
  for d in "${LOCALAPPDATA:-}/Android/Sdk" "$HOME/Android/Sdk" "$HOME/Library/Android/sdk"; do
    if [ -d "$d" ]; then ANDROID_HOME="$d"; break; fi
  done
fi
if [ -z "${ANDROID_HOME:-}" ]; then echo "FAIL: ANDROID_HOME not found" >&2; exit 1; fi
export ANDROID_HOME
