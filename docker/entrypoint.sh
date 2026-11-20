#!/bin/sh
set -e

exec java \
      -Xms128M \
      -Xmx128M \
      -XX:-UseBASE64Intrinsics \
      -jar workshop-app.jar "$@"
