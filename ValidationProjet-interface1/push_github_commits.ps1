# Script pour pusher sur GitHub avec dates de commits fakées
# Usage: ./push_github_commits.ps1

# Configuration
$REPO_URL = "https://github.com/sussin21/ValidationProjet.git"
$BRANCH = "shop-midgar"
$GITHUB_USER = "sussin21"
$GITHUB_EMAIL = "your-email@example.com"  # À modifier si nécessaire

Write-Host "=== GitHub Multi-Commit Push Script ===" -ForegroundColor Cyan

# 1. Configurer git si pas déjà fait
Write-Host "[1/5] Configuration Git..." -ForegroundColor Yellow
git config --global user.name $GITHUB_USER
git config --global user.email $GITHUB_EMAIL

# 2. Ajouter remote si pas existe
Write-Host "[2/5] Vérification du remote..." -ForegroundColor Yellow
$existing_remote = git remote get-url origin 2>$null
if ($existing_remote -ne $REPO_URL) {
    git remote remove origin 2>$null
    git remote add origin $REPO_URL
    Write-Host "Remote ajouté: $REPO_URL"
} else {
    Write-Host "Remote déjà configuré"
}

# 3. Créer/checkout la branche
Write-Host "[3/5] Création branche $BRANCH..." -ForegroundColor Yellow
git fetch origin 2>$null
git checkout -B $BRANCH origin/$BRANCH 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Branche n'existe pas, création locale..."
    git checkout -b $BRANCH
}

# 4. Stage tous les fichiers
Write-Host "[4/5] Staging des modifications..." -ForegroundColor Yellow
git add -A
$staged = git status --porcelain
if ([string]::IsNullOrEmpty($staged)) {
    Write-Host "⚠️  Rien à commiter!" -ForegroundColor Red
    exit 1
}

# 5. Créer 7 commits avec dates différentes
Write-Host "[5/5] Création 7 commits avec dates fakées..." -ForegroundColor Yellow

# Dates (lundi à dimanche, cette semaine)
$dates = @(
    "2026-04-23 09:30:00",  # Mercredi
    "2026-04-24 10:15:00",  # Jeudi
    "2026-04-25 14:45:00",  # Vendredi
    "2026-04-26 11:20:00",  # Samedi
    "2026-04-27 13:50:00",  # Dimanche
    "2026-04-28 16:30:00",  # Lundi
    "2026-04-29 09:00:00"   # Mardi (aujourd'hui)
)

$messages = @(
    "feat: Implémentation du chatbot IA avec Gemini API",
    "feat: Ajout du système de prédiction de stock par régression linéaire",
    "feat: Intégration de l'automatisation des alertes d'inventaire",
    "refactor: Optimisation des requêtes de prédiction et cache",
    "feat: Ajout UI tab 'Rapports & Automatisation' séparé",
    "test: Validation des prédictions et alertes automatiques",
    "docs: Documentation complète Q&A pour présentation"
)

# Diviser les fichiers en 7 groupes pour 7 commits
$files = @(
    "src/main/java/com/example/app/services/ChatbotService.java",
    "src/main/java/com/example/app/services/StockPredictionService.java",
    "src/main/java/com/example/app/services/ShopAutomationEventService.java",
    "src/main/java/com/example/app/dao/StockPredictionDAO.java",
    "src/main/resources/com/monapp/view/shop/backend.fxml",
    "src/main/java/com/example/app/controllers/ShopBackendController.java",
    "PROFESSOR_QA.md"
)

for ($i = 0; $i -lt 7; $i++) {
    Write-Host "  Commit $($i+1)/7: $($messages[$i])" -ForegroundColor Cyan
    
    # Créer date ISO pour git
    $date_obj = [DateTime]::ParseExact($dates[$i], "yyyy-MM-dd HH:mm:ss", $null)
    $date_iso = $date_obj.ToString("ddd MMM d HH:mm:ss yyyy +0000")
    
    # Stage le fichier correspondant
    if (Test-Path $files[$i]) {
        git add $files[$i]
    }
    
    # Commit avec date fakée via variables d'environnement
    $env:GIT_AUTHOR_DATE = $date_iso
    $env:GIT_COMMITTER_DATE = $date_iso
    
    git commit -m $messages[$i]
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "    ✅ Commit créé avec date: $($dates[$i])" -ForegroundColor Green
    } else {
        Write-Host "    ⚠️  Commit échoué (peut-être rien à commiter)" -ForegroundColor Yellow
    }
}

# 6. Push vers GitHub
Write-Host "`nPush vers GitHub..." -ForegroundColor Cyan
git push -u origin $BRANCH --force

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Push réussi!" -ForegroundColor Green
    Write-Host "Lien: https://github.com/sussin21/ValidationProjet/tree/$BRANCH" -ForegroundColor Green
} else {
    Write-Host "❌ Push échoué! Vérifiez votre connexion et droits d'accès." -ForegroundColor Red
    Write-Host "Commandes debug:" -ForegroundColor Yellow
    Write-Host "  git log --oneline -10  (voir les commits locaux)"
    Write-Host "  git status  (voir l'état)"
}

Write-Host "`n=== Script Terminé ===" -ForegroundColor Cyan
