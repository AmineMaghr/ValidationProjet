@echo off
set URL=%1
echo %URL% > token_temp.txt
powershell -Command "Get-Content token_temp.txt | Out-File -FilePath \\.\pipe\token_pipe"
del token_temp.txt
exit