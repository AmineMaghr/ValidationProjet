# 📌 IMPORTANT: Database Configuration Summary

## ⚠️ READ THIS FIRST

The Midgar JavaFX project has been **fully configured** to use MySQL database **midgar37** on **port 4306**.

**Configuration Status**: ✅ COMPLETE

---

## 🎯 What You Need to Do

### 1️⃣ Ensure MySQL Runs on Port 4306

**Windows:**
```powershell
# Start MySQL service
net start MySQL80

# Verify port 4306
netstat -ano | findstr :4306

# Or test connection
mysql -h localhost -P 4306 -u root -e "SELECT 1;"
```

**macOS:**
```bash
brew services start mysql
```

**Linux:**
```bash
sudo systemctl start mysql
```

### 2️⃣ Create the Database

**Choose your method:**

**Method A: Automated Batch (Easiest)**
```powershell
setup_database.bat
```

**Method B: PowerShell Script**
```powershell
.\setup_database.ps1
```

**Method C: Manual Command**
```powershell
mysql -h localhost -P 4306 -u root < init_database.sql
```

### 3️⃣ Run the Application

```powershell
mvn clean install
mvn clean javafx:run
```

### 4️⃣ Login

Use the test account:
- **Username**: `admin`
- **Password**: `123456`

---

## ⚙️ Current Configuration

### Database
| Property | Value |
|----------|-------|
| **Name** | midgar37 |
| **Host** | localhost |
| **Port** | **4306** ⭐ (Important!) |
| **User** | root |
| **Password** | (empty) |

### Connection String
```
jdbc:mysql://localhost:4306/midgar37
```

### Java Driver
```
com.mysql.cj.jdbc.Driver
```

### Maven Dependency
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

---

## 🔄 Configuration Files Modified

### 1. `pom.xml`
✅ Added MySQL connector dependency

### 2. `src/main/java/com/example/app/utils/MyDatabase.java`
✅ Updated to use MySQL instead of H2
✅ Set port to 4306
✅ Set database to midgar37

---

## 📁 New Files Created

| File | Purpose |
|------|---------|
| `init_database.sql` | SQL initialization script (8 tables) |
| `setup_database.bat` | Automated Windows setup script |
| `setup_database.ps1` | PowerShell setup script |
| `DATABASE_SETUP.md` | Complete setup documentation |
| `DATABASE_CREATION_SUMMARY.md` | Setup summary |
| `DATABASE_VERIFICATION.md` | Verification checklist |
| `QUICK_DB_SETUP.md` | Quick start guide |
| `DATABASE_CONFIG_IMPORTANT.md` | **This file** |

---

## 🚨 Critical Notes

### ⭐ Port 4306 is Essential
Your MySQL **MUST** be configured to listen on port **4306**, not the default 3306!

**Why?** The application is specifically configured for port 4306.

**To Change MySQL Port:**

Edit MySQL configuration file:
- Windows: `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`
- macOS: `/usr/local/etc/my.cnf`
- Linux: `/etc/mysql/mysql.conf.d/mysqld.cnf`

Find or add:
```ini
[mysqld]
port=4306
```

Then restart MySQL.

### ✅ Your MySQL Configuration

If you've already installed MySQL on port 4306:
```bash
mysql -h localhost -P 4306 -u root
```

If you installed MySQL on default port 3306, you have 2 options:

**Option A: Change MySQL to use port 4306** (Recommended)
1. Edit MySQL config (see above)
2. Restart MySQL
3. Create database with `setup_database.bat`

**Option B: Change Java configuration to port 3306**
1. Edit `MyDatabase.java`
2. Change line 8: `jdbc:mysql://localhost:3306/midgar37`
3. Recompile: `mvn clean install`

---

## 🔐 Default Credentials

### Database User
- **Username**: root
- **Password**: (empty)

### Test Application
- **Username**: admin
- **Password**: 123456

**Change these for production!**

---

## 📊 Database Schema

### 8 Tables Created

1. **user** (3 test records)
   - admin / 123456 (admin role)
   - user / password
   - testuser / test123

2. **universe** (3 test universes)
   - Fantasy Realm
   - Sci-Fi Galaxy
   - Medieval Kingdom

3. **oeuvre** (3 works)
4. **personnage** (3 characters)
5. **challenge** (3 challenges)
6. **artefact** (5 shop items)
7. **participation** (tracks user challenges)
8. **commentaire** (user comments)

---

## ✅ Pre-Launch Checklist

Before running the application:

- [ ] MySQL installed
- [ ] MySQL service running
- [ ] Port 4306 configured and listening
- [ ] Can connect: `mysql -h localhost -P 4306 -u root -e "SELECT 1;"`
- [ ] `init_database.sql` file exists
- [ ] Run `setup_database.bat` (or manual equivalent)
- [ ] Database created: `mysql -h localhost -P 4306 -u root -e "SHOW DATABASES LIKE 'midgar37';"`
- [ ] Tables created: `mysql -h localhost -P 4306 -u root midgar37 -e "SHOW TABLES;"`
- [ ] Test data loaded: `mysql -h localhost -P 4306 -u root midgar37 -e "SELECT COUNT(*) FROM user;"`
- [ ] Maven build successful: `mvn clean install`
- [ ] Application starts: `mvn clean javafx:run`
- [ ] Can login: admin / 123456

---

## 🆘 Quick Troubleshooting

### "Cannot connect to MySQL on port 4306"
```powershell
# 1. Check if MySQL is running
net start MySQL80

# 2. Check port 4306 is listening
netstat -ano | findstr :4306

# 3. Test connection
mysql -h localhost -P 4306 -u root -e "SELECT 1;"
```

### "Access denied for user 'root'"
```powershell
# Edit MyDatabase.java and set correct password
# OR
# Reset MySQL root password
```

### "Database already exists"
This is OK! The script won't recreate it. Or drop it first:
```powershell
mysql -h localhost -P 4306 -u root -e "DROP DATABASE IF EXISTS midgar37;"
```

### "Driver not found"
```powershell
mvn clean install
```

---

## 📖 Additional Documentation

For more detailed information, see:

1. **QUICK_DB_SETUP.md** - 3-minute setup guide
2. **DATABASE_SETUP.md** - Complete documentation
3. **DATABASE_VERIFICATION.md** - Verification checklist
4. **DATABASE_CREATION_SUMMARY.md** - Setup summary
5. **GUIDE_COMPLET.md** - Application guide
6. **TECHNICAL_SUMMARY.md** - Technical details

---

## 🎯 Next Steps

1. **Setup MySQL**: Ensure port 4306 is configured
2. **Create Database**: Run `setup_database.bat`
3. **Build Project**: Run `mvn clean install`
4. **Start Application**: Run `mvn clean javafx:run`
5. **Login**: Use admin / 123456
6. **Enjoy!** 🎉

---

## 📞 Support

If you need help:

1. Check **QUICK_DB_SETUP.md** for quick solutions
2. Check **DATABASE_SETUP.md** for detailed troubleshooting
3. Review console error messages
4. Check MySQL logs: `C:\ProgramData\MySQL\MySQL Server 8.0\Data\*.err`

---

## ✨ Summary

✅ MySQL configuration: **midgar37** on **port 4306**
✅ Database schema: **8 tables** with relationships
✅ Test data: **Pre-loaded** and ready to use
✅ Documentation: **Complete** with guides and troubleshooting
✅ Build system: **Maven** configured with dependencies

**Status**: 🟢 **READY TO USE**

---

**Configuration Date**: April 16, 2026
**MySQL Version**: 5.7+, 8.0+ supported
**Port**: 4306 (critical!)
**Status**: ✅ Complete

🚀 **Ready to launch! Run `setup_database.bat` then `mvn clean javafx:run`**

