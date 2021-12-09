// Shaiful Nizam, 15 May 2008
// This script provides functions that will check strings for illegal characters.

// str is valid if no pattern match.
function isStringValid(str) {
	var re = /[']/;
	var flag = re.test(str);
	
	if (flag) {
		// pattern match.
		return false;
	} else {
		// no pattern match.
		return true;
	}
}

// name is valid if no pattern matches.
function isNameValid(str) {
	var re = /[^\s\w`'@]/;
	var flag = re.test(str);
	//alert("data = "+str+", Flag ="+flag);
	if (flag) {
		// pattern match.
		return false;
	} else {
		// no pattern match.
		return true;
	}
}

// icno is valid if pattern matches.
// pattern: ######-##-#### where # are digits.
function isIcnoValid(str) {
	var re = /^\d{6}\-\d{2}\-\d{4}$/;
	var flag = re.test(str);

	if (flag) {
		// pattern match.
		return true;
	} else {
		// no pattern match.
		return false;
	}
}

// date is valid if pattern matches.
// pattern: str is made up of 2 digits only.
function isDateValid(str) {
	var re = /[\d]{1,2}$/;
	var flag = re.test(str);
	//alert("data = "+str+", Flag ="+flag);
	if (flag) {
		// pattern match.
		return true;
	} else {
		// no pattern match.
		return false;
	}
}

// year is valid if pattern matches.
// pattern: only for four charcter and are digits.
function isYearValid(str) {
	var re = /[\d]{4}$/;
	var flag = re.test(str);
	//alert("data = "+str+", Flag ="+flag);
	if (flag) {
		// pattern match.
		return true;
	} else {
		// no pattern match.
		return false;
	}
}

// postcode is valid if pattern matches.
function isPostcodeValid(str) {
	var re = /[\d]{5}$/;
	var flag = re.test(str);
	//alert("data = "+str+", Flag ="+flag);
	if (flag) {
		// pattern match.
		return true;
	} else {
		// no pattern match.
		return false;
	}
}

// Allow only numbers
function isNumberOnly(str) {
	var re = /^[\d]+$/;
	var flag = re.test(str);
	//alert("data = "+str+", Flag ="+flag);
	if (flag) {
	    // numbers only
	   	return true;
	} else {
		return false;
	}
}

function checkEmailFormat(str) {
	var emailFilter = /^.+@.+\..{2,3}$/;
	if (!(emailFilter.test(str))) { 
	    return false;
	} else {
	    return true;
	}
}

function isDateFormatValid(str) {
	var re = /^\d{2}\-\d{2}\-\d{4}$/;
	var flag = re.test(str);
	if (flag) {
		// pattern match.
		return true;
	} else {
		// no pattern match.
		return false;
	}
}