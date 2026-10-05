@echo off
rem Консольная версия: собирает jar и запускает игру в терминале.
cd /d %~dp0
call mvn -q package -DskipTests
if errorlevel 1 exit /b 1
java -jar target\SpaceXFromSeverodvinsk-1.0-SNAPSHOT.jar
