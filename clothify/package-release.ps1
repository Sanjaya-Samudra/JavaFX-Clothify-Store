# ==============================================================================
# Clothify Store - Local Production Package Builder
# Packages the standalone application bundle into a distributable ZIP archive
# ==============================================================================

Write-Host ">>> Building Clothify Store production package..." -ForegroundColor Cyan

# 1. Run Maven Build
Write-Host ">>> Running Maven Clean Package..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed! Please check errors." -ForegroundColor Red
    exit 1
}

# 2. Setup Distribution Directory
$distPath = Join-Path $PSScriptRoot "dist"
if (Test-Path $distPath) {
    try {
        Remove-Item -Recurse -Force $distPath -ErrorAction SilentlyContinue
    } catch {}
}
New-Item -ItemType Directory -Force -Path (Join-Path $distPath "config") | Out-Null

# 3. Copy Artefacts
Write-Host ">>> Copying artifacts..." -ForegroundColor Yellow
$jarFile = Get-ChildItem -Path (Join-Path $PSScriptRoot "target") -Filter "clothify-*-jar-with-dependencies.jar" | Select-Object -First 1

if ($null -eq $jarFile) {
    Write-Host "Target jar not found!" -ForegroundColor Red
    exit 1
}

Copy-Item $jarFile.FullName (Join-Path $distPath "clothify-app.jar") -Force

if (Test-Path (Join-Path $PSScriptRoot "config\database.properties")) {
    Copy-Item (Join-Path $PSScriptRoot "config\database.properties") (Join-Path $distPath "config\database.properties") -Force
} else {
    Copy-Item (Join-Path $PSScriptRoot "config\database.properties.example") (Join-Path $distPath "config\database.properties") -Force
}

Copy-Item (Join-Path $PSScriptRoot "clothify.vbs") (Join-Path $distPath "clothify.vbs") -Force

# Create standalone run.bat
@"
@echo off
title Clothify Store POS & ERP
cd /d "%~dp0"
echo Launching Clothify Store...
java -Xms256m -Xmx1024m -jar clothify-app.jar
if errorlevel 1 (
    echo.
    echo Application exited with an error. Ensure Java 11+ is installed.
    pause
)
"@ | Out-File -FilePath (Join-Path $distPath "run.bat") -Encoding ascii

# Create Quickstart Readme
@"
==============================================================================
                    Clothify Store - POS & ERP System
==============================================================================

REQUIREMENTS:
- Java 11 or higher (Free download from https://adoptium.net / Eclipse Temurin)
- MySQL database (Local MySQL or Free Cloud Database such as TiDB or Aiven)

QUICK START:
1. Open 'config/database.properties' in any text editor if you need to update DB settings.
2. Run the system:
   - Double-click 'clothify.vbs' for silent launch (no black console window).
   - Or double-click 'run.bat' for console mode with logs.

DEFAULT CREDENTIALS (Auto-generated on first run):
- Administrator: admin@clothify.com   / admin123
- Cashier:       cashier@clothify.com / cashier123

==============================================================================
"@ | Out-File -FilePath (Join-Path $distPath "README.txt") -Encoding utf8

# 4. Create ZIP
$zipPath = Join-Path $PSScriptRoot "Clothify-Store-v1.0.zip"
if (Test-Path $zipPath) {
    try {
        Remove-Item -Force $zipPath -ErrorAction SilentlyContinue
    } catch {}
}

Write-Host ">>> Compressing into Clothify-Store-v1.0.zip..." -ForegroundColor Yellow
Compress-Archive -Path "$distPath\*" -DestinationPath $zipPath -Force

Write-Host "==========================================================" -ForegroundColor Green
Write-Host " Production bundle created successfully!" -ForegroundColor Green
Write-Host " Output: $zipPath" -ForegroundColor Green
Write-Host " You can now distribute this ZIP or upload it to GitHub Releases!" -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
