@echo off
rem Нативное приложение: p (собирать строго на Windows: jpackage не умеет кросс-компиляцию)
setlocal
cd /d %~dp0
set APP_JAR=SpaceXFromSeverodvinsk-1.0-SNAPSHOT.jar
call mvn -q package
if errorlevel 1 exit /b 1
if not exist target\libs mkdir target\libs
copy /y "target\%APP_JAR%" target\libs\ >nul
jpackage ^
  --name "SpaceCompany" ^
  --app-version "1.0" ^
  --description "Симулятор космической компании" ^
  --input target\libs ^
  --main-jar "%APP_JAR%" ^
  --main-class org.example.spacecompany.ui.FxLauncher ^
  --module-path target\libs ^
  --add-modules javafx.controls,javafx.fxml ^
  --dest target\dist
echo Готово: забирайте установщик из target\dist
