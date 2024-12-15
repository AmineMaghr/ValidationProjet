
    document.querySelectorAll('.rating-form').forEach(form => {
        const stars = form.querySelectorAll('.star');
        const ratingInput = form.querySelector('input[name="rating"]');
        const submitButton = form.querySelector('button[type="submit"]');

        stars.forEach(star => {
            star.addEventListener('click', function() {
                const rating = this.getAttribute('data-value');
                console.log("Selected Rating: ", rating); 
                ratingInput.value = rating; 
                stars.forEach(star => {
                    if (star.getAttribute('data-value') <= rating) {
                        star.classList.add('filled');
                        star.classList.remove('empty');
                    } else {
                        star.classList.add('empty');
                        star.classList.remove('filled');
                    }
                });

                submitButton.disabled = false;
            });
        });
    });

    function sortPlats() {
        const sortBy = document.getElementById('sortBy').value;
        const platsContainer = document.getElementById('platsContainer');
        const plats = Array.from(platsContainer.children);

        plats.sort((a, b) => {
            const aRating = parseFloat(a.getAttribute('data-rating'));
            const bRating = parseFloat(b.getAttribute('data-rating'));
            const aPrice = parseFloat(a.getAttribute('data-price'));
            const bPrice = parseFloat(b.getAttribute('data-price'));

            if (sortBy === 'ratingHighest') {
                return bRating - aRating;
            } else if (sortBy === 'ratingLowest') {
                return aRating - bRating;
            } else if (sortBy === 'priceLowest') {
                return aPrice - bPrice;
            } else if (sortBy === 'priceHighest') {
                return bPrice - aPrice;
            }
        });

        plats.forEach(plat => platsContainer.appendChild(plat));
    }