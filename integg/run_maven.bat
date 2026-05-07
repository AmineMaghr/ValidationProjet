@echo off
REM Script de lancement Maven - Midgar JavaFX
REM Utilise Maven pour gérer automatiquement JavaFX

echo.
echo ====================================
echo  Midgar JavaFX - Lancement Maven
echo ====================================
echo.

REM Vérifier Maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Maven n'est pas installe ou pas dans le PATH
    echo.
    echo Solutions:
    echo 1. Installez Maven: https://maven.apache.org/download.cgi
    echo 2. Ajoutez Maven au PATH systeme
    echo 3. Utilisez run_modules.bat comme alternative
    echo.
    echo Ou utilisez IntelliJ IDEA qui inclut Maven
    pause
    exit /b 1
)

echo Maven detecte. Verification du projet...
echo.

REM Vérifier pom.xml
if not exist "pom.xml" (
    echo ERREUR: pom.xml non trouve dans le repertoire courant
    cd /d "%~dp0"
    if not exist "pom.xml" (
        echo ERREUR: Impossible de trouver pom.xml
        echo Assurez-vous d'etre dans le bon repertoire
        pause
        exit /b 1
    )
)

echo Projet Maven trouve. Telechargement des dependances...
echo.

REM Télécharger les dépendances
call mvn dependency:resolve -q
if errorlevel 1 (
    echo.
    echo ERREUR: Impossible de telecharger les dependances
    echo Verifiez votre connexion internet
    pause
    exit /b 1
)

echo Dependances telechargees. Compilation...
echo.

REM Compiler
call mvn compile -q
if errorlevel 1 (
    echo.
    echo ERREUR: Compilation echouee
    echo Consultez les erreurs ci-dessus
    pause
    exit /b 1
)

echo Compilation reussie. Lancement de l'application...
echo.

REM Lancer avec le plugin JavaFX Maven
call mvn javafx:run

if errorlevel 1 (
    echo.
    echo ERREUR: Lancement echoue
    echo.
    echo Solutions alternatives:
    echo 1. Utilisez run_modules.bat
    echo 2. Configurez IntelliJ IDEA (voir Midgar_JavaFX.xml)
    echo 3. Utilisez un JDK avec JavaFX inclus
    echo.
    echo Consultez JAVAFX_FIX.md pour plus de details
) else (
    echo.
    echo Application fermee avec succes.
)

echo.
pause
