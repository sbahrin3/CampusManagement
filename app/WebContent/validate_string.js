// Shaiful, 25 Feb 2006
// In-fusion Solutions Sbn. Bhd.
// http://www.in-fusion.com.my
// This script provides functions that will check strings for illegal characters.

function checkStringOnly(data,warning) {
	var illegalChars = /[^\s\w]/;
	if (illegalChars.test(data)) {
		// illegal characters detected
		// therefore data is not valid
		alert(warning);
		return false;
	} else {
		return true;
	}
}

function checkName(data,warning) {
	var legalChars = /[^\s\w\-\.\@\']/;
	//var illegalNameChars = /[\<\>\,\;\:\\\/\"\~\!\#\$\%\^\&\*\?\|]/;
	if (legalChars.test(data)) {
		// illegal characters detected
		// therefore data is not valid
		alert(warning);
		return false;
	} else {
		return true;
	}
}

function checkAddress(data,warning) {
	var illegalChars = /[\\\"\~\@\$\%\^\*\?\|]/;
	if (data.match(illegalChars)) {
		// illegal characters detected
		// therefore data is not valid
		alert(warning);
		return false;
	} else {
		return true;
	}
}

function checkURL(data,warning) {
	var illegalChars = /[\(\)\<\>\,\;\\\"\[\]\~\!\@\#\$\^\*\'\|\`]/;
	if (data.match(illegalChars)) {
		// illegal characters detected
		// therefore data is not valid
		alert(warning);
		return false;
	} else {
		return true;
	}
}

// Excepts only numbers
function checkPhone(data,warning) {
	//var stripped = data.replace(/[\(\)\.\-\ ]/g, '');
	//strip out acceptable non-numeric characters
	var illegalChars = /[^\d\-\+]/;
	//if (isNaN(parseInt(stripped))) {
	if (illegalChars.test(data)) {
	   	alert(warning);
	   	return false;
	} else {
		return true;
	}
}

function checkYear(data,warning) {
	var regex = /^\d{4}$/; // the expression to except
	if (regex.test(data)) {
		return true;
	} else {
	   	alert(warning);
	   	return false;
	}
}

// Excepts only 5 numbers
function checkPostcode(data,warning) {
	var regex = /^\d{5}$/; // the expression to except
	if (regex.test(data)) {
		return true;
	} else {
	   	alert(warning);
	   	return false;
	}
}

function checkICNO(data,warning) {
	var icnoFilter = /^\d{6}\-\d{2}\-\d{4}$/; // the icno format
	
	if (!(icnoFilter.test(data))) { 
		// error = "Please enter a valid email address.\n";
	    alert(warning);
	    return false;
	}
}

function checkPassword (strng) {
	var error = "";
	if (strng == "") {
    	error = "You didn't enter a password.\n";
	}
    var illegalChars = /[\W_]/; // allow only letters and numbers
    if ((strng.length < 6) || (strng.length > 8)) {
       error = "The password is the wrong length.\n";
    }
    else if (illegalChars.test(strng)) {
      error = "The password contains illegal characters.\n";
    }
}

function checkEmailFormat(strng, warning) {
	var emailFilter = /^.+@.+\..{2,3}$/;
	if (!(emailFilter.test(strng))) { 
		// error = "Please enter a valid email address.\n";
	    alert(warning);
	    return false;
	}
}

function checkEmailChar(strng, warning) {
	var illegalChars = /[\(\)\<\>\,\;\:\\\/\"\[\]\#]/;
	if (strng.match(illegalChars)) {
	   	// error = "The email address contains illegal characters.\n";
	   	alert(warning);
		return false;
	}
	//return true;
}