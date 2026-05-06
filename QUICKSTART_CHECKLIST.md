# ✅ Checklist - Démarrage rapide Midgar JavaFX

## 🚀 Avant de lancer l'application

### Prérequis installés?
- [ ] Java 17+ installé (`java -version`)
- [ ] Maven installé (`mvn -version`)
- [ ] MySQL installé et en cours d'exécution
- [ ] Git (optionnel)

### Configuration de la base de données
- [ ] Base de données `t` créée dans MySQL
- [ ] Tables créées (user, universe, oeuvre, defi, etc.)
- [ ] Utilisateur admin créé:
  ```sql
  INSERT INTO user (username, email, password, role) 
  VALUES ('admin', 'admin@midgar.com', '123456', 'admin');
  ```
- [ ] MyDatabase.java configuré avec les bonnes credentials

### Projet importé et configuré
- [ ] Projet ouvert dans l'IDE (IntelliJ, Eclipse, VS Code)
- [ ] Maven synchronisé (`mvn clean install`)
- [ ] Pas d'erreurs de compilation
- [ ] pom.xml reconnu

---

## 🎮 Premier lancement

### Via IDE (IntelliJ IDEA - Recommandé)
1. [ ] Clic droit sur `Main.java`
2. [ ] Sélectionner "Run 'Main.main()'"
3. [ ] Attendre le lancement (~10-15 secondes)

### Via ligne de commande
```bash
cd C:\Users\Amine Maghrebi\IdeaProjects\bank-account-app\tprojetpi
mvn clean javafx:run
```
- [ ] Commande exécutée sans erreur

### Fenêtre lancée?
- [ ] Fenêtre JavaFX ouverte (1400x900)
- [ ] Écran de login visible
- [ ] Logo "🐉 Midgar" visible

---

## 🔐 Authentification

### Se connecter
- [ ] Username: `admin`
- [ ] Password: `123456`
- [ ] Clic sur "Se Connecter"
- [ ] Redirection vers la page d'accueil

### Vérifier l'erreur de connexion
- [ ] Essayer avec un mauvais mot de passe
- [ ] Message d'erreur affiché
- [ ] Revenir à l'écran de login

---

## 🌍 Navigation - Vérifier toutes les pages

### Home (Accueil)
- [ ] Page chargée avec listes (univers, créations, défis)
- [ ] Barre de navigation visible en haut
- [ ] Bouton "Déconnexion" visible

### Univers
- [ ] [ ] Clic sur "Univers" dans la navbar
- [ ] [ ] Page des univers chargée
- [ ] [ ] Liste des univers visible
- [ ] [ ] Recherche fonctionne
- [ ] [ ] Bouton "Créer" visible

### Créer un univers
- [ ] [ ] Clic sur "Créer"
- [ ] [ ] Formulaire visible
- [ ] [ ] Tous les champs présents:
  - [ ] Nom de l'univers
  - [ ] Description courte
  - [ ] Description complète
  - [ ] Thèmes
- [ ] [ ] Bouton "Créer" fonctionne
- [ ] [ ] Retour à la liste après création

### Oeuvres
- [ ] [ ] Clic sur "Oeuvres"
- [ ] [ ] Liste des oeuvres visible
- [ ] [ ] Recherche fonctionne

### Personnages
- [ ] [ ] Clic sur "Personnages"
- [ ] [ ] Liste des personnages visible
- [ ] [ ] Recherche fonctionne

### Défis
- [ ] [ ] Clic sur "Défis"
- [ ] [ ] Liste des défis visible
- [ ] [ ] Double-clic affiche les détails
- [ ] [ ] Affichage du titre, thème, dates

### Boutique
- [ ] [ ] Clic sur "Boutique"
- [ ] [ ] Liste des produits visible

### Admin Dashboard (Admin only)
- [ ] [ ] Se reconnecter avec admin si nécessaire
- [ ] [ ] Naviguer vers Admin Dashboard
  - Note: À implémenter dans la navbar
- [ ] [ ] Onglets visibles:
  - [ ] Utilisateurs
  - [ ] Univers
  - [ ] Oeuvres
  - [ ] Défis
- [ ] [ ] Recherche fonctionne dans les onglets

---

## 🔙 Navigation inverse

### Retour depuis chaque page
- [ ] [ ] Univers → Clic "← Retour" → Home
- [ ] [ ] Oeuvres → Clic "← Retour" → Home
- [ ] [ ] Personnages → Clic "← Retour" → Home
- [ ] [ ] Défis → Clic "← Retour" → Home
- [ ] [ ] Shop → Clic "← Retour" → Home

### Via la navbar
- [ ] [ ] Clic "Accueil" → Home depuis n'importe quelle page
- [ ] [ ] Clic sur un lien → Change la page
- [ ] [ ] Navbar visible sur toutes les pages

---

## 👤 Gestion des utilisateurs

### Enregistrement
- [ ] [ ] Clic "S'inscrire" depuis login
- [ ] [ ] Formulaire visible
- [ ] [ ] Créer un nouvel utilisateur
- [ ] [ ] Se connecter avec le nouveau compte

### Déconnexion
- [ ] [ ] Clic "Déconnexion"
- [ ] [ ] Retour à l'écran de login
- [ ] [ ] Ancien utilisateur oublié

---

## 🎨 Styling et UI

### Thème appliqué
- [ ] [ ] Couleur primaire turquoise (#18E3A4) visible
- [ ] [ ] Arrière-plan sombre (#1A1F1E)
- [ ] [ ] Texte blanc-bleu (#E6FFF6)

### Contrôles réactifs
- [ ] [ ] Boutons avec hover effect
- [ ] [ ] TextFields avec border turquoise
- [ ] [ ] ListViews lisibles et scrollables
- [ ] [ ] Pas de text overlap

### Responsive
- [ ] [ ] Fenêtre redimensionnable
- [ ] [ ] Contenu s'adapte
- [ ] [ ] Pas de contenu coupé

---

## 🐛 Dépannage

### Si erreur "Base de données non trouvée"
- [ ] [ ] Vérifier que MySQL est lancé
- [ ] [ ] Vérifier le URL dans MyDatabase.java
- [ ] [ ] Vérifier que la base de données `t` existe
- [ ] [ ] Redémarrer MySQL si nécessaire

### Si erreur "Fichier FXML non trouvé"
- [ ] [ ] Vérifier l'orthographe du fichier
- [ ] [ ] Vérifier que le fichier existe dans src/main/resources/
- [ ] [ ] Maven rebuild: `mvn clean install`

### Si erreur "Connexion refusée"
- [ ] [ ] Vérifier les identifiants (admin/123456)
- [ ] [ ] Vérifier que l'utilisateur existe en base de données
- [ ] [ ] Vérifier la structure de la table user

### Si la fenêtre ne s'ouvre pas
- [ ] [ ] Vérifier que Java 17+ est utilisé
- [ ] [ ] Vérifier les logs dans la console
- [ ] [ ] Vérifier que le fichier Main.java existe
- [ ] [ ] Maven rebuild: `mvn clean install`

---

## 📊 Checklist finale

### Tous les tests passés?
- [ ] [ ] Authentification fonctionne
- [ ] [ ] Navigation fonctionne
- [ ] [ ] Toutes les pages se chargent
- [ ] [ ] Recherche fonctionne
- [ ] [ ] Styling correct
- [ ] [ ] Pas d'erreurs dans la console
- [ ] [ ] Admin dashboard accessible (si admin)

### Application prête pour la production?
- [ ] [ ] Base de données sauvegardée
- [ ] [ ] Code commité (si Git)
- [ ] [ ] Tests effectués
- [ ] [ ] Documentation lue

---

## 📚 Ressources

Si besoin d'aide:
1. Lire `GUIDE_COMPLET.md`
2. Lire `TECHNICAL_SUMMARY.md`
3. Consulter `CONVERSION_README.md`
4. Vérifier les logs console
5. Documentation JavaFX: https://openjfx.io/

---

## 🎉 Succès!

Si vous avez coché tous les cases, **Félicitations!** 🎊

Midgar JavaFX est maintenant opérationnel et prêt à être utilisé et développé.

---

**Checklist créée**: 2026-04-15  
**Version**: 1.0  
**À utiliser pour chaque session de développement**

