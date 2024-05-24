package m.client.ide.morpheus.core.utils;

public class OSUtil {

	private static String OS = System.getProperty("os.name").toLowerCase();

	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

	}

	public static boolean isSolaris() {

		return (OS.indexOf("sunos") >= 0);

	}
	
	public static String getCurrentOS() {
		return OS;
	}
	
	public static boolean is64() {
		String bit = System.getProperty("os.arch");
		if(bit.contains("_")) {
			int startIndex = bit.indexOf("_") + 1;
			if(bit.length() > startIndex) {
				String sub = bit.substring(startIndex, bit.length());
				return sub.equals("64");
			}
		}
		return false;
	}
}
