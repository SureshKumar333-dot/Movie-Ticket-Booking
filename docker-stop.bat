@echo off
title Stop CineVerse Docker Containers
cd /d "%~dp0"

echo ========================================================
echo        Stopping CineVerse Docker Containers
echo ========================================================
echo.

docker compose down

echo.
echo [SUCCESS] CineVerse Docker containers stopped.
echo.
pause
