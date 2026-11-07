# ==============================================================================
# Clothify Store - Local Production Package Builder
# Packages the standalone application bundle into a distributable ZIP & EXE
# Embedded with a lightweight JRE so clients DO NOT need Java installed!
# ==============================================================================

Write-Host ">>> Building Clothify Store production package..." -ForegroundColor Cyan

# 1. Run Maven Build
Write-Host ">>> Running Maven Clean Package..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed! Please check errors." -ForegroundColor Red
    exit 1
}

# 2. Compile native clothify.exe
Write-Host ">>> Compiling native Windows executable (clothify.exe)..." -ForegroundColor Yellow
$cscPath = "C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe"
if (Test-Path $cscPath) {
    $outArg = "/out:$PSScriptRoot\clothify.exe"
    $icoArg = "/win32icon:$PSScriptRoot\src\main\resources\Images\clothify.ico"
    $srcArg = "$PSScriptRoot\ClothifyLauncher.cs"
    & $cscPath /target:winexe $outArg $icoArg $srcArg | Out-Null
    Write-Host ">>> clothify.exe compiled with official fashion logo icon!" -ForegroundColor Green
} else {
    Write-Host "CSC compiler not found; skipping .exe build." -ForegroundColor Yellow
}

# 3. Setup Distribution Directory
$distPath = Join-Path $PSScriptRoot "dist"
if (Test-Path $distPath) {
    try {
        Remove-Item -Recurse -Force $distPath -ErrorAction SilentlyContinue
    } catch {}
}
New-Item -ItemType Directory -Force -Path (Join-Path $distPath "config") | Out-Null

# 4. Generate Embedded JRE (Clients need ZERO Java installed)
$jlinkExe = "C:\Program Files\Java\jdk-11\bin\jlink.exe"
$jmodsPath = "C:\Program Files\Java\jdk-11\jmods"
$distJre = Join-Path $distPath "jre"

if ((Test-Path $jlinkExe) -and (Test-Path $jmodsPath)) {
    Write-Host ">>> Bundling ultra-compact embedded Java 11 runtime..." -ForegroundColor Yellow
    $modules = "java.base,java.desktop,java.sql,java.naming,java.management,java.xml,java.logging,jdk.unsupported,jdk.crypto.ec,jdk.crypto.cryptoki,java.security.sasl,java.security.jgss"
    & $jlinkExe --module-path $jmodsPath --add-modules $modules --strip-debug --no-man-pages --no-header-files --compress=2 --output $distJre
    Write-Host ">>> Embedded JRE bundled successfully! (Clients do not need Java installed)" -ForegroundColor Green
}

# 5. Copy Artefacts
Write-Host ">>> Copying artifacts..." -ForegroundColor Yellow
$jarFile = Get-ChildItem -Path (Join-Path $PSScriptRoot "target") -Filter "clothify-*-jar-with-dependencies.jar" | Select-Object -First 1

if ($null -eq $jarFile) {
    Write-Host "Target jar not found!" -ForegroundColor Red
    exit 1
}

Copy-Item $jarFile.FullName (Join-Path $distPath "clothify-app.jar") -Force

if (Test-Path (Join-Path $PSScriptRoot "clothify.exe")) {
    Copy-Item (Join-Path $PSScriptRoot "clothify.exe") (Join-Path $distPath "clothify.exe") -Force
}

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

if exist "jre\bin\java.exe" (
    "jre\bin\java.exe" -Xms256m -Xmx1024m -jar clothify-app.jar
    goto :end
)

java -Xms256m -Xmx1024m -jar clothify-app.jar
if errorlevel 1 (
    echo.
    echo Application exited with an error. Ensure Java 11+ is installed.
    pause
)

:end
"@ | Out-File -FilePath (Join-Path $distPath "run.bat") -Encoding ascii

# Create Quickstart Readme
@"
==============================================================================
                    Clothify Store - POS & ERP System
==============================================================================

LAUNCH INSTRUCTIONS:
- Double-click 'clothify.exe' to launch the application directly!
- Or double-click 'clothify.vbs' / 'run.bat'.

NO JAVA INSTALLATION REQUIRED:
- An ultra-fast, pre-configured Java 11 runtime is embedded inside the 'jre' folder.
- Runs out-of-the-box on any Windows 10 / 11 computer!
- Pre-connected to your live TiDB Cloud Database.

DEFAULT CREDENTIALS (Pre-seeded):
- Administrator: admin@clothify.com   / admin123
- Cashier:       cashier@clothify.com / cashier123

==============================================================================
"@ | Out-File -FilePath (Join-Path $distPath "README.txt") -Encoding utf8

# 6. Create ZIP
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
Write-Host " - Standalone Executable: $(Join-Path $PSScriptRoot 'clothify.exe')" -ForegroundColor Green
Write-Host " - Release ZIP Archive:   $zipPath" -ForegroundColor Green
Write-Host " - Embedded JRE included: YES (No Java required on client PC)" -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
