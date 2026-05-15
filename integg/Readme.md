# 🎨 Midgar – Plateforme Fantasy Unifiée (Web & Desktop)

**Midgar** est une plateforme de création et de partage d’univers fantasy, d’œuvres artistiques, de personnages et d’artefacts magiques.  
Elle se compose de deux interfaces :

- **Site web (Symfony)** : vitrine publique, e‑commerce, challenges, recommandations IA, administration.
- **Application desktop (JavaFX)** : gestion hors‑ligne, outils avancés, synchronisation avec le serveur.

---

## 📚 Table des matières

1. [Vue d’ensemble](#vue-densemble)
2. [Fonctionnalités principales](#fonctionnalités-principales)
   - [Module Univers / Personnages / Œuvres / Artefacts](#module-univers--personnages--oeuvres--artefacts)
   - [E‑commerce (Shop)](#e-commerce-shop)
   - [Challenges & Participations](#challenges--participations)
   - [Recommandation IA (For You Page)](#recommandation-ia-for-you-page)
   - [Administration & Back‑office](#administration--back-office)
3. [Fonctionnalités spécifiques à l’application desktop (JavaFX)](#fonctionnalités-spécifiques-à-lapplication-desktop-javafx)
4. [Architecture technique](#architecture-technique)
   - [Web (Symfony)](#web-symfony)
   - [Desktop (JavaFX)](#desktop-javafx)
5. [Installation & déploiement](#installation--déploiement)
   - [Prérequis](#prérequis)
   - [Installation du projet web](#installation-du-projet-web)
   - [Installation de l’application desktop](#installation-de-lapplication-desktop)
6. [Documentation API](#documentation-api)
7. [Licence](#licence)

---

## Vue d’ensemble

Midgar offre une expérience complète aux créateurs et amateurs de fantasy :

- **Côté web** : découvrez et contribuez à des univers, passez des commandes dans la boutique, participez à des défis, recevez des recommandations personnalisées.
- **Côté desktop** : travaillez hors‑ligne, gérez vos contenus localement, bénéficiez d’outils de productivité avancés (glisser‑déposer, export PDF, synchronisation différée).

---

## Fonctionnalités principales

### Module Univers / Personnages / Œuvres / Artefacts

- **Univers**  
  Création d’univers avec nom unique, genre (fantasy, SF, horreur…), description courte, contexte narratif, thèmes, bannière, lien YouTube.  
  Consultation, modification et suppression par le créateur ou un admin.

- **Personnages**  
  Fiche détaillée : nom, alias, caractéristiques (âge, espèce), statistiques modifiables (force, agilité, magie, défense), portrait, relations.  
  Fonction **Relancer les stats** (créateur/admin). Export JSON.

- **Œuvres**  
  Publiez des œuvres (peinture, littérature, musique, sculpture, vidéo) avec titre, description, galerie d’images.  
  Support des formats PNG, JPG, GIF, BMP, WEBP.

- **Artefacts**  
  Objets magiques : nom, description, pouvoirs, origine, rareté, image. Stockage des images en chemins relatifs pour compatibilité web.

- **Favoris**  
  Ajoutez des likes sur tous les types de contenus – synchronisés entre web et desktop.

### E‑commerce (Shop)

- **Catalogue** de produits art & fantasy (panier, paiement sécurisé).
- **Chatbot IA** d’assistance client en temps réel.
- **Prédiction des ruptures de stock** via régression linéaire (analyse des ventes historiques).
- **Alertes email automatiques** :
  - Reçus de commande et suivis de livraison pour les clients.
  - Notifications à l’administrateur lorsqu’un produit est sur le point d’être en rupture.

### Challenges & Participations

- **Création de défis** par l’administrateur (titre, description, difficulté, date limite, récompense).
- **Participation** en un clic ; suivi des soumissions, scores et statuts.
- **Emails automatisés** : confirmation de participation, rappels avant la deadline, résultats finaux.
- **Analyses prédictives** : taux de complétion, tendances d’engagement, prévisions.

### Recommandation IA (For You Page)

- **Profilage initial** via un quiz administré à l’inscription. Chaque réponse est mappée à des tags de contenu.
- **Préférences avancées** : l’utilisateur peut ajouter ses propres tags d’intérêt (ex : Dark Fantasy, Magie, Politique).
- **Algorithme de scoring** pondéré : compare les tags des posts avec la matrice d’intérêts de l’utilisateur pour générer un flux personnalisé.
- **Explicabilité** (« Pourquoi je vois ceci ? ») : le système déconstruit le score et utilise l’IA générative pour produire une explication naturelle.
- **Notifications push ciblées** : à la création d’un nouveau post, les utilisateurs dont le profil d’intérêt affiche un score d’affinité élevé reçoivent une notification.

### Administration & Back‑office

- **Dashboard admin** (visible uniquement pour les administrateurs) :
  - **Utilisateurs** : lister, rechercher, bloquer/débloquer, promouvoir admin, supprimer.
  - **Œuvres / Artefacts / Univers / Personnages** : rechercher, supprimer un contenu inapproprié.
- **Modération** : signalement de contenus, validation préalable des nouvelles créations (optionnelle).
- **Gestion des rôles** : utilisateur standard / administrateur.

---

## Fonctionnalités spécifiques à l’application desktop (JavaFX)

L’application de bureau reprend l’intégralité des modules ci‑dessus **et ajoute** des capacités hors‑ligne et de productivité avancée :

- **Mode hors‑ligne complet** : travaillez sans connexion, stockage local H2, synchronisation différée à la reconnexion.
- **Authentification locale sécurisée** (BCrypt) **+ reconnaissance faciale** (webcam, descripteurs stockés).
- **Glisser‑déposer** : import d’images, réorganisation des galeries, export vers le bureau.
- **Éditeur d’images intégré** : recadrage, redimensionnement, prévisualisation avant upload.
- **Gestion multi‑onglets** : navigation simultanée entre plusieurs contenus.
- **Export PDF / CSV / JSON** : fiches détaillées (univers, personnages, œuvres) et données.
- **Synchronisation manuelle & automatique** : intervalle configurable, file d’attente des opérations hors‑ligne.
- **Résolution de conflits** : détection des modifications concurrentes et interface de résolution.
- **Cache local intelligent** : données consultées mises en cache pour un accès rapide.
- **Console SQL intégrée** : exécution de requêtes sur la base locale ou distante.
- **Sauvegarde / restauration** : export complet de la base H2 en fichier SQL.
- **Thèmes** : clair, sombre, nuit, contraste élevé, programmation horaire du mode nuit.
- **Raccourcis clavier configurables**, barre d’outils personnalisable.
- **Notifications système** pour les événements importants (synchro, rappels challenges).
- **Mode démonstration** : lancement avec données factices.
- **Assistant de migration** : convertit les anciens chemins Windows en chemins relatifs compatibles Symfony.
- **Mise à jour automatique** de l’application.
- **Planificateur de synchronisation** (WiFi uniquement, secteur uniquement, heures creuses).
- **Interface en ligne de commande** pour l’automatisation (backup, synchro, export).

---

## Architecture technique

### Web (Symfony)

| Composant                | Technologie                                 |
|--------------------------|---------------------------------------------|
| Framework                | Symfony 6                                   |
| Templating               | Twig + Bootstrap / Tailwind                 |
| Base de données          | MySQL / MariaDB (Doctrine ORM)              |
| Authentification         | BCrypt + Google OAuth (KnpUOAuth2Client)   |
| API REST                 | JSON, endpoints sécurisés par JWT           |
| Emailing                 | Symfony Mailer                              |
| Analytics / IA           | Services externes (ML, API) connectés       |
| Documentation API        | Swagger / OpenAPI                           |

### Desktop (JavaFX)

| Composant                | Technologie                                 |
|--------------------------|---------------------------------------------|
| Interface                | JavaFX 17 (FXML, CSS)                       |
| Base de données locale   | H2 (mode embarqué)                          |
| Synchronisation          | Client HTTP (OkHttp), file d’attente        |
| Traitement d’images      | JavaFX Image, ByteArray, compression        |
| Reconnaissance faciale   | OpenCV (via JavaCV) + descripteurs stockés  |
| Export / PDF             | OpenPDF, iText (pour les fiches)            |
| Logging                  | Log4j / java.util.logging                   |

---

## Installation & déploiement

### Prérequis

- **Web** : PHP 8.1+, Composer, MySQL, serveur web (Apache / Nginx / Symfony CLI).
- **Desktop** : Java 17+, Maven, connexion MySQL (pour la synchronisation).

