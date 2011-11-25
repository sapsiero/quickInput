/**
 * This is the quickInput central JS file. 
 */

/**
 * The registry stores information about dependencies between date fields
 */
var registry = {};

/**
 * CheckDate analyses the content of a field and sets it to an error, if necessary.
 * @param inputField The field that should be changed
 * @param formatSimple The simple format (e.g. MMddyyyy)
 * @param formatStandard The standard format (e.g. MM/dd/yyyy)
 * @param errorNode The node of the html that should show the error, in case of an error.
 * @param errorClass The name of the css class that should be added in case of an error.
 * @returns nothing
 */
function checkDate(inputField, formatSimple, formatStandard, errorNode, errorClass) {
	var content = inputField.value;
	var date=null;
	try {
		date = $.datepicker.parseDate(formatSimple, content);
	} catch (err) {
		try {
			date = $.datepicker.parseDate(formatStandard, content);
		} catch(err2) {
			if (errorNode.className.indexOf(errorClass) == -1)
				errorNode.className += ' ' + errorClass;
		}
	}
	if (date!=null) {
		var targetValue = $.datepicker.formatDate(formatStandard, date);
		inputField.value = targetValue;
		
		errorNode.className = errorNode.className.replace(errorClass, '');
		
		applyValue(inputField.id, targetValue);
	}
}

/**
 * Function to register an input element to another input element.
 * @param idSlave The input element that is dependent.
 * @param idMaster The input element which value changes should be propagated.
 * @returns nothing
 */
function register(idSlave, idMaster){
	if (!registry[idMaster])
		registry[idMaster] = [];

	registry[idMaster].push(idSlave);
}

/**
 * The function propagates the values from one field to another.
 * @param id The element's Id should be changed.
 * @param targetValue The value, the element should be set to.
 * @returns nothing
 */
function applyValue(id, targetValue) {
	var tmpreg = registry[id];

	if (tmpreg)
		for (var i = 0; i < tmpreg.length; i++) {
			var child = tmpreg[i];
			document.getElementById(child).value=targetValue;
			applyValue(child, targetValue);
		}
}

/**
 * Checks and formats the content of a field to a number.
 * @param inputField The field to be formated.
 * @param decimals The number of decimals (e.g. 6 for 12.121334 where 2 would return 12.12)
 * @param groupingCharacter Grouping character (e.g. for 1,232.23 the character is ',').
 * @param decimalCharacter Decimal character (e.g. for 1,232.23 the character is '.').
 * @param errorNode The node of the html that should show the error, in case of an error.
 * @param errorClass The name of the css class that should be added in case of an error.
 * @returns nothing
 */
function checkNumber(inputField, decimals, groupingCharacter, decimalCharacter, errorNode, errorClass) {
	var fieldContent = inputField.value;
	var number=null;
	try {
		number = parse(fieldContent, groupingCharacter, decimalCharacter);
	} catch (err) {
		if (errorNode.className.indexOf(errorClass) == -1)
			errorNode.className += ' ' + errorClass;
	}
	if (number!=null && !isNaN(number)) {
		var targetValue = format(number, decimals, groupingCharacter, decimalCharacter);
		inputField.value = targetValue;
		
		errorNode.className = errorNode.className.replace(errorClass,'');
	} else if (isNaN(number)) {
		if (errorNode.className.indexOf(errorClass) == -1)
			errorNode.className += ' ' + errorClass;
	}
}

/**
 * Internal function to format a number.
 * @param inputNumber Number that should be delivered in a particular format.
 * @param format Format string with '.' as decimal character and ',' as grouping character (e.g. '0,000.00')
 * @param groupingCharacter Grouping character (e.g. for 1,232.23 the character is ',').
 * @param decimalCharacter Decimal character (e.g. for 1,232.23 the character is '.').
 * @returns
 */
function formatNumber(inputNumber, format, groupingCharacter, decimalCharacter) { 
  var hasSeperator = -1 < format.indexOf(',');
  var precisionSplit = format.split('.'); 

  //process number precision 
  if (precisionSplit.length == 2) {
    // fix number precision
	inputNumber = inputNumber.toFixed(precisionSplit[1].length);
  }
  // error: too many delimiters
  else if (precisionSplit.length > 2) {
    throw('NumberFormatException: invalid format, formats should not have more than 1 period: ' + format);
  }
  // remove precision
  else {
	inputNumber = inputNumber.toFixed(0);
  } 

  // get the string now that precision is correct
  var numberString = inputNumber.toString();

  // format has comma, then compute commas
  if (hasSeperator) {
    // remove precision for computation
	var integer = numberString.split('.')[0];
	// output
    var parr = [];
    var integerLength = integer.length;
    var firstGroupLength = (integer.substr(0,1)=='-' ? integer.length - 1 : integer.length) % 3 || 3; // firstGroupLength cannot be ZERO or causes infinite loop 

	if (inputNumber < 0) {
		firstGroupLength = firstGroupLength + 1;
	}

    // break the number into chunks of 3 digits; first chunk may be less than 3
    for (var i = 0; i < integerLength; i += firstGroupLength) {
      //second, third, ... grouping length is always 3
      if (i != 0) {firstGroupLength = 3;}
      parr[parr.length] = integer.substr(i, firstGroupLength);
    } 

    // put chunks back together, separated by comma
    integer = parr.join(groupingCharacter); 

    // add the precision back in
    if (numberString.split('.').length > 1) {inputNumber = integer + decimalCharacter  + numberString.split('.')[1];}
  } 

  return inputNumber;
}

/**
 * Parses a value to a number value
 * @param inputValue Input string to be parsed.
 * @param groupingCharacter Grouping character (e.g. for 1,232.23 the character is ',').
 * @param decimalCharacter Decimal character (e.g. for 1,232.23 the character is '.').
 * @returns the parsed numeric value.
 */
function parse(inputValue, groupingCharacter, decimalCharacter) {
  var unified = inputValue.replace(/[groupingCharacter]/g,'').replace(/[decimalCharacter]/g,'.');
  var val = parseFloat(unified);
  return val;
}

/**
 * Formats a number using the given parameters:
 * @param inputValue Number to be formated.
 * @param decimalPrecision Decimal precision as number.
 * @param groupingCharacter Grouping character (e.g. for 1,232.23 the character is ',').
 * @param decimalCharacter Decimal character (e.g. for 1,232.23 the character is '.').
 * @returns A string containing the formated number.
 */
function format(inputValue, decimalPrecision, groupingCharacter, decimalCharacter) {
  if (decimalPrecision == null) { decimalPrecision = 2; }
  //create format string
  var dec = "";
  for (var i = 0; i < decimalPrecision; i++) {
  	dec += "0";
  }
  return formatNumber(inputValue, '0,000.'+dec, groupingCharacter, decimalCharacter);
}