@echo off
REM Script de lancement JavaFX avec modules - Midgar
REM Utilise les JARs Maven locaux avec le module path

echo.
echo ====================================
echo  Midgar JavaFX - Lancement Modules
echo ====================================
echo.

REM Définir les chemins Maven locaux
set MAVEN_REPO=%USERPROFILE%\.m2\repository
set JAVAFX_VERSION=17.0.2

REM Construire le module path avec tous les JARs JavaFX
set MODULE_PATH=%MAVEN_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%-win.jar
set MODULE_PATH=%MODULE_PATH%;%MAVEN_REPO%\org\openjfx\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%-win.jar
set MODULE_PATH=%MODULE_PATH%;%MAVEN_REPO%\org\openjfx\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%-win.jar
set MODULE_PATH=%MODULE_PATH%;%MAVEN_REPO%\org\openjfx\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%-win.jar

REM Vérifier si les JARs existent
if not exist "%MAVEN_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%-win.jar" (
    echo ERREUR: JARs JavaFX non trouves dans le repository Maven local
    echo.
    echo Executez d'abord: mvn clean install
    echo pour telecharger les dependances
    pause
    exit /b 1
)

echo JARs JavaFX trouves. Compilation et lancement...
echo.

REM Aller au répertoire du projet
cd /d "%~dp0"

REM Compiler le projet
echo Compilation...
javac -cp "target/classes" -d target/classes src/main/java/com/example/app/*.java src/main/java/com/example/app/**/*.java 2>nul
if errorlevel 1 (
    echo ERREUR: Compilation echouee
    pause
    exit /b 1
)

echo Compilation reussie. Lancement de l'application...
echo.

REM Lancer avec les modules JavaFX
java --module-path "%MODULE_PATH%" --add-modules javafx.controls,javafx.fxml --add-opens javafx.graphics/javafx.scene=ALL-UNNAMED --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED -cp target/classes com.example.app.Main

if errorlevel 1 (
    echo.
    echo ERREUR: Lancement echoue
    echo.
    echo Solutions:
    echo 1. Verifiez que les JARs JavaFX sont bien telecharges: mvn dependency:resolve
    echo 2. Verifiez votre JDK (Java 11+ requis)
    echo 3. Consultez JAVAFX_FIX.md pour plus de solutions
) else (
    echo.
    echo Application fermee avec succes.
)

echo.
pause
