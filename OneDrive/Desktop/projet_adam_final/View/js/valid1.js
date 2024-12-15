document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");
    const nomRecette = document.querySelector("input[name='nom_recette']");
    const nombreIng = document.querySelector("input[name='nombre_ing']");
    const instructionsRecette = document.querySelector("textarea[name='instructions_recette']");

    form.addEventListener("submit", function (event) {
        let errors = [];

        if (!nomRecette.value.trim() || nomRecette.value.length > 50) {
            errors.push("Le nom de la recette doit contenir entre 1 et 50 caractères.");
        }

        const nombreIngValue = parseInt(nombreIng.value, 10);
        if (isNaN(nombreIngValue) || nombreIngValue < 1 || nombreIngValue > 20) {
            errors.push("Le nombre d'ingrédients doit être compris entre 1 et 20.");
        }

        if (!instructionsRecette.value.trim() || instructionsRecette.value.length > 80) {
            errors.push("Les instructions doivent contenir entre 1 et 80 caractères.");
        }

        if (errors.length > 0) {
            event.preventDefault();
            const existingErrorContainer = document.querySelector(".error-messages");
            if (existingErrorContainer) {
                existingErrorContainer.remove();
            }
            const newErrorContainer = document.createElement("div");
            newErrorContainer.classList.add("alert", "alert-danger", "error-messages");
            errors.forEach(error => {
                const errorItem = document.createElement("p");
                errorItem.textContent = error;
                newErrorContainer.appendChild(errorItem);
            });
            document.body.appendChild(newErrorContainer);
            newErrorContainer.scrollIntoView({ behavior: "smooth" });
        }
    });
});
