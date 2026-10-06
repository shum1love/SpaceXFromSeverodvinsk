#!/bin/sh
# Тесты: ./t   (после ./setup — просто t из любого места)
set -e
cd "$(dirname "$0")"
mvn clean test
