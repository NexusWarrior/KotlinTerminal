#!/bin/bash

set -e

cd "$(dirname "$0")/.."

./run.sh --vfs "data/vfs" --script "scripts/startup.txt"