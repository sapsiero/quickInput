package com.finweb.quickInput

import org.springframework.web.servlet.support.RequestContextUtils
import org.springframework.web.context.request.RequestContextHolder
import java.text.*


class FormatService {
	
	private def loc
	private def dateFormat = []

	def dateFormat(def request) {
		initFormats(request)
		return dateFormat
	}
	
	/**
	 * Returns the JavaScripts date formats for date fields
	 * @param request The HTTP Request
	 * @return Returns the JavaScripts date formats for date fields
	 */
	def dateJsFormat(def request) {
		return dateFormat(request)[2..3]
	}
	
	def dateJavaFormat(def request) {
		return dateFormat(request)[0..1]
	}
	
	private def initFormats(def request) {
		def _locale = RequestContextUtils.getLocale(request)
		if (!loc || loc != _locale)  {
			loc = _locale
			dateFormat = []
			dateFormat << DateFormat.getDateInstance(DateFormat.SHORT, _locale).toPattern().replace("yy", "yyyy")
			def tmp = ""
			dateFormat[0].each { letter -> tmp += (letter == 'y' || letter == 'M' || letter == 'd' ? letter : '')}
			dateFormat << tmp.replace('yyyy', 'yy')
			dateFormat << dateFormat[0].toLowerCase().replace('yy', 'y')
			dateFormat << dateFormat[1].toLowerCase().replace('yy', 'y')
		}
	}
	
	def getDecimalFormat(def request) {
		return DecimalFormat.getInstance(RequestContextUtils.getLocale(request))
	}
}
