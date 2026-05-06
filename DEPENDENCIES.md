# 📦 Dépendances Maven - Midgar JavaFX

## Résumé des dépendances ajoutées

Le fichier `pom.xml` a été mis à jour avec **23 dépendances** complètes pour un projet JavaFX production-ready.

---

## 🎨 JavaFX (5 dépendances)

| Dépendance | Version | Rôle |
|---|---|---|
| `javafx-controls` | 17.0.2 | Contrôles UI (Button, TextField, etc.) |
| `javafx-fxml` | 17.0.2 | Support FXML pour les vues |
| `javafx-graphics` | 17.0.2 | Rendu graphique |
| `javafx-media` | 17.0.2 | Support audio/vidéo |
| `javafx-web` | 17.0.2 | Support WebView |

**Installation**: Automatique avec Maven

---

## 🗄️ Base de données (1 dépendance)

| Dépendance | Version | Rôle |
|---|---|---|
| `mysql-connector-java` | 8.0.33 | Driver MySQL JDBC |

**Usage**: Connexion à MySQL depuis Java

```java
import java.sql.Connection;
import java.sql.DriverManager;

Connection conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/t", "root", ""
);
```

---

## 📝 Logging (3 dépendances)

| Dépendance | Version | Rôle |
|---|---|---|
| `slf4j-api` | 1.7.36 | Façade de logging |
| `logback-classic` | 1.2.11 | Implémentation logging |
| `logback-core` | 1.2.11 | Cœur logging |

**Usage**: Remplace `System.out.println()`

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

Logger logger = LoggerFactory.getLogger(MyClass.class);
logger.info("Message d'information");
logger.error("Message d'erreur", exception);
```

**Configuration**: Créer `logback.xml` dans `src/main/resources/`

---

## ✅ Validation (2 dépendances)

| Dépendance | Version | Rôle |
|---|---|---|
| `validation-api` | 2.0.1.Final | API de validation |
| `hibernate-validator` | 6.2.5.Final | Implémentation validation |

**Usage**: Valider les données

```java
import javax.validation.constraints.*;

public class User {
    @NotBlank
    private String username;
    
    @Email
    private String email;
}
```

---

## 📄 JSON Processing (1 dépendance)

| Dépendance | Version | Rôle |
|---|---|---|
| `gson` | 2.10.1 | Sérialisation JSON |

**Usage**: Convertir Java ↔ JSON

```java
import com.google.gson.Gson;

Gson gson = new Gson();
String json = gson.toJson(user);
User user = gson.fromJson(jsonString, User.class);
```

---

## 🛠️ Apache Commons (2 dépendances)

| Dépendance | Version | Rôle |
|---|---|---|
| `commons-lang3` | 3.12.0 | Utilitaires pour String, Array, etc. |
| `commons-io` | 2.11.0 | Utilitaires pour I/O |

**Usage**: Fonctions utilitaires

```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.IOUtils;

String result = StringUtils.capitalize("hello"); // "Hello"
```

---

## 🧪 Testing (5 dépendances)

### JUnit

| Dépendance | Version | Rôle | Scope |
|---|---|---|---|
| `junit` | 4.13.2 | Framework de test classique | test |
| `junit-jupiter-api` | 5.9.2 | JUnit 5 API | test |
| `junit-jupiter-engine` | 5.9.2 | JUnit 5 moteur | test |

**Usage**: Écrire des tests unitaires

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    public void testUserCreation() {
        User user = new User("admin", "admin@test.com", "password", "admin");
        assertEquals("admin", user.getUsername());
    }
}
```

### TestFX

| Dépendance | Version | Rôle | Scope |
|---|---|---|---|
| `testfx-core` | 4.0.16-alpha | Framework test JavaFX | test |
| `testfx-junit` | 4.0.16-alpha | Intégration TestFX + JUnit | test |

**Usage**: Tester les interfaces JavaFX

```java
@ExtendWith(ApplicationExtension.class)
public class LoginControllerTest {
    @Test
    public void testLoginButton(FXRobot robot) {
        robot.clickOn(".login-button");
        // Vérifier le résultat
    }
}
```

---

## 🔨 Plugins Maven (8 plugins)

### 1. **javafx-maven-plugin** (0.0.8)
Permet de lancer l'application JavaFX

```bash
mvn javafx:run
```

### 2. **maven-compiler-plugin** (3.10.1)
Compile le code Java

```bash
mvn compile
```

**Configuration**:
- Source: Java 17
- Target: Java 17
- Encoding: UTF-8

### 3. **maven-shade-plugin** (3.4.1)
Crée un JAR ultra-portable avec toutes les dépendances

```bash
mvn clean package shade:shade
```

### 4. **maven-surefire-plugin** (2.22.2)
Lance les tests unitaires

```bash
mvn test
```

**Patterns détectés**:
- `*Test.java`
- `*Tests.java`

### 5. **maven-jar-plugin** (3.2.2)
Crée le fichier JAR exécutable

```bash
mvn jar:jar
```

### 6. **maven-assembly-plugin** (3.4.2)
Crée un JAR avec toutes les dépendances

```bash
mvn assembly:single
```

**Résultat**: `tprojetpi-1.0-SNAPSHOT-jar-with-dependencies.jar`

### 7. **maven-resources-plugin** (3.2.0)
Copie les ressources (FXML, CSS, images)

**Configuration**: UTF-8 encoding

### 8. **maven-clean-plugin** (3.2.0)
Nettoie les fichiers compilés

```bash
mvn clean
```

---

## 📋 Commandes Maven courantes

```bash
# Nettoyer et compiler
mvn clean install

# Lancer l'application
mvn javafx:run

# Compiler sans lancer
mvn compile

# Lancer les tests
mvn test

# Créer les JARs
mvn package

# Nettoyer
mvn clean

# Vérifier les dépendances
mvn dependency:tree

# Mettre à jour les dépendances
mvn versions:display-updates
```

---

## 📊 Tableau complet des dépendances

```
Dépendances: 23 total

JavaFX:          5 dépendances ✅
Database:        1 dépendance  ✅
Logging:         3 dépendances ✅
Validation:      2 dépendances ✅
JSON:            1 dépendance  ✅
Commons:         2 dépendances ✅
Testing:         5 dépendances ✅ (scope: test)
Plugins:         8 plugins     ✅
```

---

## 🚀 Premier lancement après les mises à jour

1. **Synchroniser Maven**:
```bash
mvn clean install
```

2. **Lancer l'application**:
```bash
mvn javafx:run
```

3. **Si erreur de dépendance**:
```bash
mvn dependency:purge-local-repository clean install
```

---

## 📦 Créer un fichier JAR exécutable

Pour distribuer l'application sans Maven:

```bash
mvn clean package
```

Cela créera:
- `target/tprojetpi-1.0-SNAPSHOT.jar` - JAR simple
- `target/tprojetpi-1.0-SNAPSHOT-jar-with-dependencies.jar` - JAR avec toutes les dépendances (recommandé)

**Lancer le JAR**:
```bash
java -jar target/tprojetpi-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 🔍 Vérifier les dépendances

**Afficher l'arborescence**:
```bash
mvn dependency:tree
```

**Vérifier les versions disponibles**:
```bash
mvn versions:display-updates
```

**Nettoyer le cache local**:
```bash
mvn dependency:purge-local-repository
```

---

## ⚠️ Dépannage des dépendances

### "Impossible de trouver une artifact"
Solution:
```bash
mvn clean install -U
```
Le `-U` force la mise à jour des dépendances

### "Version non trouvée"
Vérifier dans le pom.xml la version exacte, ou:
```bash
mvn dependency:purge-local-repository clean install
```

### "Conflit de versions"
Ajouter une exclusion dans pom.xml:
```xml
<dependency>
    <groupId>...</groupId>
    <artifactId>...</artifactId>
    <exclusions>
        <exclusion>
            <groupId>conflicting.group</groupId>
            <artifactId>conflicting.artifact</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

---

## 💡 Bonnes pratiques

1. **Utiliser des versions stables** (pas SNAPSHOT)
2. **Limiter le nombre de dépendances**
3. **Mettre à jour régulièrement** `mvn versions:display-updates`
4. **Vérifier les sécurités** (CVE)
5. **Documenter les dépendances** (ce que vous venez de faire!)

---

## 🔗 Ressources

- [Maven Central Repository](https://mvnrepository.com/)
- [JavaFX Documentation](https://openjfx.io/)
- [SLF4J Documentation](https://www.slf4j.org/)
- [JUnit 5 Documentation](https://junit.org/junit5/)

---

**Documentation des dépendances - Midgar JavaFX**  
Version 1.0 | 2026-04-15  
23 dépendances installées ✅

