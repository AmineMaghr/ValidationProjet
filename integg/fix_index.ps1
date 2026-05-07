# Script PowerShell pour corriger les fichiers
# Usage: ./fix_index.ps1

Write-Host "========================================" -ForegroundColor Green
Write-Host "Correction des fichiers index" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Chemins
$basePath = "C:\Users\user\IdeaProjects\midgarpd"
$helloAppPath = "$basePath\src\main\java\org\example\midgarpd\HelloApplication.java"
$indexFxmlPath = "$basePath\src\main\resources\com\monapp\view\index.fxml"
$helloAppFixedPath = "$basePath\src\main\java\org\example\midgarpd\HelloApplication_FIXED.java"
$indexFxmlFixedPath = "$basePath\src\main\resources\com\monapp\view\index_FIXED.fxml"

# Vérifier que les fichiers FIXED existent
if (-Not (Test-Path $helloAppFixedPath)) {
    Write-Host "❌ Erreur: $helloAppFixedPath introuvable" -ForegroundColor Red
    exit 1
}

if (-Not (Test-Path $indexFxmlFixedPath)) {
    Write-Host "❌ Erreur: $indexFxmlFixedPath introuvable" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Fichiers FIXED trouvés" -ForegroundColor Green
Write-Host ""

# Sauvegarde
Write-Host "📦 Sauvegarde des fichiers originaux..." -ForegroundColor Yellow
Copy-Item $helloAppPath "$helloAppPath.backup" -Force
Copy-Item $indexFxmlPath "$indexFxmlPath.backup" -Force
Write-Host "✅ Sauvegarde terminée" -ForegroundColor Green
Write-Host ""

# Remplacement
Write-Host "🔄 Remplacement des fichiers..." -ForegroundColor Yellow
Copy-Item $helloAppFixedPath $helloAppPath -Force
Copy-Item $indexFxmlFixedPath $indexFxmlPath -Force
Write-Host "✅ Remplacement terminé" -ForegroundColor Green
Write-Host ""

# Suppression des fichiers FIXED
Write-Host "🧹 Nettoyage..." -ForegroundColor Yellow
Remove-Item $helloAppFixedPath -Force
Remove-Item $indexFxmlFixedPath -Force
Write-Host "✅ Nettoyage terminé" -ForegroundColor Green
Write-Host ""

# Compilation
Write-Host "🔨 Compilation du projet..." -ForegroundColor Cyan
Set-Location $basePath
& mvn clean compile

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "✅ Correction terminée!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Prêt à exécuter: mvn javafx:run" -ForegroundColor Yellow

