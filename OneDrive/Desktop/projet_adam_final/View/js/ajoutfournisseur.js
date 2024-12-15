var b = document.getElementById("ff");
b.addEventListener("click", function (event) {
    let x = true;

    const cin = document.getElementById("cin").value;
    const prenom = document.getElementById("prenom").value;
    const nom = document.getElementById("nom").value;
    const adresse = document.getElementById("adresse").value;
    const numero = document.getElementById("numero").value;
    const email = document.getElementById("email").value;

    // Disable fields initially
    prenom.disabled = true;
    nom.disabled = true;
    adresse.disabled = true;
    numero.disabled = true;
    email.disabled = true;

    // Clear all error messages initially
    document.getElementById("error_cin").innerHTML = "";
    document.getElementById("error_prenom").innerHTML = "";
    document.getElementById("error_nom").innerHTML = "";
    document.getElementById("error_adresse").innerHTML = "";
    document.getElementById("error_numero").innerHTML = "";
    document.getElementById("error_email").innerHTML = "";

    // CIN Validation
    if (!cin || !/^\d{8}$/.test(cin)) {
        document.getElementById("error_cin").innerHTML = "CIN doit être exactement 8 chiffres et ne doit pas être vide !";
        x = false;
    } else {
        document.getElementById("error_cin").innerHTML=""
        prenom.disabled = false;
    }

    // Prénom Validation
    if (!prenom || !/^[A-Za-z]+$/.test(prenom)) {
        document.getElementById("error_prenom").innerHTML = "Le prénom doit contenir uniquement des lettres et ne doit pas être vide !";
        x = false;
    } else {
        document.getElementById("error_prenom").innerHTML = "";  
        nom.disabled = false;
    }

    // Nom Validation
    if (!nom || !/^[A-Za-z]+$/.test(nom)) {
        document.getElementById("error_nom").innerHTML = "Le nom doit contenir uniquement des lettres et ne doit pas être vide !";
        x = false;
    } else {
        document.getElementById("error_nom").innerHTML = "";  
        adresse.disabled = false;
    }

    // Adresse Validation
    if (!adresse || adresse.length > 20 ||!/^[A-Za-z.,: ]+$/.test(adresse)) {
        document.getElementById("error_adresse").innerHTML = "L'adresse  ne doit pas dépasser 20 caractères et ne doit pas être vide !";
        x = false;
    } else {
        document.getElementById("error_adresse").innerHTML = "";  
        numero.disabled = false;
    }

    // Numéro Validation
    if (!numero || !/^\d{8}$/.test(numero)) {
        document.getElementById("error_numero").innerHTML = "Le numéro doit contenir exactement 8 chiffres et ne doit pas être vide !";
        x = false;
    } else {
        document.getElementById("error_numero").innerHTML = "";  
        email.disabled = false;
    }

    // Email Validation
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        document.getElementById("error_email").innerHTML = "Veuillez entrer une adresse email valide et ne doit pas être vide !";
        x = false;
    } else {
        document.getElementById("error_email").innerHTML = ""; 
    }

   
    if (!x) {
        event.preventDefault();
    }
});
