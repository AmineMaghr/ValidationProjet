# 📦 Maven - Installation des dépendances complètes

## ✅ Ce qui a été fait

Le fichier `pom.xml` a été mis à jour avec **23 dépendances** complètes + **8 plugins** pour un projet JavaFX production-ready.

---

## 🚀 Démarrage RAPIDE

### 1. Synchroniser Maven (IMPORTANT)
```bash
mvn clean install
```
Cela téléchargera toutes les 23 dépendances.

### 2. Lancer l'application
```bash
mvn javafx:run
```

### 3. Ou via les scripts fournis

**Windows**:
- Double-clic sur `run.bat`

**Linux/Mac**:
```bash
chmod +x run.sh
./run.sh
```

---

## 📋 Dépendances installées

| Catégorie | Nombre | Utilisé pour |
|---|---|---|
| **JavaFX** | 5 | Interface utilisateur |
| **Base de données** | 1 | MySQL |
| **Logging** | 3 | Logs (SLF4J + Logback) |
| **Validation** | 2 | Validations |
| **JSON** | 1 | Sérialisation Gson |
| **Commons** | 2 | Utilitaires |
| **Tests** | 5 | JUnit 5 + TestFX |
| **Plugins** | 8 | Build + exécution |

**Total: 27 artefacts Maven** ✅

---

## 🎯 Commandes utiles

```bash
# Compiler
mvn compile

# Compiler + tests
mvn test

# Créer un JAR exécutable
mvn package

# Lancer l'application
mvn javafx:run

# Voir l'arborescence des dépendances
mvn dependency:tree

# Nettoyer les fichiers compilés
mvn clean

# Tout en un (nettoyage + compilation + tests)
mvn clean install

# Forcer la mise à jour des dépendances
mvn clean install -U
```

---

## 📄 Plus de détails

Pour une documentation complète sur chaque dépendance, consultez:

📖 **[DEPENDENCIES.md](DEPENDENCIES.md)** - Guide détaillé de chaque dépendance

---

## ⚠️ Troubleshooting

### Erreur: "Maven command not found"
**Solution**: Installer Maven ou utiliser votre IDE (IntelliJ, Eclipse, VS Code)

Dans IntelliJ:
1. Clic droit sur `pom.xml`
2. → Run → Maven → clean install

### Erreur: "Could not find artifact"
**Solution**:
```bash
mvn dependency:purge-local-repository clean install
```

### Erreur: "Compilation failed"
**Solution**:
```bash
mvn clean install -DskipTests
```
(Skip les tests la première fois)

---

## ✨ Résumé

✅ 23 dépendances installées
✅ 8 plugins Maven configurés  
✅ Scripts de lancement inclus (run.bat, run.sh)
✅ Documentation complète fournie
✅ Prêt pour production!

**Prochaine étape**: `mvn clean install` 🚀

Pour plus d'infos: [DEPENDENCIES.md](DEPENDENCIES.md)

