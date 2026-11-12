@echo off
setlocal
cd /d "%~dp0"

if exist "target\LESH-1.1.0.jar" (
    java -jar "target\LESH-1.1.0.jar"
    goto :eof
)

where mvn >nul 2>nul
if errorlevel 1 (
    echo Maven ne naiden. Ustanovite Maven 3.9+ ili sobерите proekt v IDE.
    echo Trebuetsya JDK 21.
    pause
    exit /b 1
)

mvn clean javafx:run
