$( function() {
	$( "#proceed" ).button().on( "click", function() {
		document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "";
		var passwd = document.forms["Login"]["passwd"].value;
		var cor = true;
		console.log(passwd);
		firebase.auth().signInWithEmailAndPassword("admin@admin.com", passwd).then(function(event) {
			console.log("HELOL+K");
			var user = firebase.auth().currentUser;
			if (user && cor) {
			  // User is signed in.
			  console.log(user.email);
			  document.cookie = "username=" + user.email;
			  window.location = "main.html";
			} else {
			  // No user is signed in.
			  document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "Wrong Password!";
			}
		}).catch(function(error) {
			var errorCode = error.code;
			var errorMessage = error.message;
			console.log(errorMessage);
			cor = false;
			document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "Wrong Password!";
			
		});
    });
});

/*$( function() {
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
			document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "Wrong Password!";
			
		}).then(function() {
			firebase.auth().onAuthStateChanged(function(user) {
				var user = firebase.auth().currentUser;
				if (user && cor) {
				  // User is signed in.
				  window.location = "main.html";
				} else {
				  // No user is signed in.
				  document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "Wrong Password!";
				}

				if (user && cor) {
					// User is signed in.
					window.location = "main.html";
				} else {
					// No user is signed in.
					document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "Wrong Password!";
				}
			});
		});
    });
});*/