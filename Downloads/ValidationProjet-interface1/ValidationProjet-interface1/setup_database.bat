@echo off
REM ============================================
REM Midgar JavaFX - MySQL Database Setup Script
REM ============================================
REM This script creates the midgar37 database
REM and initializes it with tables and test data.
REM ============================================

setlocal enabledelayedexpansion

echo.
echo ============================================
echo  Midgar JavaFX - Database Setup
echo ============================================
echo.

REM Check if MySQL is installed
echo [1/3] Checking for MySQL installation...
where mysql >nul 2>nul
if errorlevel 1 (
    echo.
    echo ❌ ERROR: MySQL is not installed or not in PATH
    echo.
    echo Please install MySQL Server and add it to PATH:
    echo - MySQL Community Server: https://dev.mysql.com/downloads/mysql/
    echo.
    pause
    exit /b 1
)

echo ✅ MySQL found

REM Test connection to MySQL
echo.
echo [2/3] Testing MySQL connection on port 4306...
mysql -h localhost -P 4306 -u root -e "SELECT 1" >nul 2>&1

if errorlevel 1 (
    echo.
    echo ❌ ERROR: Cannot connect to MySQL on port 4306
    echo.
    echo Possible solutions:
    echo 1. Start MySQL: net start MySQL80 (or your MySQL service name)
    echo 2. Check if port 4306 is in use
    echo 3. Verify MySQL is running: netstat -ano | findstr :4306
    echo.
    pause
    exit /b 1
)

echo ✅ MySQL connection successful

REM Create database and tables
echo.
echo [3/3] Creating database and tables...

REM Get the directory of this script
for %%I in ("%~dp0.") do set "SCRIPT_DIR=%%~fI"

if not exist "%SCRIPT_DIR%\init_database.sql" (
    echo.
    echo ❌ ERROR: init_database.sql not found in %SCRIPT_DIR%
    echo.
    pause
    exit /b 1
)

mysql -h localhost -P 4306 -u root < "%SCRIPT_DIR%\init_database.sql"

if errorlevel 1 (
    echo.
    echo ❌ ERROR: Failed to create database
    echo.
    pause
    exit /b 1
)

echo ✅ Database created successfully

REM Verify the database was created
echo.
echo ============================================
echo  Database Setup Complete!
echo ============================================
echo.
echo Database Details:
echo   Name: midgar37
echo   Host: localhost
echo   Port: 4306
echo   User: root
echo.
echo Tables Created:
echo   - user (with test admin/123456)
echo   - universe
echo   - oeuvre
echo   - personnage
echo   - challenge
echo   - artefact
echo   - participation
echo   - commentaire
echo.
echo Test Credentials:
echo   Username: admin
echo   Password: 123456
echo.
echo You can now run the JavaFX application!
echo.

pause



