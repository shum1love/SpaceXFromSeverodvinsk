#!/bin/sh
# Нативное приложение через jpackage для ТЕКУЩЕЙ ОС.
#
# Важно: jpackage НЕ умеет кросс-компиляцию — что за ОС, то и получится:
#   macOS   -> SpaceCompany-1.0.dmg
#   Windows -> SpaceCompany-1.0.exe (собирать на Windows через package-app.bat)
#   Linux   -> .deb (тип можно поменять флагом --type)
#
# macOS: неподписанное приложение первый раз открывайте правым кликом —
# «Открыть» (это норма для приложений без подписи Apple).
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
