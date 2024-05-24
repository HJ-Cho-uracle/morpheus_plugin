package m.client.ide.morpheus.core.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static String checkNull(String value) {
		return checkNull(value, "");
	}

	public static String checkNull(String value, String deaultValue) {
		return value == null || "null".equals(value) ? deaultValue : value;
	}

	public static String checkEmpty(String value, String defaultValue) {
		return checkNull(value).length() == 0 ? defaultValue : value;
	}

	public static boolean isEmpty(String value) {
		return checkEmpty(value, null) == null ? true : false;
	}

	public static boolean isIPVaild(@NotNull String ip) {
		return ip.matches("(((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))");
	}

	public static boolean isMACValid(@NotNull String mac) {
		return mac.matches("^([\\da-fA-F]{2}-){5}[\\da-fA-F]{2}$");
	}

	public static boolean isPortVaild(String port) {
		int value = Integer.parseInt(checkEmpty(port, "-1"));
		return value >= 1024 && value <= 65535;
	}

	public static boolean isFileNameValid(String fileName) {
		if( fileName == null ) {
			return false ;
		}
		Pattern pattern = Pattern.compile("[\\\\/:*?\"<>|]");
		Matcher matcher = pattern.matcher(fileName);
		while(matcher.find()) {
			return false;
		}
		return true;
	}

	public static boolean isValidWindowsFileName(String text) {
		Pattern pattern = Pattern.compile(
				"# Match a valid Windows filename (unspecified file system).          \n" +
						"^                                # Anchor to start of string.        \n" +
						"(?!                              # Assert filename is not: CON, PRN, \n" +
						"  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
						"    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
						"    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
						"  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
						"  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
						"  $                              # and end of string                 \n" +
						")                                # End negative lookahead assertion. \n" +
						"[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
						"[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
						"$                                # Anchor to end of string.            ", 
						Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
		Matcher matcher = pattern.matcher(text);
		boolean isMatch = matcher.matches();
		return isMatch;
	}

	public static @NotNull String wrapDoubleQuatation(String value) {
		return wrapDoubleQuatation(value, false);
	}

	public static @NotNull String wrapDoubleQuatation(String value, boolean addBackslash) {
		if(StringUtil.isEmpty(value)) {
			return "NULL";
		} else {
			StringBuffer result = new StringBuffer();
			result.append("\"");//$NON-NLS-1$
			result.append(value);
			if(addBackslash && value.endsWith("\\")) {//$NON-NLS-1$
				result.append("\\");//$NON-NLS-1$
			}
			result.append("\"");//$NON-NLS-1$
			return result.toString();
		}
	}

	public static @NotNull String wrapQuatation(String value) {
		if(StringUtil.isEmpty(value)) {
			return "NULL";
		} else {
			return "'"+value+"'"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public static boolean isJavaIdentifier(@NotNull String s) {
		if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
			return false;
		}
		for (int i=1; i<s.length(); i++) {
			if (!Character.isJavaIdentifierPart(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String convertStringToNumber(String str) {
		String result = str;
		if (isInteger(str)) {
			result = String.valueOf(Integer.parseInt(str));
		}
		else if (isDouble(str)) {
			result = String.valueOf(Double.parseDouble(str));
		}

		return result;
	}

	public static boolean isFile(String path) {
		if (isEmpty(path)) {
			return false;
		}

		File file = new File(path);
		return file.exists() && file.isFile();
	}
}
