package com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.context.providers.proposalproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.fieldassist.IContentProposal;

public class XSDProposal implements IContentProposal {

	private Map<String, XSDInfo> xsdMap;
	private String content;

	public XSDProposal(String content) {
		this.content = content;
		this.xsdMap = (getFilledXsdMap());
	}

	public XSDProposal() {
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public int getCursorPosition() {
		return 0;
	}

	@Override
	public String getLabel() {
		return xsdMap.get(content).getLabel();
	}

	@Override
	public String getDescription() {
		return xsdMap.get(content).getDescription();
	}

	/**
	 * Returns the initial hard filled map
	 * 
	 * @return map of content and information of xsd
	 */
	private Map<String, XSDInfo> getFilledXsdMap() {
		if (this.xsdMap == null) {
			xsdMap = new HashMap<String, XSDInfo>();

			xsdMap.put("boolean", new XSDInfo("boolean", "binary-valued logic legal literals {true, false, 1, 0}"));

			xsdMap.put("base64Binary", new XSDInfo("base64Binary - byte[]", "Base64-encoded arbitrary binary data"));
			xsdMap.put("hexBinary", new XSDInfo("hexBinary - byte[]",
					"Arbitrary hex-encoded binary data. Example, “0FB7” is a hex encoding for 16-bit int 4023 (binary 111110110111)"));

			xsdMap.put("anyURI", new XSDInfo("anyURI - String",
					"A Uniform Resource Identifier Reference (URI). Can be absolute or relative, and may have an optional fragment identifier"));
			xsdMap.put("language", new XSDInfo("language - String", "natural language identifiers [RFC 1766] Example: en, fr."));
			xsdMap.put("normalizedString", new XSDInfo("normalizedString - String", "White space normalized strings"));
			xsdMap.put("string", new XSDInfo("string - String", "Character strings in XML"));
			xsdMap.put("token", new XSDInfo("token - String", "Tokenized strings"));

			xsdMap.put("byte", new XSDInfo("byte", "127 to-128. Sign is omitted, “+” assumed. Example: -1, 0, 126, +100"));
			xsdMap.put(
					"decimal",
					new XSDInfo(
							"decimal - BigDecimal",
							"Arbitrary precision decimal numbers. Sign omitted, “+” is assumed. Leading and trailing zeroes are optional. If the fractional part is zero, the period and following zero(es) can be omitted."));
			xsdMap.put("double", new XSDInfo("double",
					"Double-precision 64-bit floating point type - legal literals {0, -0, INF, -INF and NaN} Example, -1E4, 12.78e-2, 12 and INF"));
			xsdMap.put("float", new XSDInfo("float",
					"32-bit floating point type - legal literals {0, -0, INF, -INF and NaN} Example, -1E4, 1267.43233E12, 12.78e-2, 12 and INF"));
			xsdMap.put("int", new XSDInfo("int", "2147483647 to -2147483648. Sign omitted, “+” is assumed. Example: -1, 0, 126789675, +100000"));
			xsdMap.put("long", new XSDInfo("long",
					"9223372036854775807 to - 9223372036854775808. Sign omitted, “+” assumed. Example: -1, 0, 12678967543233, +100000"));
			xsdMap.put("negativeInteger", new XSDInfo("negativeInteger - BigInteger",
					"Infinite set {...,-2,-1}. Example: -1, -12678967543233, -100000"));
			xsdMap.put("nonNegativeInteger", new XSDInfo("nonNegativeInteger - BigInteger",
					"Infinite set {0, 1, 2,...}. Sign omitted, “+” assumed. Example: 1, 0, 12678967543233, +100000"));
			xsdMap.put("nonPositiveInteger", new XSDInfo("nonPositiveInteger - BigInteger",
					"Infinite set {...,-2,-1,0}. Example: -1, 0, -126733, -100000"));
			xsdMap.put("positiveInteger", new XSDInfo("positiveInteger - BigInteger",
					"Infinite set {1, 2,...}. Optional “+” sign,. Example: 1, 12678967543233, +100000"));
			xsdMap.put("short", new XSDInfo("short", "32767 to -32768. Sign omitted, “+” assumed. Example: -1, 0, 12678, +10000"));
			xsdMap.put("unsignedByte", new XSDInfo("unsignedByte - short", "0 to 255. a finite-length Example: 0, 126, 100"));
			xsdMap.put("unsignedInt", new XSDInfo("unsignedInt - long", "0 to 4294967295 Example: 0, 1267896754, 100000"));
			xsdMap.put("unsignedLong", new XSDInfo("unsignedLong - BigInteger", "0 to 18446744073709551615. Example: 0, 12678967543233, 100000"));
			xsdMap.put("unsignedShort", new XSDInfo("unsignedShort - short", "0 to 65535. Example: 0, 12678, 10000"));

			xsdMap.put("date", new XSDInfo("date - XMLGregorianCalendar",
					"Calendar date.Format CCYY-MM-DD. Example, May the 31st, 1999 is: 1999-05-31."));
			xsdMap.put(
					"dateTime",
					new XSDInfo(
							"dateTime - XMLGregorianCalendar",
							"Specific instant of time. ISO 8601 extended format CCYY-MM-DDThh:mm:ss. Example, to indicate 1:20 pm on May the 31st, 1999 for Eastern Standard Time which is 5 hours behind Coordinated Universal Time (UTC): 1999-05-31T13:20:00-05:00"));
			xsdMap.put(
					"duration",
					new XSDInfo(
							"duration - Duration",
							"A duration of time. ISO 8601 extended format PnYn MnDTnH nMn S. Example, to indicate duration of 1 year, 2 months, 3 days, 10 hours, and 30 minutes: P1Y2M3DT10H30M. One could also indicate a duration of minus 120 days as: -P120D"));
			xsdMap.put("gDay", new XSDInfo("gDay - XMLGregorianCalendar", "Gregorian day. Example a day such as the 5th of the month is --0"));
			xsdMap.put("gMonth", new XSDInfo("gMonth - XMLGregorianCalendar", "Gregorian month. Example: May is --05--"));
			xsdMap.put("gMonthDay", new XSDInfo("gMonthDay - XMLGregorianCalendar", "Gregorian specific day in a month. Example: Feb 5 is --02-05."));
			xsdMap.put("gYear", new XSDInfo("gYear - XMLGregorianCalendar", "Gregorian calendar year. Example, year 1999, write: 1999"));
			xsdMap.put("gYearMonth", new XSDInfo("gYearMonth - XMLGregorianCalendar",
					"Specific gregorian month and year. Example, May 1999, write: 1999-05"));
			xsdMap.put(
					"time",
					new XSDInfo(
							"time - XMLGregorianCalendar",
							"An instant of time that recurs every day. Example, 1:20 pm for Eastern Standard Time which is 5 hours behind Coordinated Universal Time (UTC), write: 13:20:00- 05:00"));
			xsdMap.put("Name", new XSDInfo("Name - String", "XML Names"));
			xsdMap.put("NCName", new XSDInfo("NCName - String", "XML “non-colonized” Names"));
			xsdMap.put("QName", new XSDInfo("QName - QName", "XML qualified names"));
			xsdMap.put("anyType", new XSDInfo("anyType - Object", "Built-in Complex type definition of Ur-Type"));
			xsdMap.put("anySimpleType", new XSDInfo("anySimpleType - Object", "Built-in Simple type definition of Ur-Type"));
		}
		return xsdMap;
	}

	public String[] getXsdContentArray() {
		if (xsdMap == null) {
			xsdMap = getFilledXsdMap();
		}
		String[] array = new String[xsdMap.size()];
		return xsdMap.keySet().toArray(array);
	}

	public Map<String, XSDInfo> getXsdMap() {
		return xsdMap;
	}

}
