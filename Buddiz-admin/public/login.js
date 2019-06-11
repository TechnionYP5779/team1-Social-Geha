$( function() {
      <!--hide
  
      var password;
  
      var pass1="12345678";
  
      password=prompt('Please enter your password to view this page',' ');
      firebase.auth().signInWithEmailAndPassword("admin@admin.com", password).catch(function(error) {
            // Handle Errors here.
            var errorCode = error.code;
            var errorMessage = error.message;
            console.log(errorMessage);
            window.location="https://www.google.co.il/";
            // ...
      });
//      if (password !== pass1)
  
  
      //-->
  });
