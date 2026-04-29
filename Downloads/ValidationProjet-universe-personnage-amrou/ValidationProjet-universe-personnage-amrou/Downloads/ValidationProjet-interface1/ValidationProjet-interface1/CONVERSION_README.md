# Midgar JavaFX - Conversion de Symfony à JavaFX

## 📋 Résumé de la conversion

Cette documentation explique la conversion du projet Symfony "Midgar" en une application JavaFX desktop.

## ✅ Fichiers créés

### Structure de répertoires
```
src/
├── main/
│   ├── java/com/example/app/
│   │   ├── controllers/
│   │   │   ├── LoginController.java         ✅ CRÉÉ
│   │   │   ├── HomeController.java          ✅ CRÉÉ
│   │   │   ├── UniversesController.java     ✅ CRÉÉ
│   │   │   └── ...
│   │   ├── services/                        ✅ Existant (UserService, etc.)
│   │   ├── entities/                        ✅ Existant (User, Universe, etc.)
│   │   └── utils/
│   │       ├── SceneManager.java            ✅ CRÉÉ
│   │       ├── SessionManager.java          ✅ CRÉÉ
│   │       └── MyDatabase.java              ✅ Migré
│   └── resources/com/example/app/
│       ├── views/
│       │   ├── common/
│       │   │   ├── login.fxml               ✅ CRÉÉ
│       │   │   ├── home.fxml                ✅ CRÉÉ
│       │   │   └── ...
│       │   └── universes/
│       │       ├── universes.fxml           ✅ CRÉÉ
│       │       └── ...
│       └── css/
│           └── style.css                    ✅ CRÉÉ
└── pom.xml                                  ✅ Mis à jour
```

### Contrôleurs Symfony convertis en JavaFX

| Symfony Controller | JavaFX Controller | FXML | Status |
|---|---|---|---|
| PageController (index) | HomeController | home.fxml | ✅ Créé |
| SecurityController (login) | LoginController | login.fxml | ✅ Créé |
| UniverseController | UniversesController | universes.fxml | ✅ Créé |
| OeuvreController | OeuvresController | oeuvres.fxml | 📋 À faire |
| PersonnageController | PersonnagesController | personnages.fxml | 📋 À faire |
| QuizController | QuizController | quiz.fxml | 📋 À faire |
| DefiController | DefiController | challenges.fxml | 📋 À faire |
| ShopController | ShopController | shop.fxml | 📋 À faire |
| AdminController | AdminController | admin/dashboard.fxml | 📋 À faire |

## 🔄 Architecture

### Pattern MVC
```
View (FXML) → Controller (JavaFX) → Service → Database
   ↑                                           ↓
   └───────────── Entity ←────────────────────┘
```

### Navigation
- **SceneManager**: Gère le chargement des fichiers FXML et les transitions entre scènes
- **SessionManager**: Gère l'utilisateur actuellement connecté et ses permissions

### Flux de connexion
```
Login Page → Valider les identifiants → SessionManager.setCurrentUser() 
         → SceneManager.showScene("common/home") → Home Page
```

## 🎨 Styling

Le projet utilise un thème sombre avec des accents en turquoise (#18E3A4) inspiré du design Midgar original.

### Fichiers CSS
- `css/style.css` - Styles principaux et composants

### Couleurs principales
- **Primaire**: #18E3A4 (Turquoise)
- **Text**: #E6FFF6 (Blanc-bleu)
- **Background**: #1A1F1E (Gris foncé)
- **Dark**: #0D0F0F (Très noir)

## 📦 Services et Entités utilisés

### Services (de Symfony, migrés en Java)
- ✅ UserService
- ✅ UniverseService
- ✅ OeuvreService
- ✅ DefiService
- ✅ PersonnageService
- ✅ ProductService (Shop)
- 📋 ParticipationService

### Entités
- ✅ User
- ✅ Universe
- ✅ Oeuvre
- ✅ Defi
- ✅ Personnage
- ✅ Produit
- 📋 Participation

## 🚀 Fonctionnalités implémentées

### ✅ Écrans créés
1. **Login** - Authentification utilisateur
2. **Home** - Page d'accueil avec listes: univers populaires, créations récentes, défis
3. **Univers** - Liste et recherche des univers

### 📋 À faire
- Oeuvres
- Personnages
- Quiz
- Défis
- Boutique
- Profil utilisateur
- Admin Dashboard

## 💻 Démarrage

### Prérequis
- Java 17+
- Maven
- MySQL (base de données existante)

### Lancer l'application
```bash
mvn clean javafx:run
```

Ou via votre IDE (IntelliJ IDEA, Eclipse, etc.)

### Identifiants de test
- Utilisateur: `admin`
- Mot de passe: `123456`

## 🔧 Configuration

### Base de données
La configuration se fait dans `MyDatabase.java`:
```java
private final String URL = "jdbc:mysql://localhost:3306/t";
private final String USERNAME = "root";
private final String PASSWORD = "";
```

Mise à jour si nécessaire selon votre configuration MySQL.

## 📝 Prochaines étapes

1. **Implémenter les contrôleurs restants**
   - OeuvresController, PersonnagesController
   - QuizController, DefiController
   - ShopController, AdminController

2. **Créer les fichiers FXML correspondants**
   - Suivre le même pattern que login.fxml et home.fxml

3. **Ajouter les fonctionnalités de création/édition**
   - Créer des formulaires pour univers, oeuvres, etc.

4. **Implémenter les dialogues et alertes**
   - Confirmations, messages d'erreur

5. **Optimiser la performance**
   - Chargement asynchrone des données
   - Cache des images

6. **Tests unitaires**
   - JUnit tests pour les contrôleurs et services

## 📚 Ressources

- [JavaFX Documentation](https://openjfx.io/)
- [Maven POM Configuration](https://maven.apache.org/)
- [FXML Tutorial](https://docs.oracle.com/javase/8/javafx/fxml-tutorial/index.html)

## 📞 Support

Pour toute question ou problème:
1. Vérifier les logs dans la console
2. Consulter la documentation JavaFX
3. Vérifier la configuration de la base de données

---

**Dernière mise à jour**: 2026-04-15
**Version**: 1.0

