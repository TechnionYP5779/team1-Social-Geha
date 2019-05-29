$( function() {
    var dialog_add, dialog_search, dialog_delete, form_add, form_search, form_delete,
      emailRegex = /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/,
      name = $( "#name" ),
      allFields = $( [] ).add( name );
 
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
 
    function addUser() {
      var valid = true;
      allFields.removeClass( "ui-state-error" );
 
      valid = valid && checkLength( name, "username", 3, 16 );
      valid = valid && checkLength( email, "email", 6, 80 );
      valid = valid && checkLength( password, "password", 5, 16 );
 
      valid = valid && checkRegexp( name, /^[a-z]([0-9a-z_\s])+$/i, "Username may consist of a-z, 0-9, underscores, spaces and must begin with a letter." );
      valid = valid && checkRegexp( email, emailRegex, "eg. ui@jquery.com" );
      valid = valid && checkRegexp( password, /^([0-9a-zA-Z])+$/, "Password field only allow : a-z 0-9" );
 
      if ( valid ) {
        $( "#users tbody" ).append( "<tr>" +
          "<td>" + name.val() + "</td>" +
          "<td>" + email.val() + "</td>" +
          "<td>" + password.val() + "</td>" +
        "</tr>" );
        dialog_add.dialog( "close" );
      }
      return valid;
    }
	
	function findUser() {
		var valid = true;
		if ( valid ) {
			$( "#users tbody" ).append( "<tr>" +
			  "<td>" + name.val() + "</td>" +
			  "<td>" + email.val() + "</td>" +
			  "<td>" + password.val() + "</td>" +
			"</tr>" );
			dialog_search.dialog( "close" );
		}
		return valid;
	}
	
	function deleteUser() {
		var valid = true;
		if ( valid ) {
			$( "#users tbody" ).append( "<tr>" +
			  "<td>" + name.val() + "</td>" +
			  "<td>" + email.val() + "</td>" +
			  "<td>" + password.val() + "</td>" +
			"</tr>" );
			dialog_delete.dialog( "close" );
		}
		return valid;
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
	
	$( "#find-user" ).button().on( "click", function() {
      dialog_search.dialog( "open" );
	  $('#overlay, #overlay-back').fadeIn(500);
    });
	
	$( "#delete-user" ).button().on( "click", function() {
      dialog_delete.dialog( "open" );
	  $('#overlay, #overlay-back').fadeIn(500);
    });
  });