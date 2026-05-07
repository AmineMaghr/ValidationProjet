# 🚨 DÉPANNAGE - Erreur JavaFX "composants d'exécution manquants"

## ❌ **Erreur rencontrée**
```
Erreur : des composants d'exécution JavaFX obligatoires pour exécuter cette application sont manquants.
```

Cette erreur signifie que JavaFX n'est pas correctement configuré ou accessible.

---

## 🔍 **Diagnostic rapide**

### **Vérifiez votre environnement :**

1. **Version Java** :
   ```bash
   java -version
   ```
   - Doit être Java 11+ (vous avez Java 23 ✅)

2. **JavaFX inclus dans le JDK** :
   ```bash
   java --list-modules | findstr javafx
   ```
   - Si rien n'apparaît → JavaFX n'est pas inclus

3. **Maven installé** :
   ```bash
   mvn -version
   ```
   - Si pas installé → Installez Maven

---

## ✅ **Solutions par ordre de priorité**

### **Solution 1: Utiliser Maven (RECOMMANDÉ)**

1. **Installer Maven** si nécessaire :
   - Téléchargez depuis : https://maven.apache.org/download.cgi
   - Ajoutez au PATH système

2. **Lancer l'application** :
   ```bash
   cd C:\Users\Amine Maghrebi\IdeaProjects\bank-account-app\tprojetpi
   mvn clean install
   mvn javafx:run
   ```

3. **Si ça ne marche pas** :
   ```bash
   mvn clean install -U  # Force la mise à jour des dépendances
   ```

### **Solution 2: Utiliser un JDK avec JavaFX inclus**

1. **Téléchargez Liberica JDK Full** :
   - Site : https://bell-sw.com/pages/downloads/
   - Choisissez "Full JDK" (inclut JavaFX)

2. **Installez et configurez** :
   - Remplacez votre JDK actuel
   - Ou configurez votre IDE pour l'utiliser

3. **Lancez l'application** :
   ```bash
   mvn javafx:run
   ```

### **Solution 3: Télécharger JavaFX séparément**

1. **Téléchargez JavaFX** :
   - Site : https://gluonhq.com/products/javafx/
   - Version : 17.0.2 (même que dans pom.xml)

2. **Extrayez dans un dossier** , ex: `C:\javafx`

3. **Modifiez le script `run_direct.bat`** :
   ```batch
   REM Remplacez cette ligne :
   --module-path "C:\path\to\javafx\lib"
   REM Par :
   --module-path "C:\javafx\lib"
   ```

4. **Lancez avec le script modifié** :
   ```bash
   run_direct.bat
   ```

### **Solution 4: Configuration IDE**

#### **IntelliJ IDEA** :
1. **File → Project Structure → Project SDK**
2. Sélectionnez un JDK avec JavaFX ou Liberica JDK Full
3. **Run → Edit Configurations**
4. Ajoutez les VM options :
   ```
   --module-path "C:\chemin\vers\javafx\lib" --add-modules javafx.controls,javafx.fxml
   ```

#### **Eclipse** :
1. **Window → Preferences → Java → Installed JREs**
2. Ajoutez un JDK avec JavaFX
3. **Run Configurations → Arguments → VM arguments**
4. Ajoutez :
   ```
   --module-path "C:\chemin\vers\javafx\lib" --add-modules javafx.controls,javafx.fxml
   ```

---

## 🧪 **Test des solutions**

### **Test 1: Vérifier JavaFX**
```bash
java --list-modules | findstr javafx
```
- Si résultat → JavaFX disponible ✅
- Si vide → JavaFX manquant ❌

### **Test 2: Compiler avec Maven**
```bash
mvn clean compile
```
- Si succès → OK ✅
- Si erreur → Problème de dépendances ❌

### **Test 3: Lancer l'application**
```bash
mvn javafx:run
```
- Si fenêtre JavaFX apparaît → RÉUSSI ✅
- Si erreur → Continuer le dépannage ❌

---

## 📋 **Résumé des commandes**

```bash
# Vérifications
java -version                    # Version Java
java --list-modules | findstr javafx  # JavaFX disponible?
mvn -version                     # Maven installé?

# Solutions Maven
mvn clean install               # Télécharger dépendances
mvn javafx:run                  # Lancer application
mvn clean install -U           # Forcer mise à jour

# Solutions alternatives
run_direct.bat                  # Script direct (modifier chemin JavaFX)
```

---

## 🔧 **Configuration avancée**

### **Variables d'environnement** (Windows) :
```
JAVA_HOME = C:\Program Files\Java\jdk-17
PATH = %JAVA_HOME%\bin;%PATH%
MAVEN_HOME = C:\apache-maven-3.8.6
PATH = %MAVEN_HOME%\bin;%PATH%
```

### **Arguments JVM pour JavaFX** :
```
--module-path "C:\javafx\lib"
--add-modules javafx.controls,javafx.fxml,javafx.graphics
--add-opens javafx.graphics/javafx.scene=ALL-UNNAMED
```

---

## 📞 **Si rien ne marche**

### **Dernière solution : JDK complet**
1. Désinstallez votre JDK actuel
2. Installez **Liberica JDK Full 17+**
3. Redémarrez votre ordinateur
4. Lancez : `mvn javafx:run`

### **Support supplémentaire**
- **Documentation JavaFX** : https://openjfx.io/
- **Forum StackOverflow** : Cherchez "JavaFX runtime components are missing"
- **GitHub Issues** : Vérifiez les issues similaires

---

## ✅ **Résumé**

| Solution | Complexité | Recommandé pour |
|---|---|---|
| **Maven** | Facile | Tous les utilisateurs |
| **JDK avec JavaFX** | Moyen | Développement |
| **JavaFX séparé** | Difficile | Avancé |
| **IDE Config** | Moyen | Développement IDE |

**Solution recommandée** : Utilisez Maven avec `mvn javafx:run` 🚀

---

**Guide de dépannage - Erreur JavaFX**  
Version 1.0 | 2026-04-15  
Résoudre "composants d'exécution JavaFX manquants"

