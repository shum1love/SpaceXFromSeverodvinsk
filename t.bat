@echo off
rem Тесты: t
cd /d %~dp0
call mvn clean test
