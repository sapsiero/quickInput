import com.finweb.quickInput.*
import java.text.SimpleDateFormat

class NumberFormatFilters {

	def formatService
	
	def filters = {
		numberFilter(controller: '*', action: '(update|save)') {
			before = {
				def formats = []
				formatService.dateJavaFormat(request).each{ formats << new SimpleDateFormat(it) }
				def numberFormat = formatService.getDecimalFormat(request)
				
				params.each { key, value ->
					if (value == 'quickdate') {
						formats.each() { javaDateFormatInst ->
							try {
								params[key] = javaDateFormatInst.parse(params[key+"Date"])
							} catch (Exception e) {}
						}
					} else if (value == 'quicknumber') {
						try {
							params[key] = numberFormat.parse(params[key+"Number"])
						} catch (Exception e) {}
					}
				}
			}
		}
	}
	
}
