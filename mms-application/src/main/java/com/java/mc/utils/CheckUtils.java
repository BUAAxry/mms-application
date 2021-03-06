package com.java.mc.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.thymeleaf.util.StringUtils;

import com.java.mc.bean.BatchJob;
import com.java.mc.bean.SendCondition;
import com.java.mc.bean.SendConditionOperation;

public class CheckUtils {
	
	public static Integer getHanderId(BatchJob batchJob, List<SendCondition> scList, Integer defaultHandlerId) throws Exception{

		if (scList != null && scList.size() > 0) {
			for (SendCondition sc : scList) {
				boolean result = false;

				if (sc.getOperationList() != null && sc.getOperationList().size() > 0) {
					for (SendConditionOperation sco : sc.getOperationList()) {
						if (checkOperation(sco, batchJob)) {
							result = true;
							if (sc.getFoundType()) {
								continue;
							}
							break;
						} else {
							result = false;
							if (sc.getFoundType()) {
								break;
							}
							continue;
						}
					}
				}

				if (result) {
					defaultHandlerId = sc.getHandlerId();
				}
			}
		}
		
		return defaultHandlerId;
	}
	
	private static boolean checkOperation(SendConditionOperation sco, BatchJob batchJob) throws Exception {
		if (sco == null || batchJob == null) {
			return false;
		}
		if (Constants.ACTION_MAIL_SCAN == batchJob.getActionType()) {
			return checkMSOperation(sco, batchJob);
		}
		if (Constants.ACTION_SM_SCAN == batchJob.getActionType()) {
			return checkSMOperation(sco, batchJob);
		}
		return false;
	}

	private static boolean checkMSOperation(SendConditionOperation sco, BatchJob batchJob) throws Exception {
		switch (sco.getOption()) {
		case 1:
			return CheckUtils.checkMSFromMail(sco, batchJob.getFromEmail(), batchJob.getToList());
		case 2:
			return CheckUtils.checkMSFromName(sco, batchJob.getFromName(), batchJob.getFromEmail());
		case 3:
			return CheckUtils.checkFromId(sco, batchJob.getFromId());
		case 4:
			return CheckUtils.checkMSTo(sco, batchJob.getTo(), batchJob.getToList(), batchJob.getFromEmail());
		case 5:
			return CheckUtils.checkSubject(sco, batchJob.getSubject());
		case 6:
			return CheckUtils.checkContent(sco, batchJob.getContent());
		case 7:
			return CheckUtils.checkAttachment(sco, batchJob.getAttachment());
		default:
			throw new Exception("不支持的判定条件");
		}
	}
	
	private static boolean checkSMOperation(SendConditionOperation sco, BatchJob batchJob) throws Exception {
		if (sco != null) {
			switch (sco.getOption()) {
			case 9:
				return CheckUtils.checkSMFromName(sco, batchJob.getFromName());
			case 10:
				return CheckUtils.checkFromId(sco, batchJob.getFromId());
			case 11:
				return CheckUtils.checkSMTo(sco, batchJob.getTo(), batchJob.getToList());
			case 12:
				return CheckUtils.checkSubject(sco, batchJob.getSubject());
			case 13:
				return CheckUtils.checkContent(sco, batchJob.getContent());
			case 14:
				return CheckUtils.checkAttachment(sco, batchJob.getAttachment());
			default:
				throw new Exception("不支持的判定条件");
			}
		}
		return false;
	}
	
	public static boolean checkSMFromName(SendConditionOperation sco, String fromName) throws Exception {
		return checkString(sco, fromName);
	}
	
	public static boolean checkMSFromMail(SendConditionOperation sco, String fromMail, String[] toList) throws Exception {
		switch (sco.getOperation()) {
		case 27:
			return StringUtils.endsWith(fromMail, new StringBuffer("@").append(sco.getValue()).toString());
		case 28:
			return !StringUtils.endsWith(fromMail, new StringBuffer("@").append(sco.getValue()).toString());
		case 29:
			return StringUtils.startsWith(fromMail, new StringBuffer(sco.getValue()).append("@").toString());
		case 30:
			return StringUtils.startsWith(fromMail, new StringBuffer(sco.getValue()).append("@").toString());
		case 31:
			try {
				new InternetAddress(fromMail, true);
			} catch (AddressException e) {
				return true;
			}
			return false;
		case 32:
			try {
				new InternetAddress(fromMail, true);
			} catch (AddressException e) {
				return false;
			}
			return true;
		case 33:
			if (toList != null) {
				for (String mail : toList) {
					if (StringUtils.equalsIgnoreCase(
							StringUtils.indexOf(StringUtils.toLowerCase(fromMail, Locale.getDefault()), "@"),
							StringUtils.indexOf(StringUtils.toLowerCase(mail, Locale.getDefault()), "@"))) {
						continue;
					}
					return false;
				}
			} else {
				return false;
			}
			return true;
		case 34:
			if (toList != null) {
				for (String mail : toList) {
					if (StringUtils.equalsIgnoreCase(
							StringUtils.indexOf(StringUtils.toLowerCase(fromMail, Locale.getDefault()), "@"),
							StringUtils.indexOf(StringUtils.toLowerCase(mail, Locale.getDefault()), "@"))) {
						continue;
					}
					return true;
				}
			}
			return false;
		default:
			return checkString(sco, fromMail);
		}
	}
	
	public static boolean checkSMTo(SendConditionOperation sco, String to, String[] toList) throws Exception{
		switch(sco.getOperation()){
		case 35:
			return ValidationUtils.phoneNumberValidate(to) == null;
		case 36:
			return ValidationUtils.phoneNumberValidate(to) != null;
		case 39:
			return toList != null && toList.length == 1;
		case 40:
			return toList != null && toList.length > 1;
		case 41:
			return toList != null && toList.length == sco.getVal();
		case 42:
			return toList != null && toList.length != sco.getVal();
		case 43:
			return toList != null && toList.length > sco.getVal();
		case 44:
			return toList != null && toList.length >= sco.getVal();
		case 45:
			return toList != null && toList.length < sco.getVal();
		case 46:
			return toList != null && toList.length <= sco.getVal();
		case 47:
			if (toList != null) {
				for (String phoneNumber : toList) {
					if (!(StringUtils.equalsIgnoreCase(phoneNumber, sco.getValue()))) {
						return false;
					}
				}
			}
			return true;
		case 48:
			if (toList != null) {
				for (String phoneNumber : toList) {
					if (!(StringUtils.equalsIgnoreCase(phoneNumber, sco.getValue()))) {
						return true;
					}
				}
			}
			return false;
		default:
			return checkString(sco, to);
		}
	}
	
	public static boolean checkMSTo(SendConditionOperation sco, String to, String[] toList, String fromMail)
			throws Exception {
		switch (sco.getOperation()) {
		case 27:
			if (toList != null) {
				for (String mail : toList) {
					String todomainName = StringUtils.substring(mail, StringUtils.indexOf(mail, "@"));
					if (!(StringUtils.equalsIgnoreCase(sco.getValue(), todomainName))) {
						return false;
					}
				}
			}
			return true;
		case 28:
			if (toList != null) {
				for (String mail : toList) {
					String todomainName = StringUtils.substring(mail, StringUtils.indexOf(mail, "@"));
					if (!(StringUtils.equalsIgnoreCase(sco.getValue(), todomainName))) {
						return true;
					}
				}
			}
			return false;
		case 29:
			if (toList != null) {
				for (String mail : toList) {
					String mailName = StringUtils.substring(mail, 0, StringUtils.indexOf(mail, "@"));
					if (!(StringUtils.equalsIgnoreCase(sco.getValue(), mailName))) {
						return false;
					}
				}
			}
			return true;
		case 30:
			if (toList != null) {
				for (String mail : toList) {
					String mailName = StringUtils.substring(mail, 0, StringUtils.indexOf(mail, "@"));
					if (!(StringUtils.equalsIgnoreCase(sco.getValue(), mailName))) {
						return true;
					}
				}
			}
			return false;
		case 31:
			try {
				new InternetAddress(to, true);
			} catch (AddressException e) {
				return true;
			}
			return false;
		case 32:
			try {
				new InternetAddress(to, true);
			} catch (AddressException e) {
				return false;
			}
			return true;
		case 33:
			if (toList != null) {
				for (String mail : toList) {
					if (StringUtils.equalsIgnoreCase(
							StringUtils.indexOf(StringUtils.toLowerCase(fromMail, Locale.getDefault()), "@"),
							StringUtils.indexOf(StringUtils.toLowerCase(mail, Locale.getDefault()), "@"))) {
						continue;
					}
					return false;
				}
			} else {
				return false;
			}
			return true;
		case 34:
			if (toList != null) {
				for (String mail : toList) {
					if (StringUtils.equalsIgnoreCase(
							StringUtils.indexOf(StringUtils.toLowerCase(fromMail, Locale.getDefault()), "@"),
							StringUtils.indexOf(StringUtils.toLowerCase(mail, Locale.getDefault()), "@"))) {
						continue;
					}
					return true;
				}
			}
			return false;
		case 39:
			return toList.length == 1;
		case 40:
			return toList.length > 1;
		case 41:
			return toList.length == sco.getVal();
		case 42:
			return toList.length != sco.getVal();
		case 43:
			return toList.length > sco.getVal();
		case 44:
			return toList.length >= sco.getVal();
		case 45:
			return toList.length < sco.getVal();
		case 46:
			return toList.length <= sco.getVal();
		case 47:
			if (toList != null) {
				for (String mail : toList) {
					if (StringUtils.equalsIgnoreCase(mail, sco.getValue())) {
						return true;
					}
				}
			}
			return false;
		case 48:
			if (toList != null) {
				for (String mail : toList) {
					if (StringUtils.equalsIgnoreCase(mail, sco.getValue())) {
						return false;
					}
				}
			}
			return true;
		default:
			return checkString(sco, to);
		}
	}
	
	public static boolean checkMSFromName(SendConditionOperation sco, String fromName, String fromMail) throws Exception {
		switch (sco.getOperation()) {
		case 37:
			return StringUtils.equals(fromMail, fromName);
		case 38:
			return !StringUtils.equals(fromMail, fromName);
		default:
			return checkString(sco, fromName);
		}
	}
	
	public static boolean checkFromId(SendConditionOperation sco, String fromId) throws Exception {
		return checkString(sco, fromId);
	}
	
	public static boolean checkSubject(SendConditionOperation sco, String subject) throws Exception {
		return checkString(sco, subject);
	}
	
	public static boolean checkContent(SendConditionOperation sco, String content) throws Exception {
		return checkString(sco, content);
	}
	
	public static boolean checkAttachment(SendConditionOperation sco, String attachment) throws Exception{
		switch(sco.getOperation()){
		case 54:
			return !StringUtils.isEmpty(attachment);
		case 55:
			return StringUtils.isEmpty(attachment);
		case 56:
			return (!StringUtils.isEmpty(attachment))
					&& StringUtils.endsWith(StringUtils.toLowerCase(attachment, Locale.getDefault()),
							StringUtils.concat(".", StringUtils.toLowerCase(sco.getValue(), Locale.getDefault())));
		case 57:
			return StringUtils.isEmpty(attachment) || (!(StringUtils.endsWith(
					StringUtils.toLowerCase(attachment, Locale.getDefault()),
					StringUtils.concat(".", StringUtils.toLowerCase(sco.getValue(), Locale.getDefault())))));
		default:
			throw new Exception("不支持的判定条件");
		}
	}
	
	public static boolean checkTime(SendConditionOperation sco, Timestamp dateTime) throws Exception{
		switch(sco.getOperation()){
		case 49: {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(dateTime.getTime());
			Calendar beginTime = Calendar.getInstance();
			beginTime.setTimeInMillis(sco.getBegin());
			return time.before(beginTime);
		}
		case 50: {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(dateTime.getTime());
			Calendar beginTime = Calendar.getInstance();
			beginTime.setTimeInMillis(sco.getBegin());
			return time.after(beginTime);
		}
		case 51: {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(dateTime.getTime());
			Calendar beginTime = Calendar.getInstance();
			beginTime.setTimeInMillis(sco.getBegin());
			Calendar endTime = Calendar.getInstance();
			endTime.setTimeInMillis(sco.getEnd());
			return time.after(beginTime) && time.before(endTime);
		}
		case 52: {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(dateTime.getTime());
			Calendar compareTime = Calendar.getInstance();
			compareTime.set(Calendar.HOUR_OF_DAY, 0);
			compareTime.set(Calendar.MINUTE, 0);
			compareTime.set(Calendar.SECOND, 0);
			compareTime.set(Calendar.MILLISECOND, 0);
			compareTime.add(Calendar.DAY_OF_YEAR, sco.getVal());
			return time.before(compareTime);
		}
		case 53: {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(dateTime.getTime());
			Calendar compareTime = Calendar.getInstance();
			compareTime.set(Calendar.HOUR_OF_DAY, 0);
			compareTime.set(Calendar.MINUTE, 0);
			compareTime.set(Calendar.SECOND, 0);
			compareTime.set(Calendar.MILLISECOND, 0);
			return time.before(compareTime);
		}
		default:
			throw new Exception("不支持的判定条件");
		}
	}

	public static boolean checkString(SendConditionOperation sco, String str) throws Exception {
		switch(sco.getOperation()){
		case 1:
			return sco.getCaseInsensitive() ? StringUtils.equalsIgnoreCase(str, sco.getValue())
					: StringUtils.equals(str, sco.getValue());
		case 2:
			return sco.getCaseInsensitive() ? !StringUtils.equalsIgnoreCase(str, sco.getValue())
					: !StringUtils.equals(str, sco.getValue());
		case 3:
			return StringUtils.isEmpty(str);
		case 4:
			return !StringUtils.isEmpty(str);
		case 5:
			return sco.getCaseInsensitive()
					? StringUtils.containsIgnoreCase(str, sco.getValue(), Locale.getDefault())
					: StringUtils.contains(str, sco.getValue());
		case 6:
			return sco.getCaseInsensitive()
					? !StringUtils.containsIgnoreCase(str, sco.getValue(), Locale.getDefault())
					: !StringUtils.contains(str, sco.getValue());
		case 7:
			return Pattern.matches("(.*?)\\d+(.*?)", str);
		case 8:
			return !Pattern.matches("(.*?)\\d+(.*?)", str);
		case 9:
			return Pattern.matches("\\d+", str);
		case 10:
			return StringUtils.startsWith(
					sco.getCaseInsensitive() ? StringUtils.toLowerCase(str, Locale.getDefault())
							: str,
					sco.getCaseInsensitive() ? StringUtils.toLowerCase(sco.getValue(), Locale.getDefault())
							: sco.getValue());
		case 11:
			return !StringUtils.startsWith(
					sco.getCaseInsensitive() ? StringUtils.toLowerCase(str, Locale.getDefault())
							: str,
					sco.getCaseInsensitive() ? StringUtils.toLowerCase(sco.getValue(), Locale.getDefault())
							: sco.getValue());
		case 12:
			return StringUtils.endsWith(
					sco.getCaseInsensitive() ? StringUtils.toLowerCase(str, Locale.getDefault())
							: str,
					sco.getCaseInsensitive() ? StringUtils.toLowerCase(sco.getValue(), Locale.getDefault())
							: sco.getValue());
		case 13:
			return !StringUtils.endsWith(
					sco.getCaseInsensitive() ? StringUtils.toLowerCase(str, Locale.getDefault())
							: str,
					sco.getCaseInsensitive() ? StringUtils.toLowerCase(sco.getValue(), Locale.getDefault())
							: sco.getValue());
		case 14:
			return StringUtils.length(str) == sco.getVal();
		case 15:
			return StringUtils.length(str) != sco.getVal();
		case 16:
			return StringUtils.length(str) > sco.getVal();
		case 17:
			return StringUtils.length(str) >= sco.getVal();
		case 18:
			return StringUtils.length(str) < sco.getVal();
		case 19:
			return StringUtils.length(str) <= sco.getVal();
		case 20:
			return Pattern.matches(sco.getValue(), str);
		default:
			throw new Exception("不支持的判定条件");
		}
	}
}
