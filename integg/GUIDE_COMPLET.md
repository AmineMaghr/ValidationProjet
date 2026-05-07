# Midgar JavaFX - Guide Complet de Conversion et Utilisation

## 📚 Table des matières
1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Installation et Configuration](#installation-et-configuration)
4. [Utilisation](#utilisation)
5. [Fichiers créés](#fichiers-créés)
6. [Développement futur](#développement-futur)

---

## 🎯 Vue d'ensemble

**Midgar JavaFX** est une conversion complète du projet web Symfony "Midgar" en une application desktop JavaFX.

### Objectifs atteints :
- ✅ Migration d'une architecture web vers desktop
- ✅ Conversion des templates Twig en fichiers FXML
- ✅ Conversion des contrôleurs Symfony en contrôleurs JavaFX
- ✅ Réutilisation des services et entités Java
- ✅ Implémentation d'un système de navigation centralisé
- ✅ Gestion des sessions utilisateur
- ✅ Authentification et autorisation (admin)

---

## 🏗️ Architecture

### Pattern MVC + Services

```
┌─────────────────────────────────────────────┐
│         JavaFX Application (Main)           │
│        - SceneManager (Navigation)          │
│        - SessionManager (Utilisateur)       │
└─────────────────────────────────────────────┘
                    ↓
        ┌───────────────────────┐
        │   View (FXML Files)   │
        │  ├─ login.fxml        │
        │  ├─ home.fxml         │
        │  ├─ universes.fxml    │
        │  └─ ...               │
        └───────────────────────┘
                    ↓
    ┌───────────────────────────────────┐
    │   Controller (JavaFX)             │
    │  ├─ LoginController               │
    │  ├─ HomeController                │
    │  ├─ UniversesController           │
    │  └─ ...                           │
    └───────────────────────────────────┘
                    ↓
    ┌───────────────────────────────────┐
    │   Service (Business Logic)        │
    │  ├─ UserService                   │
    │  ├─ UniverseService               │
    │  ├─ OeuvreService                 │
    │  └─ ...                           │
    └───────────────────────────────────┘
                    ↓
    ┌───────────────────────────────────┐
    │   Entity (Data Model)             │
    │  ├─ User                          │
    │  ├─ Universe                      │
    │  ├─ Oeuvre                        │
    │  └─ ...                           │
    └───────────────────────────────────┘
                    ↓
    ┌───────────────────────────────────┐
    │   Database (MySQL)                │
    │   jdbc:mysql://localhost:3306/t  │
    └───────────────────────────────────┘
```

### Structure des fichiers

```
src/
├── main/
│   ├── java/com/example/app/
│   │   ├── Main.java
│   │   ├── controllers/
│   │   │   ├── LoginController.java
│   │   │   ├── HomeController.java
│   │   │   ├── UniversesController.java
│   │   │   ├── OeuvresController.java
│   │   │   ├── PersonnagesController.java
│   │   │   ├── ChallengesController.java
│   │   │   ├── ShopController.java
│   │   │   └── AdminController.java
│   │   ├── services/
│   │   │   ├── UserService.java
│   │   │   ├── UniverseService.java
│   │   │   ├── OeuvreService.java
│   │   │   ├── DefiService.java
│   │   │   ├── PersonnageService.java
│   │   │   ├── ProduitService.java
│   │   │   └── ...
│   │   ├── entities/
│   │   │   ├── User.java
│   │   │   ├── Universe.java
│   │   │   ├── Oeuvre.java
│   │   │   ├── Defi.java
│   │   │   ├── Personnage.java
│   │   │   ├── Produit.java
│   │   │   └── ...
│   │   └── utils/
│   │       ├── SceneManager.java
│   │       ├── SessionManager.java
│   │       └── MyDatabase.java
│   └── resources/com/example/app/
│       ├── views/
│       │   ├── common/
│       │   │   ├── login.fxml
│       │   │   ├── register.fxml
│       │   │   └── home.fxml
│       │   ├── universes/
│       │   │   ├── universes.fxml
│       │   │   └── create_universe.fxml
│       │   ├── oeuvres/
│       │   │   └── oeuvres.fxml
│       │   ├── personnages/
│       │   │   └── personnages.fxml
│       │   ├── challenges/
│       │   │   └── challenges.fxml
│       │   ├── shop/
│       │   │   └── shop.fxml
│       │   └── admin/
│       │       └── dashboard.fxml
│       └── css/
│           └── style.css
└── pom.xml
```

---

## 🔧 Installation et Configuration

### Prérequis
- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8.0+

### 1. Clone ou extraction du projet

```bash
cd C:\Users\Amine Maghrebi\IdeaProjects\bank-account-app\tprojetpi
```

### 2. Configuration de la base de données

Modifiez `src/main/java/com/example/app/utils/MyDatabase.java`:

```java
private final String URL = "jdbc:mysql://localhost:3306/t";
private final String USERNAME = "root";
private final String PASSWORD = "";
```

Assurez-vous que :
- Le service MySQL est en cours d'exécution
- La base de données `t` existe
- Les tables sont créées

### 3. Installation des dépendances Maven

```bash
mvn clean install
```

### 4. Lancement de l'application

#### Via ligne de commande
```bash
mvn clean javafx:run
```

#### Via IDE (IntelliJ IDEA / Eclipse)
1. Importer le projet comme projet Maven
2. Clic droit sur le projet → Run → Run 'Main.main()'

---

## 🚀 Utilisation

### Écran de connexion

**Page**: `login.fxml`
**Identifiants de test**:
- Utilisateur: `admin`
- Mot de passe: `123456`

Les identifiants sont validés directement dans la base de données.

### Pages disponibles

#### 1. Accueil (Home)
- **URL FXML**: `common/home.fxml`
- **Contrôleur**: `HomeController.java`
- **Fonctionnalités**:
  - Affichage des univers populaires
  - Affichage des créations récentes
  - Affichage des défis actuels
  - Navigation vers toutes les pages

#### 2. Univers
- **URL FXML**: `universes/universes.fxml`
- **Contrôleur**: `UniversesController.java`
- **Fonctionnalités**:
  - Liste complète des univers
  - Recherche par nom/description
  - Création d'un nouvel univers
  - Double-clic pour afficher les détails

#### 3. Création d'univers
- **URL FXML**: `universes/create_universe.fxml`
- **Contrôleur**: `CreateUniverseController.java`
- **Formulaire**:
  - Nom de l'univers
  - Description courte
  - Description complète
  - Thèmes (séparés par des virgules)

#### 4. Oeuvres
- **URL FXML**: `oeuvres/oeuvres.fxml`
- **Contrôleur**: `OeuvresController.java`
- **Fonctionnalités**:
  - Liste des oeuvres
  - Recherche par titre/auteur
  - Affichage des détails

#### 5. Personnages
- **URL FXML**: `personnages/personnages.fxml`
- **Contrôleur**: `PersonnagesController.java`
- **Fonctionnalités**:
  - Liste des personnages
  - Recherche par nom/type
  - Filtrage avancé

#### 6. Défis
- **URL FXML**: `challenges/challenges.fxml`
- **Contrôleur**: `ChallengesController.java`
- **Fonctionnalités**:
  - Liste des défis actuels
  - Affichage des détails
  - Participation aux défis
  - Dates de début/fin

#### 7. Boutique
- **URL FXML**: `shop/shop.fxml`
- **Contrôleur**: `ShopController.java`
- **Fonctionnalités**:
  - Liste des produits
  - Prix et descriptions
  - Achat de produits (à implémenter)

#### 8. Admin Dashboard
- **URL FXML**: `admin/dashboard.fxml`
- **Contrôleur**: `AdminController.java`
- **Accès**: Admin uniquement (rôle = "admin")
- **Onglets**:
  - **Utilisateurs**: Gestion des utilisateurs, recherche
  - **Univers**: Gestion des univers
  - **Oeuvres**: Gestion des oeuvres
  - **Défis**: Gestion des défis

---

## 📋 Fichiers créés

### Contrôleurs (8 fichiers)
✅ LoginController.java
✅ HomeController.java
✅ UniversesController.java
✅ CreateUniverseController.java
✅ OeuvresController.java
✅ PersonnagesController.java
✅ ChallengesController.java
✅ ShopController.java
✅ RegisterController.java
✅ AdminController.java (mis à jour)

### Fichiers FXML (11 fichiers)
✅ common/login.fxml
✅ common/register.fxml
✅ common/home.fxml
✅ universes/universes.fxml
✅ universes/create_universe.fxml
✅ oeuvres/oeuvres.fxml
✅ personnages/personnages.fxml
✅ challenges/challenges.fxml
✅ shop/shop.fxml
✅ admin/dashboard.fxml

### Utilitaires (2 fichiers)
✅ SceneManager.java - Gestion de la navigation
✅ SessionManager.java - Gestion de la session utilisateur

### CSS (1 fichier)
✅ style.css - Thème sombre avec accents turquoise

### Documentation (2 fichiers)
✅ CONVERSION_README.md
✅ GUIDE_COMPLET.md (ce fichier)

---

## 🎨 Styling

### Thème
- **Couleur primaire**: #18E3A4 (Turquoise)
- **Texte**: #E6FFF6 (Blanc-bleu)
- **Arrière-plan**: #1A1F1E (Gris foncé)
- **Très noir**: #0D0F0F

### Classes CSS
- `.button` - Boutons primaires
- `.text-field` - Champs de texte
- `.list-view` - Listes
- `.label` - Étiquettes
- Et bien d'autres...

---

## 📝 Développement futur

### À implémenter
- [ ] Page Quiz avec questions/réponses
- [ ] Système de panier/achat pour la boutique
- [ ] Chargement asynchrone des images
- [ ] Pagination pour les longues listes
- [ ] Filtrages avancés
- [ ] Historique des actions utilisateur
- [ ] Notifications en temps réel
- [ ] Édition/suppression d'éléments
- [ ] Upload d'images pour les avatars
- [ ] Système de commentaires

### Optimisations suggérées
- Implémenter le chargement asynchrone (Task<T>)
- Ajouter un système de cache pour les données
- Implémenter la pagination côté contrôleur
- Ajouter des animations de transition entre les pages
- Valider les formulaires avec des RegEx
- Ajouter des tests unitaires JUnit

### Structure suggérée pour les futurs contrôleurs

```java
package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.app.services.*;
import com.example.app.entities.*;
import com.example.app.utils.*;

public class MyNewController {
    @FXML
    private Label title;
    
    private MyService service;
    private List<MyEntity> allData;

    @FXML
    public void initialize() {
        service = new MyService();
        loadData();
        setupUI();
    }

    private void loadData() {
        // Charger les données depuis la service
    }

    private void setupUI() {
        // Configurer les éléments UI
    }

    @FXML
    public void goHome() {
        SceneManager.showScene("common/home", "Midgar - Accueil");
    }
}
```

---

## 🐛 Dépannage

### Erreur: "Impossible de se connecter à la base de données"
1. Vérifier que MySQL est en cours d'exécution
2. Vérifier les identifiants dans `MyDatabase.java`
3. Vérifier que la base de données `t` existe

### Erreur: "Fichier FXML non trouvé"
1. Vérifier l'orthographe du chemin dans `SceneManager.showScene()`
2. Vérifier que le fichier existe dans `src/main/resources/`
3. Vérifier les chemins avec des slashes `/`

### Erreur: "Accès refusé" pour l'admin
1. Vérifier que l'utilisateur a le rôle "admin" dans la base de données
2. Vérifier le code dans `AdminController.initialize()`

---

## 📞 Support

Pour toute question:
1. Consulter la [documentation JavaFX officielle](https://openjfx.io/)
2. Vérifier les logs dans la console
3. Revérifier la configuration de la base de données

---

## 📄 Licence

Ce projet est une conversion éducative du projet Symfony Midgar.

**Version**: 1.0  
**Date**: 2026-04-15  
**Développeur**: Amine Maghrebi

