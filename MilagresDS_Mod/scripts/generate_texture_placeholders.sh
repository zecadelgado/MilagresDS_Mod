#!/usr/bin/env bash
set -euo pipefail

BASE64_DATA="iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMB/axIYZYAAAAASUVORK5CYII="

decode_png() {
  local target="$1"
  mkdir -p "$(dirname "$target")"
  printf '%s' "$BASE64_DATA" | base64 --decode > "$target"
}

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")"/.. && pwd)"

decode_png "$ROOT/src/main/resources/assets/milagresdsmod/textures/gui/memorize_bg.png"
for name in lightningspear fireball healingburst fallback; do
  decode_png "$ROOT/src/main/resources/assets/milagresdsmod/textures/spells/${name}.png"
done

echo "Placeholder textures generated."
