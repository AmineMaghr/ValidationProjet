@echo off
REM Script principal de lancement - Midgar JavaFX
REM Détecte automatiquement la meilleure méthode de lancement

echo.
echo ====================================
echo  Midgar JavaFX - Lancement Auto
echo ====================================
echo.

echo Detection de l'environnement...
echo.

REM Vérifier Maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo Maven non trouve - Utilisation du mode modules JavaFX
    goto :modules
) else (
    echo Maven trouve - Utilisation du mode Maven recommande
    goto :maven
)

:maven
echo.
echo [MODE MAVEN] Lancement avec Maven...
echo.
call run_maven.bat
goto :end

:modules
echo.
echo [MODE MODULES] Lancement avec modules JavaFX...
echo.
call run_modules.bat
goto :end

:end
echo.
echo ====================================
echo  Script termine
echo ====================================
echo.
if errorlevel 1 (
    echo.
    echo Si le lancement a echoue, consultez:
    echo - JAVAFX_FIX.md pour les solutions
    echo - diagnose.bat pour le diagnostic
)
echo.
pause
