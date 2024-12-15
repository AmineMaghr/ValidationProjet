
    window.onload = function() {
        sortPlats();  // Sort by the selected option when the page loads
    };

    // Function to sort plats by rating or price
    function sortPlats() {
        const sortOption = document.getElementById('sortBy').value;  // Get selected sort option
        const platsContainer = document.getElementById('platsContainer');
        const plats = Array.from(platsContainer.getElementsByClassName('col-xl-3'));

        plats.sort((a, b) => {
            let valueA, valueB;

            // Get the correct value (rating or price) from data attributes
            if (sortOption.includes('rating')) {
                valueA = parseFloat(a.getAttribute('data-rating'));
                valueB = parseFloat(b.getAttribute('data-rating'));
            } else {
                valueA = parseFloat(a.getAttribute('data-price'));
                valueB = parseFloat(b.getAttribute('data-price'));
            }

            // Handle NaN values (in case of missing ratings or prices)
            if (isNaN(valueA)) valueA = 0;
            if (isNaN(valueB)) valueB = 0;

            // Sort in descending order for highest options, ascending for lowest
            if (sortOption === 'ratingHighest' || sortOption === 'priceHighest') {
                return valueB - valueA;  // Sort in descending order (highest first)
            } else {
                return valueA - valueB;  // Sort in ascending order (lowest first)
            }
        });

        // Append the sorted plats back to the container
        plats.forEach(plat => platsContainer.appendChild(plat));
    }
