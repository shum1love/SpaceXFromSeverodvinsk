#!/bin/sh
# Нативное приложение: ./p   (после ./setup — просто p из любого места)
# jpackage НЕ умеет кросс-компиляцию: что за ОС, то и получится
# (macOS → .dmg, Windows → .exe через p.bat, Linux → .deb).
set -e
cd "$(dirname "$0")"
APP_JAR="SpaceXFromSeverodvinsk-1.0-SNAPSHOT.jar"
mvn -q package
mkdir -p target/libs
cp "target/$APP_JAR" target/libs/
jpackage \
  --name "SpaceCompany" \
  --app-version "1.0" \
  --description "Симулятор космической компании" \
  --input target/libs \
  --main-jar "$APP_JAR" \
  --main-class org.example.spacecompany.ui.FxLauncher \
  --module-path target/libs \
  --add-modules javafx.controls,javafx.fxml \
  --dest target/dist
echo "Готово: забирайте установщик из target/dist"
