#!/bin/sh
# Watches all "*-loader" containers and records how long each one lived, i.e.
# how long it took that ODM microservice to load its dataset before exiting.
#
# The container lifetime is taken straight from Docker's own bookkeeping
# (State.StartedAt / State.FinishedAt), so it is the exact wall-clock life of
# the loader container, independent of the microservice language.
set -eu

OUT_DIR="${OUT_DIR:-/logs}"
LOG_FILE="$OUT_DIR/load-times.log"
SEEN_DIR="$OUT_DIR/.seen"
mkdir -p "$OUT_DIR" "$SEEN_DIR"

record() {
  name="$1"

  started=$(docker inspect -f '{{.State.StartedAt}}' "$name" 2>/dev/null)  || return 0
  finished=$(docker inspect -f '{{.State.FinishedAt}}' "$name" 2>/dev/null) || return 0
  exitcode=$(docker inspect -f '{{.State.ExitCode}}' "$name" 2>/dev/null)   || exitcode="?"

  # Not actually finished yet (Docker uses this zero value while running).
  case "$finished" in
    0001-01-01T*) return 0 ;;
  esac

  # De-duplicate on (name, finishedAt): a fresh `docker compose up` recreates
  # the loader with a new FinishedAt, which we DO want to record again.
  marker="$SEEN_DIR/$(printf '%s_%s' "$name" "$finished" | tr -c 'A-Za-z0-9._-' '_')"
  [ -e "$marker" ] && return 0
  : > "$marker"

  s=$(date -d "$started" +%s.%N 2>/dev/null)  || return 0
  f=$(date -d "$finished" +%s.%N 2>/dev/null) || return 0
  dur=$(awk "BEGIN { printf \"%.3f\", $f - $s }")
  ts=$(date -u +%Y-%m-%dT%H:%M:%SZ)

  printf '%s\tservice=%s\tload_seconds=%s\texit_code=%s\n' \
    "$ts" "$name" "$dur" "$exitcode" >> "$LOG_FILE"
  echo "[load-timer] $name loaded in ${dur}s (exit $exitcode)"
}

echo "[load-timer] watching *-loader containers; logging to $LOG_FILE"

# Catch any loader that already exited before we subscribed to the event stream.
docker ps -a --filter 'name=-loader' --filter 'status=exited' --format '{{.Names}}' \
  | while read -r n; do [ -n "$n" ] && record "$n"; done

# React to every future container death, filtering to loaders.
docker events --filter 'event=die' --format '{{.Actor.Attributes.name}}' \
  | while read -r name; do
      case "$name" in
        *-loader) record "$name" ;;
      esac
    done
