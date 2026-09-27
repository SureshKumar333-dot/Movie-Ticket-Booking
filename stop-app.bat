@echo off
title Stop CineVerse Services
cd /d "%~dp0"

echo ========================================================
echo        Stopping CineVerse Services (Port 8080 & 3000)
echo ========================================================
echo.

powershell -ExecutionPolicy Bypass -Command "Get-NetTCPConnection -LocalPort 8080,3000 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique | ForEach-Object { Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue }"

echo CineVerse services stopped successfully.
pause
