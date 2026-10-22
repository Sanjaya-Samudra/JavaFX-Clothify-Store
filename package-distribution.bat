@echo off
setlocal enabledelayedexpansion
title Packaging Clothify Production Release...
echo ========================================================
echo        Clothify Production Release Packaging Tool
echo ========================================================

cd /d "%~dp0\clothify"
echo [*] Compiling and packaging standalone binaries...
call mvn clean package -DskipTests -o
if errorlevel 1 (
    echo [ERROR] Maven build failed.
    pause
    exit /b 1
)

cd /d "%~dp0"
set "DIST_DIR=%~dp0dist\Clothify-Store-v1.0"
echo [*] Creating distribution folder at %DIST_DIR%...
if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"
mkdir "%DIST_DIR%"
mkdir "%DIST_DIR%\config"

echo [*] Copying executable JAR...
copy "clothify\target\clothify-1.0-SNAPSHOT-jar-with-dependencies.jar" "%DIST_DIR%\Clothify.jar" >nul

echo [*] Copying default database configuration...
copy "clothify\config\database.properties.example" "%DIST_DIR%\config\database.properties" >nul

echo [*] Generating production launcher script...
(
    echo @echo off
    echo title Clothify Store - Enterprise POS ^& ERP System
    echo cd /d "%%~dp0"
    echo java -Xms256m -Xmx1024m -jar Clothify.jar
    echo if errorlevel 1 pause
) > "%DIST_DIR%\Clothify.bat"

echo [*] Generating distribution instructions...
(
    echo =====================================================================
    echo                 Clothify Store - Production Deployment
    echo =====================================================================
    echo.
    echo PREREQUISITES:
    echo 1. Java 11 or higher installed on this machine.
    echo    (Verify by opening Command Prompt and typing: java -version^)
    echo.
    echo 2. MySQL Database running (Localhost, Local LAN Server, or Cloud^).
    echo    Make sure the 'clothify' database exists:
    echo        CREATE DATABASE IF NOT EXISTS clothify;
    echo.
    echo CONFIGURATION:
    echo Open 'config/database.properties' in any text editor and update:
    echo    db.url=jdbc:mysql://^<DATABASE_HOST^>:3306/clothify?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=UTC
    echo    db.user=^<DATABASE_USER^>
    echo    db.password=^<DATABASE_PASSWORD^>
    echo.
    echo LAUNCHING:
    echo Double-click 'Clothify.bat' to launch the application.
    echo.
    echo DEFAULT CREDENTIALS:
    echo - Administrator: one@gmail.com / one
    echo - Cashier:       two@gmail.com / two
    echo.
    echo =====================================================================
) > "%DIST_DIR%\README.txt"

echo [*] Compressing into release ZIP archive...
powershell -NoProfile -Command "Compress-Archive -Path '%DIST_DIR%' -DestinationPath '%~dp0dist\Clothify-Store-v1.0.zip' -Force"

echo ========================================================
echo  BUILD COMPLETE!
echo  Release Directory: %DIST_DIR%
echo  Release Archive:   %~dp0dist\Clothify-Store-v1.0.zip
echo ========================================================
pause
