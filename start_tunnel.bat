@echo off
chcp 65001 >nul
title KHOI CHAY CLOUDFLARE TUNNEL - VJU

cd /d "%~dp0"
echo ==============================================================================
echo   DANG TAO LINK CLOUDFLARE TUNNEL TRO VAO PORT 8080 (SPRING BOOT)
echo ==============================================================================
echo.
.\cloudflared.exe tunnel --url http://localhost:8080
pause
