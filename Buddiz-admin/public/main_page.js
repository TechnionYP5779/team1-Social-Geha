$(function() {
	// If the user is not logged in, and tried to enter this page, he is emmediately redirected to the login page.
	const db = firebase.firestore();
	if (hashCodeGenerator(document.cookie) == "586086846") {
		// User is signed in.
		document.getElementById("overlay-back-loading").style.display = "none";
	} else {
		// No user is signed in.
		window.location = "index.html";
	}

    var dialog_add, dialog_search, dialog_delete, form_add, form_search, form_delete;

	// Regexps for input validation.
	var nameRegex = /^[A-Z][a-zA-Z]* ([A-Z][a-zA-Z]* *)+$/;
	var idRegex = /^\d{9}$/;
	var phoneRegex = /^\+972\-5[0-9]\-\d{7}$/;

	/**
	 * Checks if the string is compatible with the regex.
	 */
    function checkRegexp(obj, regexp) {
		return regexp.test(obj);
    }
	
	/**
	 * Hash function
	 */
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
	
	/**
	 * Generated the user code out of his name, kind and id - using several hash functions and encryption.
	 * @param name - the name of the user
	 * @param id - the id of the user
	 * @param kind - the kind of the user
	 * @param salt - a randomly chosen string
	 */
	function generateCode(name, id, kind, salt) {
		var encodedString = btoa(name.concat(salt, id, kind));
		var code = btoa(hashCodeGenerator(encodedString));
		code = code.substring(0, code.length - 1);
		return code;
	}
 
	/**
	 * Validates that all the fields were filled correctly by the user.
	 * @param name - the name of the user
	 * @param idNum - the id of the user
	 * @param phone - the phone number of the user
	 * @param kind - the kind of the user
	 * @param deps - an array that contains all the departments that the user was in.
	 */
	function checkFieldsCorrectness(name, idNum, phone, kind, deps) {
		var res = true;
		if (idNum == "" || !checkRegexp(idNum, idRegex)) {
			document.forms["AddUser"].getElementsByClassName("add_id_error")[0].innerHTML = "Please enter a 9 digit ID";
			res = false;
		}
		if (name == "" || !checkRegexp(name, nameRegex)) {
			document.forms["AddUser"].getElementsByClassName("add_name_error")[0].innerHTML = "Please enter a valid name (first name and surname)";
			res = false;
		}
		if (phone == "" || !checkRegexp(phone, phoneRegex)) {
			document.forms["AddUser"].getElementsByClassName("add_phone_error")[0].innerHTML = "Please enter a valid phone number (eg. +972-52-5555555)";
			res = false;
		}
		if (kind == "") {
			document.forms["AddUser"].getElementsByClassName("add_kind_error")[0].innerHTML = "Please choose a kind";
			res = false;
		}
		if (deps.length == 0) {
			document.forms["AddUser"].getElementsByClassName("add_dep_error")[0].innerHTML = "Please choose at least one department";
			res = false;
		}
		return res;
	}

	/**
	 * Validates that all the fields were filled correctly by the user in the edit form.
	 * @param name - the name of the user
	 * @param phone - the phone number of the user
	 */
	function checkEditFieldsCorrectness(name, phone) {
		var res = true;
		if (name !== "" && !checkRegexp(name, nameRegex)) {
			document.forms["EditUser"].getElementsByClassName("edit_name_error")[0].innerHTML = "Please enter a valid name (first name and surname)";
			res = false;
		}
		if (phone !== "" && !checkRegexp(phone, phoneRegex)) {
			document.forms["EditUser"].getElementsByClassName("edit_phone_error")[0].innerHTML = "Please enter a valid phone number (eg. +972-52-5555555)";
			res = false;
		}
		return res;
	}

	/**
	 * Cleans all the error messages in the 'add' form. To be used before each check.
	 */
	function cleanErrorMessagesAdd() {
		document.forms["AddUser"].getElementsByClassName("add_id_error")[0].innerHTML = "";
		document.forms["AddUser"].getElementsByClassName("add_name_error")[0].innerHTML = "";
		document.forms["AddUser"].getElementsByClassName("add_phone_error")[0].innerHTML = "";
		document.forms["AddUser"].getElementsByClassName("add_kind_error")[0].innerHTML = "";
		document.forms["AddUser"].getElementsByClassName("add_dep_error")[0].innerHTML = "";
	}

	/**
	 * Cleans all the error messages in the 'edit' form. To be used before each check.
	 */
	function cleanErrorMessagesEdit() {
		document.forms["EditUser"].getElementsByClassName("edit_id_error")[0].innerHTML = "";
		document.forms["EditUser"].getElementsByClassName("edit_name_error")[0].innerHTML = "";
		document.forms["EditUser"].getElementsByClassName("edit_phone_error")[0].innerHTML = "";
	}

	/**
	 * Adds a user to the DB.
	 * Reads the input from the form, checks its validity, and adds the user to the DB if everything is correct.
	 */
    function addUser() {
		cleanErrorMessagesAdd();
		var name = document.forms["AddUser"]["name"].value;
		var idNum = document.forms["AddUser"]["id_num"].value;
		var phone = document.forms["AddUser"]["phone"].value;
		var kind = document.forms["AddUser"]["kind"].value;
		var departments = [];
		if (document.forms["AddUser"]["adults-day"].checked) {
			departments.push("adults_day_care");
		}
		if (document.forms["AddUser"]["adults-open"].checked) {
			departments.push("adults_open_dep.");
		}
		if (document.forms["AddUser"]["adults-closed"].checked) {
			departments.push("adults_closed_dep.");
		}
		if (document.forms["AddUser"]["minor-day"].checked) {
			departments.push("minor_day_care");
		}
		if (document.forms["AddUser"]["minor-closed"].checked) {
			departments.push("minor_closed_dep.");
		}
		
		if (!checkFieldsCorrectness(name, idNum, phone, kind, departments)) return false;
        
        userCode = generateCode(name, "7", idNum, kind);
                        
		var docRef = db.collection('adminAddedUsers').doc(idNum);
	
		docRef.get().then(function(doc) {
			if (doc.exists) {
				document.getElementById("user_control_message").innerHTML = "A user with id " + idNum + " already exists!";
				document.getElementById("user_control_message").style.color = "red";
				dialog_add.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			}
			else{
				var setAda = docRef.set({
					name: name,
					id: idNum,
					phone: phone,
					kind:kind,
					departments:departments,
					user_code:userCode                 
				});
				document.getElementById("user_control_message").innerHTML = name + " was added successfully.<br/>" + name + "'s user code for Buddiz: " + userCode;
				document.getElementById("user_control_message").style.color = "#4cc113";
				dialog_add.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			}
		});
    }

	/**
	 * Edits a user in the DB.
	 * Reads the input from the edit form, checks its validity, and if something was changed, changes the corresponding firlds in the DB.
	 */
	function editUser() {
		cleanErrorMessagesEdit();
		var idNum = document.forms["EditUser"]["id_num"].value;
		if (idNum == "") {
			document.forms["EditUser"].getElementsByClassName("edit_id_error")[0].innerHTML = "Please enter a valid ID";
			return false;
		}
		var docRef = db.collection("adminAddedUsers").doc(idNum);
		docRef.get().then(function(doc) {
			if (doc.exists) {
				var nameBefore = doc.data().name;
				var changed = false;
				var name = document.forms["EditUser"]["name"].value;
				var phone = document.forms["EditUser"]["phone"].value;
				if (!checkEditFieldsCorrectness(name, phone)) return false;
				if (name !== ''){
					changed = true;
					docRef.update({
						name: name
					})
				}
				if (phone !== '' ){
					changed = true;
					docRef.update({
						phone: phone
					})
				}
				var kind = document.forms["EditUser"]["kind"].value;
				if (kind !== '' ){
					changed = true;
					docRef.update({
						kind: kind
					})
				}
				var departments = [];
				if (document.forms["EditUser"]["adults-day"].checked) {
					departments.push("adults_day_care");
				}
				if (document.forms["EditUser"]["adults-open"].checked) {
					departments.push("adults_open_dep.");
				}
				if (document.forms["EditUser"]["adults-closed"].checked) {
					departments.push("adults_closed_dep.");
				}
				if (document.forms["EditUser"]["minor-day"].checked) {
					departments.push("minor_day_care");
				}
				if (document.forms["EditUser"]["minor-closed"].checked) {
					departments.push("minor_closed_dep.");
				}
				if (departments.length != 0 ){
					changed = true;
					docRef.update({
						departments: departments
					})
				}
				if (!changed) {
					document.getElementById("user_control_message").innerHTML = "You didn't make any changes";
					document.getElementById("user_control_message").style.color = "blue";
				}
				else {
					var currentName = name !== "" ? name : nameBefore;
					document.getElementById("user_control_message").innerHTML = currentName  + " was edited successfully";
					document.getElementById("user_control_message").style.color = "#4cc113";
				}
				dialog_edit.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			} else {
				// doc.data() will be undefined in this case
				document.getElementById("user_control_message").innerHTML = "A user with id " + idNum + " does not exist";
				document.getElementById("user_control_message").style.color = "red";
				dialog_edit.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			}
		});
    }

	/**
	 * Finds a user that exists in the DB.
	 * Retrieves a user by her id number, and shows her details to the admin.
	 */
	function findUser() {
		var idNum = document.forms["FindUser"]["id_num"].value;
		document.forms["FindUser"].getElementsByClassName("user_data_search")[0].innerHTML = "";
		document.forms["FindUser"].getElementsByClassName("find_id_error")[0].innerHTML = "";
		if (idNum == "") {
			document.forms["FindUser"].getElementsByClassName("find_id_error")[0].innerHTML = "Please enter a valid ID";
			return false;
		}
        var docRef = db.collection("adminAddedUsers").doc(idNum);
        var deparments,name,id,phone,kind,departments,user_code;
        docRef.get().then(function(doc) {
			if (doc.exists) {
				deparments = doc.data().departments;
				name = doc.data().name;

				id = doc.data().idNum;
				phone = doc.data().phone;
				kind = doc.data().kind;
				departments = doc.data().departments;
				user_code = doc.data().user_code;
				kind = kind.split('_').join(' ');

				var deps_string = "";
				for (i = 0; i < departments.length - 1; i++) {
					deps_string += deparments[i].split('_').join(' ');
					deps_string += ", ";
				}
				deps_string += deparments[departments.length - 1].split('_').join(' ');

				document.forms["FindUser"].getElementsByClassName("user_data_search")[0].innerHTML = name + "'s phone number is: " + "<b>" + phone + "</b>.<br/>"
					+ "User code for Buddiz: " + "<b>" + user_code + "</b><br/>"
					+ name + " is a " + "<b>" + kind + "</b>.<br/>"
					+ name + " is able to help with " + "<b>" + deps_string + "</b><br/>";

			} else {
				// doc.data() will be undefined in this case
				document.forms["FindUser"].getElementsByClassName("user_data_search")[0].innerHTML = "No such user!";
				return true;
			}
	    }).catch(function(error) {
			console.log("Error getting document:", error);
		});
	}

	/**
	 * Deletes a user that exists in the DB by her id.
	 */
	function deleteUser() {
		var idNum = document.forms["DeleteUser"]["id_num"].value;
		if (idNum == "") {
			document.forms["DeleteUser"].getElementsByClassName("delete_id_error")[0].innerHTML = "Please enter a valid ID";
			return false;
		}
		
		var docRef = db.collection('adminAddedUsers').doc(idNum);
	
		docRef.get().then(function(doc) {
			if (doc.exists) {
				name = doc.data().name;
				db.collection("adminAddedUsers").doc(idNum).delete().then(function() {
					document.getElementById("user_control_message").innerHTML = name  + " was deleted successfully!";
					document.getElementById("user_control_message").style.color = "#4cc113";
				}).catch(function(error) {
					document.getElementById("user_control_message").innerHTML = "There was a problem while trying to delete " + name;
					document.getElementById("user_control_message").style.color = "red";
				});
			}
			else {
				document.getElementById("user_control_message").innerHTML = "The user " + idNum + " does not exist";
				document.getElementById("user_control_message").style.color = "red";
			}
		});
		dialog_delete.dialog( "close" );
		$('#overlay, #overlay-back').fadeOut(500);
	}
 
	/***************************************************************
							Forms - Dialogs
	***************************************************************/
 
    dialog_add = $( "#add-user-form" ).dialog({
		autoOpen: false,
		height: 400,
		width: 350,
		modal: true,
		buttons: {
			"Add this user": addUser,
			Cancel: function() {
				form_add[ 0 ].reset();
				dialog_add.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			}
		},
		close: function() {
			form_add[ 0 ].reset();
			$('#overlay, #overlay-back').fadeOut(500);
		}
    });
	
	dialog_edit = $( "#edit-user-form" ).dialog({
		autoOpen: false,
		height: 400,
		width: 350,
		modal: true,
		buttons: {
			"Edit this user's data": editUser,
			Cancel: function() {
				form_edit[ 0 ].reset();
				dialog_edit.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			}
		},
		close: function() {
			form_edit[ 0 ].reset();
			$('#overlay, #overlay-back').fadeOut(500);
		}
    });
	
	dialog_search = $( "#search-user-form" ).dialog({
		autoOpen: false,
		height: 400,
		width: 350,
		modal: true,
		buttons: {
			"Find this user": findUser,
			Cancel: function() {	
				form_search[ 0 ].reset();
				dialog_search.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			}
		},
		close: function() {
			form_search[ 0 ].reset();
			document.forms["FindUser"].getElementsByClassName("user_data_search")[0].innerHTML = "";
			$('#overlay, #overlay-back').fadeOut(500);
		}
    });
	
	dialog_delete = $( "#delete-user-form" ).dialog({
		autoOpen: false,
		height: 400,
		width: 350,
		modal: true,
		buttons: {
			"Delete this user": deleteUser,
			Cancel: function() {
				form_delete[ 0 ].reset();
				dialog_delete.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			}
		},
		close: function() {
			form_delete[ 0 ].reset();
			$('#overlay, #overlay-back').fadeOut(500);
		}
    });
 
    form_add = dialog_add.find( "form" ).on( "submit", function( event ) {
		event.preventDefault();
		addUser();
    });
	
	form_edit = dialog_edit.find( "form" ).on( "submit", function( event ) {
		event.preventDefault();
		editUser();
    });
	
	form_search = dialog_search.find( "form" ).on( "submit", function( event ) {
		event.preventDefault();
		findUser();
    });
	
	form_delete = dialog_delete.find( "form" ).on( "submit", function( event ) {
		event.preventDefault();
		deleteUser();
    });
 
 	/***********************************************************
						Buttons Functionality
	************************************************************/
 
    $( "#add-user" ).button().on( "click", function() {
		form_add[ 0 ].reset();
		cleanErrorMessagesAdd();
		document.getElementById("user_control_message").innerHTML = "";
		dialog_add.dialog( "open" );
		$('#overlay, #overlay-back').fadeIn(500);
    });
	
	$( "#edit-user" ).button().on( "click", function() {
		form_edit[ 0 ].reset();
		document.getElementById("user_control_message").innerHTML = "";
		dialog_edit.dialog( "open" );
		document.forms["EditUser"].getElementsByClassName("edit_id_error")[0].innerHTML = "";
		$('#overlay, #overlay-back').fadeIn(500);
    });
	
	$( "#find-user" ).button().on( "click", function() {
		form_search[ 0 ].reset();
		document.getElementById("user_control_message").innerHTML = "";
		document.forms["FindUser"].getElementsByClassName("user_data_search")[0].innerHTML = "";
		dialog_search.dialog( "open" );
		$('#overlay, #overlay-back').fadeIn(500);
    });
	
	$( "#delete-user" ).button().on( "click", function() {
		form_delete[ 0 ].reset();
		document.getElementById("user_control_message").innerHTML = "";
		document.forms["DeleteUser"].getElementsByClassName("delete_id_error")[0].innerHTML = "";
		dialog_delete.dialog( "open" );
		$('#overlay, #overlay-back').fadeIn(500);
    });
	
	$( "#logout" ).button().on( "click", function() {
		firebase.auth().signOut().then(function() {
			// Sign-out successful.
			document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
			window.location = "index.html";
		}).catch(function(error) {
			// An error happened.
		});
    });
});
