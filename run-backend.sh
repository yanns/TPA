#!/bin/bash

DIRNAME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "starting applications"

$DIRNAME/backends/PlayerService/target/universal/stage/bin/playerservice -Dhttp.port=9001 &
$DIRNAME/backends/VideoService/target/universal/stage/bin/videoservice -Dhttp.port=9002 &


sleep 2
echo "all applications are running"
read -p "press key to stop"

echo "killing applications"
player_pid=`cat $DIRNAME/backends/PlayerService/target/universal/stage/RUNNING_PID`
kill -SIGTERM $player_pid

video_pid=`cat $DIRNAME/backends/VideoService/target/universal/stage/RUNNING_PID`
kill -SIGTERM $video_pid

