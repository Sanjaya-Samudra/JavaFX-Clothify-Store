@echo off
title Clothify Store - Enterprise POS & ERP System
echo ========================================================
echo         Launching Clothify POS & ERP System...
echo ========================================================
cd /d "%~dp0"

if exist "target\clothify-1.0-SNAPSHOT-jar-with-dependencies.jar" (
    java -Xms256m -Xmx1024m -jar "target\clothify-1.0-SNAPSHOT-jar-with-dependencies.jar"
    goto :end
)

if exist "target\clothify-1.0-SNAPSHOT.jar" (
    java -Xms256m -Xmx1024m -jar "target\clothify-1.0-SNAPSHOT.jar"
    goto :end
)

set "CP=target\classes;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\19\javafx-controls-19-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\19\javafx-controls-19.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\19\javafx-fxml-19-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\19\javafx-fxml-19.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\19\javafx-graphics-19-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\19\javafx-graphics-19.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\19\javafx-base-19-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\19\javafx-base-19.jar;%USERPROFILE%\.m2\repository\com\jfoenix\jfoenix\9.0.1\jfoenix-9.0.1.jar;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\9.0.0\mysql-connector-j-9.0.0.jar"
java -cp "%CP%" Main

:end
if errorlevel 1 pause
