package m.client.ide.morpheus.core.constants;

import m.client.ide.morpheus.core.config.CoreSettingsState;

public class Const {

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String PATH_SEPARATOR = System.getProperty("path.separator");

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final String JAVA_HOME 	= System.getProperty("java.home");

	public static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");

	public static final String ENCODING_UTF_8 = "UTF-8";

	public static final String ENCODING_EUC_KR = "EUC-KR";

	public static final String EMPTY_STRING = "";

	public static final String ENTER_STRING = "\n";

	public static final String TAB_STRING = "\t";

	public static final String RANGE_SEPARATOR = "~";

	public static final String STRING_UNDERSCORE = "_";

	public static final String STRING_HYPHEN = "-";

	public static final String STRING_SLASH = "/";

	public static final String STRING_QUESTION 					= "?";
	
	public static final String PARAM_SEPARATOR 					= "&";

	public static final String VALUE_SEPARATOR 					= "=";

	public static final String VALUE_WRAPPER 					= "%";

	public static final String OS_NAME = System.getProperty("os.name");

	public static final String DBL_QUOTATION_MARK = "\"";

	public final static String SPACE_STRING							= " ";

	public final static String NOT_AVAILABLE_STRING					= "N/A";

	public final static String COLON_STRING							= ":";

	public final static String VAR_DELIMETER						= ";";

	public final static String CURRENT_DIRECTORY					= ".";

	public final static String DOT                                  = CURRENT_DIRECTORY;

	public final static String PARENT_DIRECTORY						= "..";

	public final static String COMMA_STRING							= ",";

	public final static String SINGLE_QUOTATION						= "'";

	public final static String BRACKET_OPEN							= "(";

	public final static String BRACKET_CLOSE						= ")"; 

	public static final String USER_HOME = System.getProperty("user.home");
	
	public static final String FD_UPDATE_3_0 = "/3.0/";
	public static final String FD_UPDATE_3_0_DEV = "/3.0_dev/";
			
	public static String addEndSlash(String url) {
		return url + (url.lastIndexOf(STRING_SLASH) == url.length() - 1 ? "" : STRING_SLASH);
	}

	public static String removeStartSlash(String url) {
		return url == null ? "" : (url.indexOf(STRING_SLASH) == 0 ? url.substring(1) : url);
	}
}