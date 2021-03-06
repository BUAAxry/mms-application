package com.java.mc.utils;

import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtils {
	private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

	/**
	 * email address string validation. support for a single email address or
	 * email address list separated by comma or semicolon.
	 * 
	 * @param addressString
	 *            string of the address that go to validation.
	 * @param serverType
	 *            server type
	 * @return valid address list separated by semicolon or signle email
	 *         address. if no valid address then return null.
	 */
	public static String mailAddressValidate(String addressString, int serverType) {
		if (addressString != null && addressString.trim().length() >= 3) {
			addressString = addressString.trim();
			addressString = addressString.replaceAll(",", ";");
			addressString = addressString.replaceAll(";+", ";");
			String[] parseArray = addressString.split(";");

			// check email address and compose the return string.
			StringBuffer returnString = new StringBuffer("");
			if (parseArray.length > 0) {
				for (String parseString : parseArray) {
					try {
						// validate email address. if valid, then nothing
						// happened. or throw AddressException
						if(parseString != null && parseString.trim().length() > 0){
							if (Constants.SERVER_TYPE_LOTUS == serverType) {
								new InternetAddress(parseString.trim(), false);
							} else {
								new InternetAddress(parseString.trim(), true);
							}
							returnString.append(parseString.trim()).append(",");
							continue;
						}
						logger.debug("The invalid mail address[{}] is ignored.", parseString);
					} catch (AddressException e) {
						logger.debug("The invalid mail address[{}] is ignored.", parseString);
					}
				}
			}
			if (returnString.length() > 1) {
				returnString.deleteCharAt(returnString.length() - 1);
				return returnString.toString();
			}
		}
		return null;
	}
	
	/**
	 * phone number string validation. support for a single phone number string or
	 * phone number list separated by comma or semicolon.
	 * 
	 * valid phone number : start with digital 1 and the length of the string is 11
	 * 
	 * @param phoneString phone number string
	 * @return valid phone number address or list separated by comma.
	 */
	public static String phoneNumberValidate(String phoneString) {
		if (phoneString != null && phoneString.trim().length() >= 3) {
			phoneString = phoneString.trim();
			phoneString = phoneString.replaceAll(",", ";");
			phoneString = phoneString.replaceAll(";+", ";");
			String[] parseArray = phoneString.split(";");

			// check phone number and compose the return string.
			StringBuffer returnString = new StringBuffer("");
			if (parseArray.length > 0) {
				for (String parseString : parseArray) {
					try {
						// validate phone number. if valid, then nothing
						// happened. or throw RuntimeException
						if(parseString != null && parseString.trim().length() > 0){
							//remove leading digital 0 and keep it is digital.
							parseString = String.valueOf(Long.valueOf(parseString.trim())).trim();	
							//remove 86 if necessary, only support Chinese mobile number.
							if(parseString.startsWith("86")){
								parseString = parseString.substring(2).trim();
							}
							//remove leading digital 0 if necessary.
							parseString = String.valueOf(Long.valueOf(parseString.trim())).trim();	
							//only support Chinese mobile number.
							if(Pattern.matches("1\\d{10}", parseString)){
								returnString.append(parseString.trim()).append(",");
								continue;
							}
						}
						logger.debug("The invalid phone number[{}] is ignored.", parseString);
					} catch (RuntimeException e) {
						logger.debug("The invalid phone number[{}] is ignored.", parseString);
					}
				}
			}
			if (returnString.length() > 1) {
				returnString.deleteCharAt(returnString.length() - 1);
				return returnString.toString();
			}
		}
		return null;
	}
}
