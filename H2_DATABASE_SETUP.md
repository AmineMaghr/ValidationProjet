# 🗄️ Configuration H2 Database - Guide Complet

## 📋 Vue d'ensemble

Le projet a été configuré avec **H2 Database**, une base de données SQL embarquée en mémoire, idéale pour le développement et les tests.

## ✅ Configuration effectuée

### 1️⃣ Dépendance Maven
```xml
<!-- H2 Database for testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.1.214</version>
</dependency>
```

### 2️⃣ Classe MyDatabase.java
- **Emplacement** : `src/main/java/com/example/app/utils/MyDatabase.java`
- **Configuration** : Base de données H2 en mémoire
- **URL** : `jdbc:h2:mem:midgar;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- **Mode** : Compatible MySQL
- **Initialisation automatique** : Les tables se créent au démarrage

### 3️⃣ Tables créées automatiquement

#### 📌 Table USER
```sql
CREATE TABLE USER (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) DEFAULT 'user',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

#### 📌 Table DEFI
```sql
CREATE TABLE DEFI (
  id INT PRIMARY KEY AUTO_INCREMENT,
  titre VARCHAR(255) NOT NULL,
  description TEXT,
  theme VARCHAR(100),
  image_cover VARCHAR(500),
  date_debut DATE,
  date_fin DATE,
  date_limite DATE,
  statut VARCHAR(50) DEFAULT 'OUVERT',
  difficulte VARCHAR(50) DEFAULT 'FACILE',
  createur_id INT NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (createur_id) REFERENCES USER(id)
)
```

#### 📌 Table PARTICIPATION
```sql
CREATE TABLE PARTICIPATION (
  id INT PRIMARY KEY AUTO_INCREMENT,
  defi_id INT NOT NULL,
  user_id INT NOT NULL,
  date_participation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  statut VARCHAR(50) DEFAULT 'EN_COURS',
  FOREIGN KEY (defi_id) REFERENCES DEFI(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES USER(id) ON DELETE CASCADE
)
```

### 4️⃣ Données d'exemple insérées

**Utilisateurs :**
- `admin` (admin@midgar.com) - Rôle: admin
- `user1` (user1@midgar.com) - Rôle: user
- `user2` (user2@midgar.com) - Rôle: user

**Défis initiaux :**
- 🌍 Défi Éco-Responsable (Statut: OUVERT, Difficulté: FACILE)
- 💪 Défi Santé (Statut: OUVERT, Difficulté: MOYEN)
- 🎨 Défi Créatif (Statut: TERMINÉ, Difficulté: DIFFICILE)

## 🚀 Opérations CRUD

### Classe TestDB.java
**Emplacement** : `src/main/java/com/example/app/utils/TestDB.java`

Tests les opérations CRUD suivantes :

1. **CREATE (INSERT)** ✅
   - Création d'un nouveau défi
   - Test : "Défi Technologie"

2. **READ (SELECT)** ✅
   - Affichage de tous les utilisateurs
   - Affichage de tous les défis
   - Requête : `SELECT * FROM "DEFI"`

3. **UPDATE** ✅
   - Mise à jour du défi créé
   - Modification de la description
   - Requête : `UPDATE "DEFI" SET description = ...`

4. **DELETE** ✅
   - Suppression du défi créé
   - Requête : `DELETE FROM "DEFI" WHERE ...`

### Classe DefiDAO.java
**Emplacement** : `src/main/java/com/example/app/dao/DefiDAO.java`

Implémente l'interface IDAO<Defi> avec les méthodes :

**Créer :**
```java
void add(Defi defi) throws SQLException
```

**Lire :**
```java
List<Defi> select() throws SQLException
List<Defi> findOuverts() throws SQLException
List<Defi> findByTheme(String theme) throws SQLException
List<Defi> findActifs() throws SQLException
Defi findWithParticipations(int id) throws SQLException
```

**Mettre à jour :**
```java
void update(Defi defi) throws SQLException
int countByStatut(String statut) throws SQLException
```

**Supprimer :**
```java
void delete(int id) throws SQLException
```

**Rechercher :**
```java
List<Defi> searchDefis(String search, String sortBy) throws SQLException
```

## 🧪 Exécuter les tests

```bash
# Compilation et test complet CRUD
.\mvnw.cmd -q compile "exec:java" "-Dexec.mainClass=com.example.app.utils.TestDB"
```

**Résultat attendu :**
```
🔍 Test de connexion à H2 Database...

✅ Connecté à la base de données H2 en mémoire
✅ Schéma de base de données initialisé avec succès
✅ Données d'exemple insérées
✅ Connexion réussie !

📊 Utilisateurs :
  - admin (admin)
  - user1 (user)
  - user2 (user)

🏆 Défis :
  - Défi Éco-Responsable (OUVERT)
  - Défi Santé (OUVERT)
  - Défi Créatif (TERMINÉ)

➕ Création d'un nouveau défi...
✅ Défi créé avec succès !

✏️ Mise à jour du défi...
✅ Défi mis à jour !

🗑️ Suppression du défi...
✅ Défi supprimé !

📈 Total final des défis :
  Nombre de défis : 3

✅ Tous les tests CRUD sont passés avec succès !
```

## 🔑 Points importants

### Noms de tables avec guillemets
En H2, certains noms de tables (comme `USER`, `DEFI`) sont des mots clés réservés.
**Solution** : Utiliser des guillemets doubles

```java
// ✅ Correct
"SELECT * FROM \"DEFI\""
"INSERT INTO \"USER\" ..."

// ❌ Mauvais
"SELECT * FROM DEFI"
"INSERT INTO USER ..."
```

### Fonctions SQL
H2 utilise une syntaxe légèrement différente de MySQL :

```sql
-- ❌ MySQL
DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY)

-- ✅ H2
CURRENT_DATE + INTERVAL '30' DAY
```

### Timestamps
```sql
-- ❌ MySQL
NOW()

-- ✅ H2
CURRENT_TIMESTAMP
```

## 📦 Configuration Maven

Plugin exec-maven pour exécuter les tests :
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.6.3</version>
    <configuration>
        <mainClass>com.example.app.utils.TestDB</mainClass>
    </configuration>
</plugin>
```

## 💡 Avantages de H2

- ✅ **Embarquée** : Pas d'installation d'un serveur de base de données
- ✅ **En mémoire** : Performances rapides
- ✅ **Compatible SQL** : Support MySQL et autres dialectes
- ✅ **Petite taille** : Moins de 2 MB
- ✅ **Idéale pour le développement** : Données réinitialisées à chaque démarrage
- ✅ **Aucune configuration** : Juste l'URL JDBC

## 🔗 Ressources

- [H2 Database](https://www.h2database.com/)
- [Documentation H2](https://h2database.com/javadoc/index.html)
- [Comparaison syntaxe SQL](https://www.h2database.com/html/grammar.html)

---

**Status** : ✅ Configuration complète et testée
**Date** : 2026-04-16
**Version** : H2 2.1.214

