document.addEventListener('DOMContentLoaded', function () {
    const input = document.getElementById('ingredient-input');
    const badgeContainer = document.getElementById('badge-container');
    const hiddenInput = document.getElementById('hidden-ingredients');

    let ingredients = hiddenInput.value ? hiddenInput.value.split(',').map(i => i.trim()) : [];

    function renderBadges() {
        badgeContainer.innerHTML = '';
        ingredients.forEach((ingredient, index) => {
            const badge = document.createElement('span');
            badge.className = 'badge badge-primary m-1';
            badge.innerHTML = `
                ${ingredient}
                <button type="button" class="close ml-1" aria-label="Close" data-index="${index}">
                    <span aria-hidden="true">&times;</span>
                </button>
            `;
            badgeContainer.appendChild(badge);
        });
        hiddenInput.value = ingredients.join(',');
    }

    input.addEventListener('keypress', function (e) {
        if (e.key === 'Enter' && input.value.trim() !== '') {
            e.preventDefault();
            ingredients.push(input.value.trim());
            input.value = '';
            renderBadges();
        }
    });

    badgeContainer.addEventListener('click', function (e) {
        if (e.target.closest('.close')) {
            const index = e.target.closest('.close').getAttribute('data-index');
            ingredients.splice(index, 1);
            renderBadges();
        }
    });

    renderBadges();
});
