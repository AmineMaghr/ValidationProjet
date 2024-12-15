function saveRecetteAsTxt(Recette) {
    const { name, instructions_recette} = Recette;
    const nombre_ing = document.getElementById('nombre_ing').innerText;
    const content = `Recette: ${name}\n\nNombre d'ingrédients: ${nombre_ing}\n\nInstructions:\n${instructions_recette}`;
    const blob = new Blob([content], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `${name}.txt`;
    link.click();
}
