$( function() {
	function hashCodeGenerator(string){
	    var hash = 0;
	    if (string.length == 0) return hash;
	    for (i = 0; i < string.length; i++) {
	        char = string.charCodeAt(i);
	        hash = ((hash<<5)-hash)+char;
	        hash = hash & hash; // Convert to 32bit integer
	    }
	    return hash;
	}
	
	$( "#proceed" ).button().on( "click", function() {
		document.forms["Login"].getElementsByClassName("passwd_error")[0].innerHTML = "";
		var passwd = document.forms["Login"]["passwd"].value;
		var cor = true;
		firebase.auth().signInWithEmailAndPassword("admin@admin.com", passwd).then(function(event) {
			var user = firebase.auth().currentUser;
			if (user && cor) {
				// User is signed in.
				var cookieval = hashCodeGenerator(user.email);
				document.cookie = "username=" + cookieval;
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