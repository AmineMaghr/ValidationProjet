@echo off
cd /d "C:\Users\user\Desktop\validation"
set URL=%1
echo %URL% > token.txt
mvn javafx:run
exit