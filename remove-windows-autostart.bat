@echo off
title Disable CineVerse Auto-Startup
cd /d "%~dp0"

echo ========================================================
echo   Removing CineVerse from Windows Auto-Startup
echo ========================================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\disable-autostart.ps1"

echo.
echo [SUCCESS] CineVerse will no longer start automatically on boot.
echo.
pause
