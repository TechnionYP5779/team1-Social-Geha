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
 
    function addUser() {
		var name = document.forms["AddUser"]["name"].value;
		var idNum = document.forms["AddUser"]["id_num"].value;
		var phone = document.forms["AddUser"]["phone"].value;
		var kind = document.forms["AddUser"]["kind"].value;
		var departments = [];
		if (document.forms["AddUser"]["adults-day"].checked) {
			departments.push("adults_day");
		}
		if (document.forms["AddUser"]["adults-open"].checked) {
			departments.push("adults_open");
		}
		if (document.forms["AddUser"]["adults-closed"].checked) {
			departments.push("adults_closed");
		}
		if (document.forms["AddUser"]["minor-day"].checked) {
			departments.push("minor_day");
		}
		if (document.forms["AddUser"]["minor-closed"].checked) {
			departments.push("minor_closed");
		}
		
        
        userCode = generateCode(name, "7", idNum, kind);
                        
		var docRef = db.collection('adminAddedUsers').doc(idNum);
	
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
		var name = document.forms["EditUser"]["name"].value;
		var idNum = document.forms["EditUser"]["id_num"].value;
		var phone = document.forms["EditUser"]["phone"].value;
		var kind = document.forms["EditUser"]["kind"].value;
		var departments = [];
		if (document.forms["EditUser"]["adults-day"].checked) {
			departments.push("adults_day");
		}
		if (document.forms["EditUser"]["adults-open"].checked) {
			departments.push("adults_open");
		}
		if (document.forms["EditUser"]["adults-closed"].checked) {
			departments.push("adults_closed");
		}
		if (document.forms["EditUser"]["minor-day"].checked) {
			departments.push("minor_day");
		}
		if (document.forms["EditUser"]["minor-closed"].checked) {
			departments.push("minor_closed");
		}
                        
		var docRef = db.collection('adminEditedUsers').doc(idNum);
	
		var setAda = docRef.set({
		  name: name,
		  id: idNum,
		  phone: phone,
          kind:kind,
          departments:departments        
		});
    }
	
	function findUser() {
		var idNum = document.forms["FindUser"]["id_num"].value;
		console.log(idNum);
	}
	
	function deleteUser() {
		var idNum = document.forms["DeleteUser"]["id_num"].value;
		console.log(idNum);
	}
 
    dialog_add = $( "#add-user-form" ).dialog({
      autoOpen: false,
      height: 400,
      width: 350,
      modal: true,
      buttons: {
        "Add this user": addUser,
        Cancel: function() {
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
          dialog_edit.dialog( "close" );
		  $('#overlay, #overlay-back').fadeOut(500);
        }
      },
      close: function() {
        form_add[ 0 ].reset();
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
          dialog_search.dialog( "close" );
		  $('#overlay, #overlay-back').fadeOut(500);
        }
      },
      close: function() {
        form_search[ 0 ].reset();
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
      dialog_add.dialog( "open" );
	  $('#overlay, #overlay-back').fadeIn(500);
    });
	
	$( "#edit-user" ).button().on( "click", function() {
      dialog_edit.dialog( "open" );
	  $('#overlay, #overlay-back').fadeIn(500);
    });
	
	$( "#find-user" ).button().on( "click", function() {
      dialog_search.dialog( "open" );
	  $('#overlay, #overlay-back').fadeIn(500);
    });
	
	$( "#delete-user" ).button().on( "click", function() {
      dialog_delete.dialog( "open" );
	  $('#overlay, #overlay-back').fadeIn(500);
    });
  });
