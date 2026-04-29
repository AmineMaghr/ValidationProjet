    # 🚀 Quick Database Setup Guide

## In 3 Steps

### Step 1: Ensure MySQL is Running
```powershell
# Windows - Check if running
Get-Service | Select-String MySQL

# Windows - Start MySQL (admin required)
net start MySQL80

# macOS
brew services start mysql

# Linux
sudo systemctl start mysql
```

### Step 2: Create the Database
**Choose ONE of the following:**

**Option A: Automated Batch Script (Windows)**
```powershell
setup_database.bat
```

**Option B: PowerShell Script (Windows, more advanced)**
```powershell
.\setup_database.ps1
```

**Option C: Manual Command**
```powershell
mysql -h localhost -P 4306 -u root < init_database.sql
```

### Step 3: Run the Application
```powershell
mvn clean install
mvn clean javafx:run
```

---

## ✅ Test It Works

Login with:
- **Username**: `admin`
- **Password**: `123456`

---

## Database Details

| Property | Value |
|----------|-------|
| Name | midgar37 |
| Host | localhost |
| Port | 4306 |
| User | root |
| Password | (empty) |

---

## ❌ If Something Goes Wrong

### Error: "Cannot connect to MySQL"
1. Start MySQL: `net start MySQL80`
2. Check port 4306: `netstat -ano | findstr :4306`
3. Verify in MySQL config: `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`

### Error: "Database already exists"
✅ This is OK! Just continue.

### Error: "Access denied for user 'root'"
Edit: `src/main/java/com/example/app/utils/MyDatabase.java`
```java
private static final String PASSWORD = "your_password";
```

### Error: "Driver not found"
Run: `mvn clean install`

---

## 📁 What Gets Created

**Tables** (8 total):
- user (3 test accounts)
- universe (3 universes)
- oeuvre (3 works)
- personnage (3 characters)
- challenge (3 challenges)
- artefact (5 shop items)
- participation (empty)
- commentaire (empty)

---

## 📖 More Info

See: `DATABASE_SETUP.md` for complete documentation

---

**Need help?** Check `DATABASE_SETUP.md` troubleshooting section

