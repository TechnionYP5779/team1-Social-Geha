$( function() {
	var firebaseConfig = {
	  apiKey: "AIzaSyBADn-vc_h9jP5njsQkPRiKmPu6uYwXk3Q",
	  authDomain: "team1-social-geha.firebaseapp.com",
	  databaseURL: "https://team1-social-geha.firebaseio.com",
	  projectId: "team1-social-geha",
	  storageBucket: "team1-social-geha.appspot.com",
	  messagingSenderId: "813662253318",
	  appId: "1:813662253318:web:b0186efd3138b553"
	};
	
	firebase.initializeApp(firebaseConfig);
	 
	const db = firebase.firestore();

    var dialog_add, dialog_search, dialog_delete, form_add, form_search, form_delete,
		emailRegex = /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/,
		name = $( "#name" ),
		allFields = $( [] ).add( name ),
		phoneNumber = $("#phone"),
		kind = $("#kind.value"),
		department = "",
		userCode = "";

    function updateTips( t ) {
      tips
        .text( t )
        .addClass( "ui-state-highlight" );
      setTimeout(function() {
        tips.removeClass( "ui-state-highlight", 1500 );
      }, 500 );
    }
 
    function checkLength( o, n, min, max ) {
      if ( o.val().length > max || o.val().length < min ) {
        o.addClass( "ui-state-error" );
        updateTips( "Length of " + n + " must be between " +
          min + " and " + max + "." );
        return false;
      } else {
        return true;
      }
    }
 
    function checkRegexp( o, regexp, n ) {
      if ( !( regexp.test( o.val() ) ) ) {
        o.addClass( "ui-state-error" );
        updateTips( n );
        return false;
      } else {
        return true;
      }
    }
	
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
	
	function generateCode(name, id, kind, salt) {
		var encodedString = btoa(name.concat(salt, id, kind));
		var code = btoa(hashCodeGenerator(encodedString));
		code = code.substring(0, code.length - 1);
		return code;
	}
 
	function checkFieldsCorrectness(name, idNum, phone, kind, deps) {
		var res = true;
		if (idNum == "") {
			document.forms["AddUser"].getElementsByClassName("add_id_error")[0].innerHTML = "Please enter a valid ID";
			res = false;
		}
		if (name == "") {
			document.forms["AddUser"].getElementsByClassName("add_name_error")[0].innerHTML = "Please enter a valid name";
			res = false;
		}
		if (phone == "") {
			document.forms["AddUser"].getElementsByClassName("add_phone_error")[0].innerHTML = "Please enter a valid phone number";
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
	
	function cleanErrorMessagesAdd() {
		document.forms["AddUser"].getElementsByClassName("add_id_error")[0].innerHTML = "";
		document.forms["AddUser"].getElementsByClassName("add_name_error")[0].innerHTML = "";
		document.forms["AddUser"].getElementsByClassName("add_phone_error")[0].innerHTML = "";
		document.forms["AddUser"].getElementsByClassName("add_kind_error")[0].innerHTML = "";
		document.forms["AddUser"].getElementsByClassName("add_dep_error")[0].innerHTML = "";
	}
 
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

				console.log(name);
				console.log(idNum);
				console.log(phone);
				console.log(kind);
				console.log(departments);
				console.log(userCode);
				document.getElementById("user_control_message").innerHTML = name + " was added successfully.<br/>" + name + "'s user code for Buddiz: " + userCode;
				document.getElementById("user_control_message").style.color = "#4cc113";
				dialog_add.dialog( "close" );
				$('#overlay, #overlay-back').fadeOut(500);
			}
		});
		

        // firebase.database().ref('adminAddedUsers/' + 55555).set({
            // companyName: "hello",
            // contact: "hello",
        // })
      // var valid = true;
      // allFields.removeClass( "ui-state-error" );
      //
      // valid = valid && checkLength( name, "username", 3, 16 );
      // valid = valid && checkLength( email, "email", 6, 80 );
      // valid = valid && checkLength( password, "password", 5, 16 );
      //
      // valid = valid && checkRegexp( name, /^[a-z]([0-9a-z_\s])+$/i, "Username may consist of a-z, 0-9, underscores, spaces and must begin with a letter." );
      // valid = valid && checkRegexp( email, emailRegex, "eg. ui@jquery.com" );
      // valid = valid && checkRegexp( password, /^([0-9a-zA-Z])+$/, "Password field only allow : a-z 0-9" );
      //
      // if ( valid ) {
      //   $( "#users tbody" ).append( "<tr>" +
      //     "<td>" + name.val() + "</td>" +
      //     "<td>" + email.val() + "</td>" +
      //     "<td>" + password.val() + "</td>" +
      //   "</tr>" );
      //   dialog_add.dialog( "close" );
      // }
      // return valid;
    }
	
	function editUser() {
      var idNum = document.forms["EditUser"]["id_num"].value;
	  if (idNum == "") {
			document.forms["EditUser"].getElementsByClassName("edit_id_error")[0].innerHTML = "Please enter a valid ID";
			return false;
	  }
      var docRef = db.collection("adminAddedUsers").doc(idNum);
      docRef.get().then(function(doc) {
      if (doc.exists) {
		var nameBefore = doc.data().name;
		console.log("Document data:", doc.data());
		var changed = false;
		var name = document.forms["EditUser"]["name"].value;
		if (name !== ''){
			changed = true;
			docRef.update({
				name: name
			})
		}
		var phone = document.forms["EditUser"]["phone"].value;
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
		console.log("changes: " + changed);
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
      }//).catch(function(error) {
         //      console.log("Error getting document:", error);
      //}
	  );
    }
	
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
            console.log("Document data:", doc.data());
            deparments = doc.data().departments;
		    name = doc.data().name;
            console.log(name);

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
        allFields.removeClass( "ui-state-error" );
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
        allFields.removeClass( "ui-state-error" );
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
        allFields.removeClass( "ui-state-error" );
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
        allFields.removeClass( "ui-state-error" );
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
  });
