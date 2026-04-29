# ✅ Database Setup Verification Checklist

## Pre-Setup Requirements

- [ ] MySQL Server installed (version 5.7+)
- [ ] MySQL service can be started
- [ ] Port 4306 is available (not blocked by firewall)
- [ ] Have administrator access to MySQL
- [ ] Project files are in: `C:\Users\feral\Downloads\ValidationProjet-interface1\ValidationProjet-interface1\`

---

## Setup Verification

### ✅ File Checks

- [ ] `init_database.sql` exists in project root (165+ lines)
- [ ] `setup_database.bat` exists in project root
- [ ] `setup_database.ps1` exists in project root
- [ ] `MyDatabase.java` contains MySQL configuration
- [ ] `pom.xml` contains MySQL connector dependency
- [ ] `DATABASE_SETUP.md` exists
- [ ] `DATABASE_CREATION_SUMMARY.md` exists
- [ ] `QUICK_DB_SETUP.md` exists

### ✅ Configuration Checks

**File**: `src/main/java/com/example/app/utils/MyDatabase.java`
- [ ] URL: `jdbc:mysql://localhost:4306/midgar37`
- [ ] USER: `root`
- [ ] PASSWORD: (empty or your password)
- [ ] Driver: `com.mysql.cj.jdbc.Driver`

**File**: `pom.xml`
- [ ] Has MySQL dependency: `mysql-connector-java:8.0.33`

**File**: `init_database.sql`
- [ ] Creates database: `midgar37`
- [ ] Creates table: `user`
- [ ] Creates table: `universe`
- [ ] Creates table: `oeuvre`
- [ ] Creates table: `personnage`
- [ ] Creates table: `challenge`
- [ ] Creates table: `artefact`
- [ ] Creates table: `participation`
- [ ] Creates table: `commentaire`
- [ ] Inserts test user: `admin / 123456`

---

## Pre-Database Creation

- [ ] MySQL is installed
- [ ] MySQL service is running or can be started
- [ ] Can access MySQL command: `mysql --version`
- [ ] Port 4306 is listening

**Commands to verify:**
```powershell
# Check MySQL
mysql --version

# Check if port 4306 is open
netstat -ano | findstr :4306

# Try to connect
mysql -h localhost -P 4306 -u root -e "SELECT 1;"
```

---

## Database Creation Verification

### Using Batch Script
```powershell
cd C:\Users\feral\Downloads\ValidationProjet-interface1\ValidationProjet-interface1
setup_database.bat
```

Check for messages:
- ✅ "MySQL found"
- ✅ "MySQL connection successful"
- ✅ "Database created successfully"

### Using PowerShell Script
```powershell
cd C:\Users\feral\Downloads\ValidationProjet-interface1\ValidationProjet-interface1
.\setup_database.ps1
```

Check for messages:
- ✅ "MySQL found at..."
- ✅ "MySQL connection successful"
- ✅ "Database created successfully"

### Using Manual Command
```powershell
cd C:\Users\feral\Downloads\ValidationProjet-interface1\ValidationProjet-interface1
mysql -h localhost -P 4306 -u root < init_database.sql
```

---

## Post-Database Creation Verification

### Verify Database Exists
```powershell
mysql -h localhost -P 4306 -u root -e "SHOW DATABASES LIKE 'midgar37';"
```

Expected output:
```
Database (midgar37)
midgar37
```

### Verify Tables Exist
```powershell
mysql -h localhost -P 4306 -u root midgar37 -e "SHOW TABLES;"
```

Expected tables (8):
- [x] user
- [x] universe
- [x] oeuvre
- [x] personnage
- [x] challenge
- [x] artefact
- [x] participation
- [x] commentaire

### Verify Test User
```powershell
mysql -h localhost -P 4306 -u root midgar37 -e "SELECT * FROM user WHERE username='admin';"
```

Expected output:
```
id  username  email                password  role
1   admin     admin@midgar.com     123456    admin
```

### Verify Test Data
```powershell
# Count users
mysql -h localhost -P 4306 -u root midgar37 -e "SELECT COUNT(*) FROM user;"
# Expected: 3

# Count universes
mysql -h localhost -P 4306 -u root midgar37 -e "SELECT COUNT(*) FROM universe;"
# Expected: 3

# Count challenges
mysql -h localhost -P 4306 -u root midgar37 -e "SELECT COUNT(*) FROM challenge;"
# Expected: 3

# Count artefacts
mysql -h localhost -P 4306 -u root midgar37 -e "SELECT COUNT(*) FROM artefact;"
# Expected: 5
```

---

## Build Verification

### Download Dependencies
```powershell
cd C:\Users\feral\Downloads\ValidationProjet-interface1\ValidationProjet-interface1
mvn clean install
```

Verify:
- [ ] Build succeeds (no ERROR messages)
- [ ] MySQL connector downloaded
- [ ] JavaFX dependencies downloaded
- [ ] `target/` folder created

### Verify JAR Creation
```powershell
# Check if compiled classes exist
dir target/classes/com/example/app/utils/MyDatabase.class
```

---

## Application Runtime Verification

### Test Database Connection
```powershell
# Compile and test connection
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.app.utils.MyDatabase"
```

Expected output:
```
✅ Connexion à la base 'midgar37' (MySQL:4306) réussie !
```

### Run Application
```powershell
mvn clean javafx:run
```

Verify:
- [ ] Application starts
- [ ] Login page displays
- [ ] Can login with: admin / 123456
- [ ] Home page loads
- [ ] Can navigate to other pages
- [ ] Data loads from database

---

## Troubleshooting Checklist

### MySQL Connection Issues
- [ ] MySQL service is running: `net start MySQL80`
- [ ] Port 4306 is correct: Check `my.ini`
- [ ] Firewall allows port 4306
- [ ] Can connect manually: `mysql -h localhost -P 4306 -u root`

### Database Creation Issues
- [ ] `init_database.sql` file exists and not empty
- [ ] No existing `midgar37` database conflicts
- [ ] Root user has CREATE privilege
- [ ] No syntax errors in SQL file

### Driver Issues
- [ ] Maven executed successfully: `mvn clean install`
- [ ] MySQL connector in pom.xml
- [ ] Correct driver class: `com.mysql.cj.jdbc.Driver`
- [ ] No version conflicts with other drivers

### Connection String Issues
- [ ] URL correct: `jdbc:mysql://localhost:4306/midgar37`
- [ ] No typos in database name
- [ ] Port number is 4306, not 3306
- [ ] Username and password match MySQL config

---

## Final Verification Summary

After completing all checks, you should have:

✅ MySQL running on port 4306
✅ Database `midgar37` created
✅ 8 tables with proper schema
✅ Test data loaded (3 users, 3 universes, etc.)
✅ Maven dependencies downloaded
✅ Application compiles successfully
✅ Application connects to database
✅ Can login with admin/123456

---

## Status: Ready to Deploy

When all checkboxes are marked, your Midgar JavaFX database is ready for:

1. **Development**: Modify and test new features
2. **Testing**: Run integration tests
3. **Deployment**: Package and distribute the application

---

## Quick Recovery Procedure

If something breaks:

```powershell
# 1. Stop the application
# (Press Ctrl+C in terminal)

# 2. Drop the database
mysql -h localhost -P 4306 -u root -e "DROP DATABASE IF EXISTS midgar37;"

# 3. Recreate from scratch
setup_database.bat

# 4. Verify
mysql -h localhost -P 4306 -u root midgar37 -e "SHOW TABLES;"

# 5. Rebuild and run
mvn clean install
mvn clean javafx:run
```

---

**Created**: April 16, 2026  
**Last Updated**: April 16, 2026  
**Status**: ✅ Complete and Ready

