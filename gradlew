#!/usr/bin/env sh
set -eu
GRADLE_VERSION="9.6.0"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
DIST_ROOT="$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin/caller-bootstrap"
GRADLE_HOME="$DIST_ROOT/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"
ZIP_PATH="$DIST_ROOT/gradle-$GRADLE_VERSION-bin.zip"

if [ ! -x "$GRADLE_BIN" ]; then
    mkdir -p "$DIST_ROOT"
    if [ ! -f "$ZIP_PATH" ]; then
        if command -v curl >/dev/null 2>&1; then
            curl -fL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ZIP_PATH"
        elif command -v wget >/dev/null 2>&1; then
            wget -O "$ZIP_PATH" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
        else
            echo "Caller build needs curl or wget to download Gradle." >&2
            exit 1
        fi
    fi
    command -v unzip >/dev/null 2>&1 || { echo "Caller build needs unzip." >&2; exit 1; }
    unzip -q -o "$ZIP_PATH" -d "$DIST_ROOT"
    rm -f "$ZIP_PATH"
fi
exec "$GRADLE_BIN" "$@"
