#!/bin/bash
# Script de lancement - Midgar JavaFX
# Pour Linux/Mac
# Assurez-vous que Maven et Java 17+ sont installés

echo ""
echo "===================================="
echo "  Midgar JavaFX - Script de lancement"
echo "===================================="
echo ""

# Vérifier Java
if ! command -v java &> /dev/null; then
    echo "ERREUR: Java n'est pas installé ou pas dans le PATH"
    echo "Téléchargez Java 17+ depuis https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

# Aller au répertoire du projet
cd "$(dirname "$0")"

echo ""
echo "Nettoyage et compilation..."
echo ""

# Compiler avec Maven wrapper ou Maven global
if [ -f "mvnw" ]; then
    ./mvnw clean install -DskipTests
else
    mvn clean install -DskipTests
fi

if [ $? -ne 0 ]; then
    echo ""
    echo "ERREUR: La compilation a échoué!"
    echo "Vérifiez que Maven est correctement installé."
    exit 1
fi

echo ""
echo "===================================="
echo "  Lancement de l'application..."
echo "===================================="
echo ""

# Lancer l'application
if [ -f "mvnw" ]; then
    ./mvnw javafx:run
else
    mvn javafx:run
fi

if [ $? -ne 0 ]; then
    echo ""
    echo "ERREUR: Le lancement a échoué!"
    exit 1
fi

echo ""
echo "L'application s'est fermée."
echo ""

