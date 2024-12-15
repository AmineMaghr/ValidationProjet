<?php
<script>
  function sendmail() {
    console.log('Sending email...');

    // Get input values
    const email = document.getElementById('email').value;
    const message = document.getElementById('message').value;
    // Define parameters for EmailJS
    const parms = {
      email: email,
      message: message
    };

    // Use EmailJS to send email
    emailjs
      .send("service_h41o208", "template_fag9c2r", parms)
      .then(
        function(response) {
          // Success callback
          alert("Email sent successfully!");
          console.log("Success:", response);
        },
        function(error) {
          // Error callback
          alert("Failed to send email. Please try again.");
          console.error("Error:", error);
        }
      );
  }
</script>