#!/bin/sh
# Полигон: ./q 13 | ./q strings | ./q Quest13_Strings | ./q (список)
# После ./setup — просто q 13 из любого места.
set -e
cd "$(dirname "$0")"
if [ -z "$1" ]; then
  ls quests/Quest*.java | sed 's|.*/||; s|\.java||'
  exit 0
fi
KEY="$1"
FILE=""
case "$KEY" in
  ''|*[!0-9]*) ;;
  *) NUM=$(printf '%02d' "$KEY"); FILE=$(ls quests/Quest*.java | grep "Quest${NUM}_" | head -1) ;;
esac
[ -z "$FILE" ] && FILE=$(ls quests/Quest*.java | grep -i "Quest${KEY}" | head -1)
[ -z "$FILE" ] && FILE=$(ls quests/Quest*.java | grep -i "$KEY" | head -1)
if [ -z "$FILE" ]; then
  echo "Не найдено: $KEY"
  exit 1
fi
NAME=$(basename "$FILE" .java)
TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT INT TERM
javac -encoding UTF-8 -d "$TMP" "$FILE"
java -cp "$TMP" "$NAME"
