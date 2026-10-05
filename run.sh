#!/bin/bash

set -e

cd "$(dirname "$0")"

if [ "$#" -eq 0 ]; then
    ./gradlew --console=plain run
else
    ./gradlew --console=plain run --args="$*"
fi