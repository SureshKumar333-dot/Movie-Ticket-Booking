@echo off
title CineVerse Docker Live Logs
cd /d "%~dp0"

echo ========================================================
echo        Viewing CineVerse Docker Logs (Press Ctrl+C to exit)
echo ========================================================
echo.

docker compose logs -f
