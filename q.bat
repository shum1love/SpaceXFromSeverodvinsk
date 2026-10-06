@echo off
rem Полигон: q 13 | q strings | q Quest13_Strings | q (список)
cd /d %~dp0
if "%~1"=="" (
  dir /b quests\Quest*.java
  exit /b 0
)
set KEY=%~1
echo %KEY%| findstr /r "^[0-9][0-9]*$" >nul
if %errorlevel%==0 (
  if "%KEY:~1,1%"=="" set KEY=0%KEY%
  for %%f in (quests\Quest%KEY%_*.java) do ( call :run "%%f" & exit /b 0 )
)
for %%f in (quests\Quest%KEY%*.java) do ( call :run "%%f" & exit /b 0 )
for %%f in (quests\Quest*%KEY%*.java) do ( call :run "%%f" & exit /b 0 )
echo Не найдено: %KEY%
exit /b 1
:run
for %%n in (%1) do set NAME=%%~nn
set TMPDIR=%TEMP%\quest-%RANDOM%
mkdir "%TMPDIR%" 2>nul
javac -encoding UTF-8 -d "%TMPDIR%" "quests\%NAME%.java" || exit /b 1
java -cp "%TMPDIR%" "%NAME%"
rmdir /s /q "%TMPDIR%"
exit /b 0
