#!/usr/bin/env powershell
# ============================================
# Midgar JavaFX - MySQL Database Setup Script
# PowerShell Version (Windows)
# ============================================

param(
    [string]$MySQLPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin",
    [string]$Host = "localhost",
    [int]$Port = 4306,
    [string]$User = "root",
    [string]$Password = "",
    [string]$Database = "midgar37"
)

# ============================================
# Configuration
# ============================================

$ErrorActionPreference = "Continue"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$sqlFile = Join-Path $scriptDir "init_database.sql"

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Midgar JavaFX - Database Setup" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# ============================================
# Step 1: Check MySQL Installation
# ============================================

Write-Host "[1/4] Checking MySQL installation..." -ForegroundColor Yellow

if (-Not (Test-Path "$MySQLPath\mysql.exe")) {
    Write-Host "❌ ERROR: MySQL not found at $MySQLPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Possible solutions:" -ForegroundColor Yellow
    Write-Host "1. Install MySQL Community Server"
    Write-Host "   Download: https://dev.mysql.com/downloads/mysql/"
    Write-Host "2. Update `$MySQLPath parameter:"
    Write-Host "   .\setup_database.ps1 -MySQLPath 'C:\path\to\MySQL\bin'"
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "✅ MySQL found at $MySQLPath" -ForegroundColor Green

# ============================================
# Step 2: Test MySQL Connection
# ============================================

Write-Host ""
Write-Host "[2/4] Testing MySQL connection on port $Port..." -ForegroundColor Yellow

$connectionTest = & "$MySQLPath\mysql.exe" -h $Host -P $Port -u $User -N -e "SELECT 1;" 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR: Cannot connect to MySQL on port $Port" -ForegroundColor Red
    Write-Host ""
    Write-Host "Possible solutions:" -ForegroundColor Yellow
    Write-Host "1. Start MySQL service:"
    Write-Host "   net start MySQL80"
    Write-Host "2. Check if MySQL is configured for port $Port"
    Write-Host "   Edit: C:\ProgramData\MySQL\MySQL Server 8.0\my.ini"
    Write-Host "   Look for: port=$Port"
    Write-Host "3. Check if port $Port is in use:"
    Write-Host "   netstat -ano | findstr :$Port"
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "✅ MySQL connection successful" -ForegroundColor Green

# ============================================
# Step 3: Verify SQL File
# ============================================

Write-Host ""
Write-Host "[3/4] Checking SQL initialization file..." -ForegroundColor Yellow

if (-Not (Test-Path $sqlFile)) {
    Write-Host "❌ ERROR: $sqlFile not found" -ForegroundColor Red
    Write-Host ""
    Write-Host "Make sure init_database.sql is in: $scriptDir" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "✅ SQL file found: $sqlFile" -ForegroundColor Green

# ============================================
# Step 4: Create Database
# ============================================

Write-Host ""
Write-Host "[4/4] Creating database and tables..." -ForegroundColor Yellow

try {
    $output = & "$MySQLPath\mysql.exe" -h $Host -P $Port -u $User -N (Get-Content $sqlFile) 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Database created successfully" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Database creation completed with warnings" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Output:" -ForegroundColor Yellow
        Write-Host $output
    }
} catch {
    Write-Host "❌ ERROR: Failed to execute SQL script" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

# ============================================
# Verification
# ============================================

Write-Host ""
Write-Host "Verifying database setup..." -ForegroundColor Yellow

$tableCount = & "$MySQLPath\mysql.exe" -h $Host -P $Port -u $User -D $Database -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$Database';" 2>&1

if ($tableCount -match "^\d+$") {
    [int]$count = $tableCount
    Write-Host "✅ Database has $count tables" -ForegroundColor Green
} else {
    Write-Host "⚠️  Could not verify table count" -ForegroundColor Yellow
}

# ============================================
# Success Summary
# ============================================

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "  Database Setup Complete!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""

Write-Host "Database Configuration:" -ForegroundColor Cyan
Write-Host "  Name: $Database"
Write-Host "  Host: $Host"
Write-Host "  Port: $Port"
Write-Host "  User: $User"
Write-Host ""

Write-Host "Test Credentials:" -ForegroundColor Cyan
Write-Host "  Username: admin"
Write-Host "  Password: 123456"
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Run Maven: mvn clean install"
Write-Host "  2. Start app:  mvn clean javafx:run"
Write-Host "  3. Login with: admin / 123456"
Write-Host ""

Write-Host "Documentation:" -ForegroundColor Cyan
Write-Host "  - DATABASE_SETUP.md"
Write-Host "  - GUIDE_COMPLET.md"
Write-Host "  - TECHNICAL_SUMMARY.md"
Write-Host ""

Read-Host "Press Enter to exit"


