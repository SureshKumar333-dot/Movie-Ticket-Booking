@echo off
title Start CineVerse with Docker
cd /d "%~dp0"

echo ========================================================
echo        Starting CineVerse with Docker Compose
echo ========================================================
echo.

docker compose up -d --build

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed to start Docker containers. Make sure Docker Desktop is running!
) else (
    echo.
    echo [SUCCESS] CineVerse is running in Docker!
    echo   - Frontend: http://localhost:3000
    echo   - Backend:  http://localhost:8080
    echo   - MySQL:    localhost:3306
)

echo.
pause
