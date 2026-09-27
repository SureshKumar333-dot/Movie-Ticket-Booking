@echo off
title CineVerse Full-Stack App
cd /d "%~dp0"

echo ========================================================
echo        Starting CineVerse (Frontend + Backend)
echo ========================================================
echo.

:: Ensure MySQL service is running if installed
net start MySQL80 >nul 2>&1

:: Start both frontend and backend concurrently
npm run dev:all
