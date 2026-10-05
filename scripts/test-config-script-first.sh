#!/bin/bash

set -e

cd "$(dirname "$0")/.."

./run.sh --script "scripts/startup.txt" --vfs "data/vfs"