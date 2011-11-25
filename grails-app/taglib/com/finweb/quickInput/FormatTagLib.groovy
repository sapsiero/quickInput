	package com.finweb.quickInput

import java.text.*

class FormatTagLib {
	static namespace = "qi"

	def formatService
	
	def dateInput = { attr ->
		def formats = formatService.dateJavaFormat(request)
		
		def javaDateFormatInst = new SimpleDateFormat(formats[0])
		
		if (attr.register)
			out << """<script type="text/javascript">register('${attr.name}', '${attr.register}');</script>"""
			
		out << "<input name='${attr.name}' type='hidden' value='quickdate'/>"
		
		def jsFormats = formatService.dateJsFormat(request)
		jsFormats = jsFormats.collect { it.encodeAsJavaScript() } 
		
		out << "<input id='${attr.name}' name='${attr.name}Date' type='text' onblur=\"checkDate(this, '${jsFormats[1]}', '${jsFormats[0]}', ${ (attr.errornode ? attr.errornode : 'this.parentNode') }, '${attr.errorclass}');\""
		
		if (attr.value != null)
			out << " value='${javaDateFormatInst.format(attr.value)}'"
		else
			out << " value='${javaDateFormatInst.format(new Date())}'"
		
		out << " />"
	} 
	
	def numberInput = { attr ->
		def format = formatService.getDecimalFormat(request)
		
		format.minimumFractionDigits = (attr.decimals ? Integer.parseInt(attr.decimals) : 2)
		format.maximumFractionDigits = (attr.decimals ? Integer.parseInt(attr.decimals) : 2)
		
		if (attr.function)
		out << """
			<script type="text/javascript">
				function ${attr.name}() {
					document.getElementById('${attr.name}').value = format(${(attr.function =~ /([a-zA-Z]+[0-9]*)/).replaceAll("parse(document.getElementById('\$1').value)")}, ${(attr.decimals ? attr.decimals : 2)}, '${format.decimalFormatSymbols.groupingSeparator.encodeAsJavaScript()}', '${format.decimalFormatSymbols.decimalSeparator.encodeAsJavaScript()}');
				};
			</script>
				"""
		
		if (attr.register) {
			out << """
				<script type="text/javascript">
					var element${attr.name};
					"""
			
			def check = out
			attr.register?.split(",").eachWithIndex {  registerTarget, i ->
				check << """
						element${attr.name} = document.getElementById('${registerTarget}');
						var event${attr.name}${i};
						event${attr.name}${i}   = (element${attr.name}.onblur) ? element${attr.name}.onblur : function () {};
						element${attr.name}.onblur = function () {event${attr.name}${i}(); ${attr.name}()};
						"""
			}
			out << """
				</script>
					"""
		}
		
		
		out << "<input name='${attr.name}' type='hidden' value='quicknumber'/>"
		out << "<input id='${attr.name}' name='${attr.name}Number' type='text' onblur=\"checkNumber(document.getElementById('${attr.name}')"
		
		
		if (attr.decimals)
			out << ", ${attr.decimals}"
		else
			out << ", 2"
		
		out << ", '${format.decimalFormatSymbols.groupingSeparator.encodeAsJavaScript()}'"
		out << ", '${format.decimalFormatSymbols.decimalSeparator.encodeAsJavaScript()}'"
		out << ", ${(attr.errorNode ? attr.errorNode : "document.getElementById('${attr.name}').parentNode")}"
		out << ", '${attr.errorclass}');\""
		
		def val = attr.value
		
		if (attr.value != null)
			out << " value=\"${format.format(format.parse(val.toString())).encodeAsHTML()}\""
		else
			out << " value='${format.format(0.0).encodeAsHTML()}'"
		
		out << " />"
	}
}
