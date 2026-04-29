# 🗄️ Midgar JavaFX - Database Setup Guide

## Database Configuration

The application uses **MySQL** database on port **4306** with database name **midgar37**.

### Database Details
- **Database Name**: `midgar37`
- **Host**: `localhost`
- **Port**: `4306`
- **User**: `root`
- **Password**: (empty by default)

---

## ⚡ Quick Setup (Automatic)

### Option 1: Use the Setup Script

Simply run the batch file from the project root:

```bash
setup_database.bat
```

This script will:
1. Check if MySQL is installed
2. Verify connection to port 4306
3. Create the database
4. Create all tables
5. Insert test data

---

## 🔧 Manual Setup

If the automated script doesn't work, follow these steps:

### Step 1: Ensure MySQL is Running

#### On Windows:
```powershell
# Check if MySQL service is running
Get-Service | Select-String MySQL

# If not running, start it (may require admin rights)
net start MySQL80
```

#### On macOS:
```bash
brew services start mysql
```

#### On Linux:
```bash
sudo systemctl start mysql
```

### Step 2: Connect to MySQL

Open your terminal and connect to MySQL on port 4306:

```bash
mysql -h localhost -P 4306 -u root
```

### Step 3: Execute the SQL Script

Either:

**Option A: From command line (recommended)**
```bash
mysql -h localhost -P 4306 -u root < init_database.sql
```

**Option B: From MySQL CLI**
```mysql
source init_database.sql;
```

### Step 4: Verify Database Creation

```mysql
-- Check if database exists
SHOW DATABASES;

-- Check if tables exist
USE midgar37;
SHOW TABLES;

-- Verify test user
SELECT * FROM user WHERE username = 'admin';
```

---

## 🧪 Test the Database

### Login Credentials

After setup, you can log in with:

```
Username: admin
Password: 123456
```

Other test credentials:
- `user` / `password`
- `testuser` / `test123`

### Sample Data

The script automatically inserts:

**Users (3)**
- admin (admin role)
- user (user role)
- testuser (user role)

**Universes (3)**
- Fantasy Realm
- Sci-Fi Galaxy
- Medieval Kingdom

**Oeuvres (3)**
- The Last Dragon
- Space Odyssey
- Castle Chronicles

**Personnages (3)**
- Aragorn
- Captain Nova
- King Arthur

**Challenges (3)**
- Dragon Slayer (50 points)
- Alien Contact (30 points)
- Quest Master (75 points)

**Artefacts (5)**
- Excalibur (999.99)
- Mithril Armor (749.99)
- Magic Wand (199.99)
- Invisibility Cloak (599.99)
- Shield of Protection (449.99)

---

## ❓ Troubleshooting

### Error: "Cannot connect to MySQL on port 4306"

**Cause**: MySQL is not running or not listening on port 4306

**Solution**:
1. Start MySQL service
2. Check port configuration in MySQL config file:
   - Windows: `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`
   - macOS: `/usr/local/etc/my.cnf`
   - Linux: `/etc/mysql/mysql.conf.d/mysqld.cnf`

Look for:
```ini
[mysqld]
port=4306
```

### Error: "Access denied for user 'root'@'localhost'"

**Cause**: MySQL root user has a password set

**Solution**:
1. Update `MyDatabase.java`:
```java
private static final String PASSWORD = "your_root_password";
```

2. Or reset the root password using MySQL commands

### Error: "Driver MySQL not found"

**Cause**: MySQL connector dependency is missing

**Solution**:
1. Run `mvn clean install` to download dependencies
2. Check pom.xml has MySQL connector dependency

### Database Already Exists

If you run the script multiple times and get "database already exists" error:

**Option A**: Drop and recreate
```bash
mysql -h localhost -P 4306 -u root -e "DROP DATABASE IF EXISTS midgar37;"
mysql -h localhost -P 4306 -u root < init_database.sql
```

**Option B**: Just skip the database creation
The script uses `CREATE DATABASE IF NOT EXISTS`, so duplicates are safe.

---

## 📋 Database Schema

### Tables

```
user (8 fields)
├── id (PRIMARY KEY)
├── username (UNIQUE)
├── email (UNIQUE)
├── password
├── role
├── created_at
├── updated_at

universe (9 fields)
├── id (PRIMARY KEY)
├── name
├── short_description
├── description
├── themes
├── image_url
├── creator_id (FK → user.id)
├── created_at
├── updated_at

oeuvre (6 fields)
├── id (PRIMARY KEY)
├── title
├── description
├── image_url
├── universe_id (FK → universe.id)
├── created_at
├── updated_at

personnage (6 fields)
├── id (PRIMARY KEY)
├── name
├── description
├── image_url
├── universe_id (FK → universe.id)
├── created_at
├── updated_at

challenge (6 fields)
├── id (PRIMARY KEY)
├── title
├── description
├── difficulty
├── points
├── created_at
├── updated_at

artefact (7 fields)
├── id (PRIMARY KEY)
├── name
├── description
├── price
├── image_url
├── type
├── created_at
├── updated_at

participation (7 fields)
├── id (PRIMARY KEY)
├── user_id (FK → user.id)
├── challenge_id (FK → challenge.id)
├── status
├── score
├── completed_at
├── created_at
├── updated_at

commentaire (6 fields)
├── id (PRIMARY KEY)
├── content
├── user_id (FK → user.id)
├── universe_id (FK → universe.id)
├── created_at
├── updated_at
```

---

## 🔄 Connection Flow

```
JavaFX App (MainApp.java)
    ↓
SceneManager (Navigation)
    ↓
Controllers (LoginController, HomeController, etc.)
    ↓
Services (UserService, UniverseService, etc.)
    ↓
MyDatabase.getConnection() ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ←
    ↓
MySQL JDBC Driver
    ↓
MySQL Server (localhost:4306)
    ↓
midgar37 Database
```

---

## 📖 Configuration Files

### MyDatabase.java
Location: `src/main/java/com/example/app/utils/MyDatabase.java`

Update connection details here:
```java
private static final String URL = "jdbc:mysql://localhost:4306/midgar37";
private static final String USER = "root";
private static final String PASSWORD = "";
```

### pom.xml
Location: `pom.xml`

MySQL connector dependency:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### init_database.sql
Location: `init_database.sql`

SQL script with:
- Database creation
- Table creation
- Test data insertion

---

## ✅ Next Steps

After database setup:

1. **Verify Setup**:
   ```bash
   mysql -h localhost -P 4306 -u root midgar37 -e "SELECT COUNT(*) FROM user;"
   ```

2. **Run the Application**:
   ```bash
   mvn clean javafx:run
   ```

3. **Login with Test Account**:
   - Username: `admin`
   - Password: `123456`

4. **Explore the Features**:
   - Home page
   - Universes
   - Oeuvres
   - Personnages
   - Challenges
   - Shop
   - Admin Dashboard

---

## 🆘 Need Help?

1. Check the **Troubleshooting** section above
2. Verify MySQL is running: `mysql -h localhost -P 4306 -u root -e "SELECT 1"`
3. Check error messages in console
4. Review `GUIDE_COMPLET.md` for more details
5. Check logs for connection errors

---

**Last Updated**: April 16, 2026  
**Database Version**: 1.0  
**Compatibility**: MySQL 5.7+, MariaDB 10.3+

