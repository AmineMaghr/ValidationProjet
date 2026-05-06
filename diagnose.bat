@echo off
REM Script de diagnostic - Midgar JavaFX
REM Vérifie l'environnement et diagnostique les problèmes JavaFX

echo.
echo ====================================
echo  DIAGNOSTIC - Midgar JavaFX
echo ====================================
echo.

echo [1/6] Verification de Java...
java -version
if errorlevel 1 (
    echo ❌ ERREUR: Java n'est pas installe ou pas dans le PATH
    goto :error
) else (
    echo ✅ Java detecte
)

echo.
echo [2/6] Verification de la version Java...
java -version 2>&1 | findstr "version" | findstr "1[1-9]\|2[0-9]"
if errorlevel 1 (
    echo ❌ ERREUR: Java 11+ requis (vous avez une version trop ancienne)
    goto :error
) else (
    echo ✅ Version Java compatible (11+)
)

echo.
echo [3/6] Verification de JavaFX dans le JDK...
java --list-modules 2>nul | findstr "javafx" >nul 2>&1
if errorlevel 1 (
    echo ❌ JavaFX n'est pas inclus dans votre JDK
) else (
    echo ✅ JavaFX detecte dans le JDK
)

echo.
echo [4/6] Verification de Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven n'est pas installe
    echo.
    echo SOLUTION: Utilisez start.bat ou run_modules.bat
) else (
    echo ✅ Maven detecte
    echo.
    echo SOLUTION: Utilisez start.bat ou run_maven.bat
    mvn -version | findstr "Apache Maven"
)

echo.
echo [5/6] Verification du repertoire projet...
if exist "pom.xml" (
    echo ✅ pom.xml trouve
) else (
    echo ❌ pom.xml manquant
    goto :error
)

if exist "src\main\java\com\example\app\Main.java" (
    echo ✅ Classe Main trouvee
) else (
    echo ❌ Classe Main manquante
    goto :error
)

echo.
echo [6/6] Test de compilation Maven...
mvn clean compile -q >nul 2>&1
if errorlevel 1 (
    echo ❌ Erreur de compilation Maven
    echo.
    echo Tentative avec details...
    mvn clean compile 2>&1 | findstr /i "error\|exception" | head -10
) else (
    echo ✅ Compilation Maven reussie
)

echo.
echo ====================================
echo  RESULTATS DU DIAGNOSTIC
echo ====================================
echo.

REM Afficher les resultats
java --list-modules 2>nul | findstr "javafx" >nul 2>&1
if errorlevel 1 (
    echo ❌ PROBLEME: JavaFX manquant
    echo.
    echo SOLUTIONS:
    echo 1. Utilisez un JDK avec JavaFX inclus (Liberica JDK Full)
    echo 2. Installez JavaFX separement et configurez le module-path
    echo 3. Utilisez Maven pour gerer automatiquement les dependances
    echo.
    echo Consultez JAVAFX_FIX.md pour les details
) else (
    mvn -version >nul 2>&1
    if errorlevel 1 (
        echo ❌ PROBLEME: Maven manquant
        echo.
        echo SOLUTION:
        echo Installez Maven depuis https://maven.apache.org/download.cgi
    ) else (
        echo ✅ ENVIRONNEMENT OK
        echo.
        echo Vous pouvez lancer l'application avec:
        echo mvn javafx:run
        echo ou
        echo run.bat
    )
)

goto :end

:error
echo.
echo ❌ DIAGNOSTIC ECHOUE
echo.
echo Consultez JAVAFX_FIX.md pour resoudre les problemes

:end
echo.
echo ====================================
echo  FIN DU DIAGNOSTIC
echo ====================================
echo.
pause
