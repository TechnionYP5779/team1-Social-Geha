$( function() {
	$( "#proceed" ).button().on( "click", function() {
		document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "";
		var passwd = document.forms["Login"]["passwd"].value;
		var cor = true;
		console.log(passwd);
		firebase.auth().signInWithEmailAndPassword("admin@admin.com", passwd).catch(function(error) {
			var errorCode = error.code;
			var errorMessage = error.message;
			console.log(errorMessage);
			cor = false;
			
		});
		firebase.auth().onAuthStateChanged(function(user) {
			if (user) {
				// User is signed in.
				window.location = "main.html";
			} else {
				// No user is signed in.
				document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "Wrong Password!";
			}
		});
    });
});