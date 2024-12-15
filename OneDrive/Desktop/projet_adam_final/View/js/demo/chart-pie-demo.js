// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

// Fetch data from chart-pie-demo.php
fetch('http://localhost/projet_adam_final/Controller/fetch-pie-demo.php') // Replace with the correct PHP file path
  .then(response => response.json())
  .then(data => {
    // Extract labels and data from the fetched JSON
    var chartLabels = data.labels; // Labels fetched from the PHP backend
    var chartData = data.data; // Data fetched from the PHP backend

    // Check if chartLabels and chartData are valid
    if (chartLabels && chartData && chartLabels.length > 0 && chartData.length > 0) {
      // Create the Doughnut Chart
      var ctx = document.getElementById("myPieChart").getContext('2d'); // Ensure the context is properly fetched
      var myPieChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: chartLabels, // Dynamically set the labels
          datasets: [{
            data: chartData, // Dynamically set the data
            backgroundColor: [
              '#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b',  // Existing colors
              '#5a2d9b', '#ff9f43', '#f1c40f', '#e67e22', '#9b59b6',   // New colors
              '#3498db', '#2ecc71', '#1abc9c', '#16a085', '#27ae60',   // More colors
              '#8e44ad', '#2980b9', '#f39c12', '#d35400', '#c0392b',   // Additional shades
              '#9b59b6', '#2c3e50', '#34495e', '#95a5a6', '#7f8c8d'    // Neutral tones
            ],
            hoverBackgroundColor: [
              '#2e59d9', '#17a673', '#2c9faf', '#f4b619', '#d43a2d',  // Existing hover colors
              '#9b59b6', '#ff7f50', '#f39c12', '#f5a623', '#8e44ad',   // New hover colors
              '#3498db', '#1abc9c', '#16a085', '#27ae60', '#2ecc71',   // More hover colors
              '#f39c12', '#e74c3c', '#f1c40f', '#7f8c8d', '#95a5a6',   // Additional hover shades
              '#c0392b', '#2c3e50', '#34495e', '#8e44ad', '#ff6f61'    // Neutral hover tones
            ],
            hoverBorderColor: "rgba(234, 236, 244, 1)" // Keep the same border color for hover
            
          }],
        },
        options: {
          maintainAspectRatio: false,
          tooltips: {
            backgroundColor: "rgb(255,255,255)",
            bodyFontColor: "#858796",
            borderColor: '#dddfeb',
            borderWidth: 1,
            xPadding: 15,
            yPadding: 15,
            displayColors: false,
            caretPadding: 10,
          },
          legend: {
            display: true, // Display legend for clarity
            position: 'top', // Position of the legend
          },
          cutoutPercentage: 80, // Adjust the doughnut hole size
        },
      });
    } else {
      console.error("Invalid data fetched: labels or data array is empty.");
    }
  })
  .catch(error => console.error('Error fetching data:', error));
