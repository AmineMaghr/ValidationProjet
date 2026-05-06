@echo off
REM Script de lancement - Midgar JavaFX
REM Assurez-vous que Maven et Java 17+ sont installés

echo.
echo ====================================
echo  Midgar JavaFX - Script de lancement
echo ====================================
echo.

REM Set JAVA_HOME and PATH to IntelliJ's downloaded JDK 17
FOR /D %%I IN ("%USERPROFILE%\.jdks\*") DO (
    IF EXIST "%%I\bin\java.exe" (
        set "JAVA_HOME=%%I"
        goto :foundJdk
    )
)

:foundJdk
if "%JAVA_HOME%"=="" (
    echo ERREUR: Aucun JDK trouve dans %USERPROFILE%\.jdks
    pause
    exit /b 1
)

echo Utilisation de JAVA_HOME : %JAVA_HOME%
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Vérifier Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Java n'est pas installé ou pas dans le PATH
    echo Téléchargez Java 17+ depuis https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Aller au répertoire du projet
cd /d "%~dp0"

echo.
echo Nettoyage et compilation...
echo.

REM Compiler avec Maven wrapper ou Maven global
if exist "mvnw.cmd" (
    call mvnw.cmd clean install -DskipTests
) else (
    call mvn clean install -DskipTests
)

if errorlevel 1 (
    echo.
    echo ERREUR: La compilation a échoué!
    echo Vérifiez que Maven est correctement installé.
    pause
    exit /b 1
)

echo.
echo ====================================
echo  Lancement de l'application...
echo ====================================
echo.

REM Lancer l'application
if exist "mvnw.cmd" (
    call mvnw.cmd javafx:run
) else (
    call mvn javafx:run
)

if errorlevel 1 (
    echo.
    echo ERREUR: Le lancement a échoué!
    pause
    exit /b 1
)

echo.
echo L'application s'est fermée.
echo.
pause
