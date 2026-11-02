#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
export JAVA_HOME="$DIR/tools/jdk-17.0.20.1+1"
export PATH="$JAVA_HOME/bin:$PATH"
exec "$DIR/tools/gradle-8.5/bin/gradle" "$@"
