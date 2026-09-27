@echo off
title Enable CineVerse Auto-Startup
cd /d "%~dp0"

echo ========================================================
echo   Configuring CineVerse to Start Automatically on Boot
echo ========================================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\enable-autostart.ps1"

echo.
echo [SUCCESS] CineVerse will now automatically start whenever Windows boots!
echo.
pause
