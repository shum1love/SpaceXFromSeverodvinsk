#!/bin/sh
# Терминал: ./c   (после ./setup — просто c из любого места)
set -e
cd "$(dirname "$0")"
mvn -q package -DskipTests
java -jar target/SpaceXFromSeverodvinsk-1.0-SNAPSHOT.jar
