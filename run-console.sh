#!/bin/sh
# Консольная версия: собирает jar и запускает игру в терминале.
set -e
cd "$(dirname "$0")"
mvn -q package -DskipTests
java -jar target/SpaceXFromSeverodvinsk-1.0-SNAPSHOT.jar
