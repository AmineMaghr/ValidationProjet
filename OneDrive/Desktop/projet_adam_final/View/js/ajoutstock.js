var b = document.getElementById("fs");
b.addEventListener("click", function (event) {
    let x = true;

    const nom_produit = document.getElementById("nom_produit").value;
    const quantite = document.getElementById("quantite").value;
    const unite = document.getElementById("unite").value;
    const prix_uni = document.getElementById("prix_uni").value;
    const date_expir = document.getElementById("date_expir").value;
    const dispo = document.getElementById("dispo").value;

    const error_nom_produit = document.getElementById("error_nom_produit");
    const error_quantite = document.getElementById("error_quantite");
    const error_unite = document.getElementById("error_unite");
    const error_date_expir = document.getElementById("error_date_expir");
    const error_prix_uni = document.getElementById("error_prix_uni");
    const error_dispo = document.getElementById("error_dispo");

    error_nom_produit.innerHTML = "";
    error_quantite.innerHTML = "";
    error_date_expir.innerHTML = "";
    error_unite.innerHTML = "";
    error_prix_uni.innerHTML = "";
    error_dispo.innerHTML = "";

    if (!nom_produit || !/^[A-Za-z\s\-éèêàç]+$/.test(nom_produit)) {
        error_nom_produit.innerHTML = "Nom du produit doit contenir uniquement des lettres et des espaces !";
        x = false;
    } else {
        error_nom_produit.innerHTML = ""; 
    }
    if (!quantite || quantite <= 0 || isNaN(quantite)) {
        error_quantite.innerHTML = "Quantité doit être un nombre positif supérieur à zéro !";
        x = false;
    } else {
        error_quantite.innerHTML = "";
    }

   
    if (!unite || !/^[A-Za-z\s]+$/.test(unite)) {
        error_unite.innerHTML = "Unité doit contenir uniquement des lettres !";
        x = false;
    } else {
        error_unite.innerHTML = ""; 
    }

  
    const floatRegex = /^-?\d+(\.\d+)?$/;
    if (!prix_uni || !floatRegex.test(prix_uni) || parseFloat(prix_uni) <= 0) {
        error_prix_uni.innerHTML = "Veuillez saisir un prix valide (nombre décimal ou entier positif) !";
        x = false;
    } else {
        error_prix_uni.innerHTML = ""; 
    }

   
    if (!date_expir || date_expir === "") {
        error_date_expir.innerHTML = "Veuillez saisir une date d'expiration !";
        x = false;
    } else {
        error_date_expir.innerHTML = ""; 
    }

   
    if (!dispo || !/^(Oui|Non)$/i.test(dispo)) {
        error_dispo.innerHTML = "La disponibilité doit être 'Oui' ou 'Non'!";
        x = false;
    } else {
        error_dispo.innerHTML = ""; 
    }

    if (!x) {
        event.preventDefault();
    }
});
