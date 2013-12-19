#!/bin/bash

DIRNAME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "preparing applications"

pushd $DIRNAME/backends/PlayerService
sbt stage &
popd

pushd $DIRNAME/backends/VideoService
sbt stage &
popd

