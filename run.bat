@echo off
echo ==========================================================
echo Starting Fundora Social FinTech Web Application...
echo ==========================================================
cd /d "%~dp0"
"C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.1.3\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
pause
