@echo off
REM Script de correction des fichiers
REM Remplace les fichiers par leurs versions corrigées

echo Sauvegarde des fichiers originaux...
copy "C:\Users\user\IdeaProjects\midgarpd\src\main\java\org\example\midgarpd\HelloApplication.java" "C:\Users\user\IdeaProjects\midgarpd\src\main\java\org\example\midgarpd\HelloApplication.java.backup"
copy "C:\Users\user\IdeaProjects\midgarpd\src\main\resources\com\monapp\view\index.fxml" "C:\Users\user\IdeaProjects\midgarpd\src\main\resources\com\monapp\view\index.fxml.backup"

echo Remplacement des fichiers...
copy "C:\Users\user\IdeaProjects\midgarpd\src\main\java\org\example\midgarpd\HelloApplication_FIXED.java" "C:\Users\user\IdeaProjects\midgarpd\src\main\java\org\example\midgarpd\HelloApplication.java"
copy "C:\Users\user\IdeaProjects\midgarpd\src\main\resources\com\monapp\view\index_FIXED.fxml" "C:\Users\user\IdeaProjects\midgarpd\src\main\resources\com\monapp\view\index.fxml"

echo Compilation du projet...
cd C:\Users\user\IdeaProjects\midgarpd
call mvn clean compile

echo Terminé!
pause

