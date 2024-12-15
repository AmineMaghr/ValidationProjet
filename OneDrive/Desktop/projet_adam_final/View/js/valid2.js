document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');
    const nomPlatInput = document.querySelector('input[name="nom_plat"]');
    const prixPlatInput = document.querySelector('input[name="prix_plat"]');
    const urlImgInput = document.querySelector('input[name="url_img"]');
    const recetteSelect = document.querySelector('select[name="id_recette"]');

    form.addEventListener('submit', function (e) {
        let errors = [];

        // Validate "Nom Plat"
        if (nomPlatInput.value.trim() === '') {
            errors.push('Le nom du plat est obligatoire.');
        } else if (nomPlatInput.value.length > 50) {
            errors.push('Le nom du plat ne peut pas dépasser 50 caractères.');
        }

        // Validate "Prix Plat"
        if (prixPlatInput.value.trim() === '') {
            errors.push('Le prix du plat est obligatoire.');
        } else if (prixPlatInput.value <= 0 || prixPlatInput.value > 99) {
            errors.push('Le prix du plat doit être compris entre 1 et 99.');
        }

        // Validate "Recette"
        if (!recetteSelect.value) {
            errors.push('Veuillez sélectionner une recette.');
        }

        // Validate "URL IMG"
        if (urlImgInput.value.trim() === '') {
            errors.push('L\'URL de l\'image est obligatoire.');
        } else if (urlImgInput.value.length > 200) {
            errors.push('L\'URL de l\'image ne peut pas dépasser 200 caractères.');
        }

        // Handle Errors
        if (errors.length > 0) {
            e.preventDefault();
            const existingErrorMessages = document.querySelectorAll('.error-message');
            existingErrorMessages.forEach(message => message.remove()); // Clear existing errors
            
            errors.forEach(error => {
                const errorMessage = document.createElement('p');
                errorMessage.textContent = error;
                errorMessage.classList.add('alert', 'alert-danger', 'error-message');
                errorMessage.style.marginTop = '10px';
                form.prepend(errorMessage); // Place errors above the form
            });

            document.querySelector('.error-message').scrollIntoView({ behavior: 'smooth' });
        }
    });
});
