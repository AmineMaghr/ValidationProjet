@echo off
REM Script alternatif - Midgar JavaFX (sans Maven)
REM Utilise Java directement avec les modules JavaFX

echo.
echo ====================================
echo  Midgar JavaFX - Lancement Direct
echo ====================================
echo.

REM Vérifier Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Java n'est pas installé ou pas dans le PATH
    echo Téléchargez Java 17+ depuis https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo Java détecté. Vérification des modules JavaFX...
echo.

REM Vérifier si JavaFX est disponible
java --list-modules | findstr "javafx" >nul 2>&1
if errorlevel 1 (
    echo AVERTISSEMENT: JavaFX n'est pas inclus dans votre JDK
    echo.
    echo Solutions:
    echo 1. Utilisez un JDK avec JavaFX inclus (Liberica JDK Full)
    echo 2. Téléchargez JavaFX séparément et ajoutez-le au module-path
    echo 3. Utilisez Maven pour gérer les dépendances automatiquement
    echo.
    echo Tentative de lancement avec Maven...
    echo.

    REM Essayer Maven si disponible
    where mvn >nul 2>&1
    if errorlevel 1 (
        echo ERREUR: Maven n'est pas installé non plus.
        echo.
        echo Solutions recommandées:
        echo 1. Installer Maven: https://maven.apache.org/download.cgi
        echo 2. Ou utiliser un IDE (IntelliJ IDEA, Eclipse)
        echo 3. Ou utiliser un JDK avec JavaFX inclus
        pause
        exit /b 1
    ) else (
        echo Maven trouvé. Lancement avec Maven...
        call mvn clean compile javafx:run
        goto :end
    )
) else (
    echo JavaFX détecté dans le JDK. Tentative de compilation directe...
    echo.

    REM Créer les répertoires de sortie
    if not exist "target\classes" mkdir target\classes 2>nul

    REM Compiler avec JavaFX
    echo Compilation en cours...
    javac --module-path "C:\path\to\javafx\lib" --add-modules javafx.controls,javafx.fxml -cp "src/main/java" -d target/classes src/main/java/com/example/app/*.java src/main/java/com/example/app/**/*.java 2>nul

    if errorlevel 1 (
        echo ERREUR: Compilation échouée.
        echo Le chemin JavaFX n'est pas correct.
        echo.
        echo Modifiez le --module-path dans ce script avec le bon chemin vers JavaFX.
        pause
        exit /b 1
    )

    echo Compilation réussie. Lancement de l'application...
    echo.

    REM Lancer l'application
    java --module-path "C:\path\to\javafx\lib" --add-modules javafx.controls,javafx.fxml -cp target/classes com.example.app.Main

    goto :end
)

:end
if errorlevel 1 (
    echo.
    echo ERREUR: Le lancement a échoué.
    echo Consultez les messages d'erreur ci-dessus.
) else (
    echo.
    echo Application fermée avec succès.
)
echo.
pause
