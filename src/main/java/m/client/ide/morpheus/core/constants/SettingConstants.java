package m.client.ide.morpheus.core.constants;

import org.jetbrains.annotations.NonNls;

/**
 * Constant definitions for plug-in preferences
 */
public class SettingConstants {
	public static final String SERVICE_ANDROID_SDKS = "com.android.tools.idea.sdk.AndroidSdks";
	public static final @NonNls String ANDROID_SDK_PATH_KEY = "android.sdk.path";
	public static final String SDK_MODE_MORPHEUS = "morpheus";
	public static final String SDK_MODE_DEV = "dev";
//	public static final String SDK_PATH = "sdkPath";
	public static final String SHOW_DEBUG_MESSAGE = "showDebugMessage";
	public static final String MSDK_FD = "resources";
	public static final String LICENSE_FD = "license";
	public static final String CMDLINETOOLS_FD = "cmdline-tools";
	
	
	public static final String JS_FILE_NAME = "templates.xml";
	public static final String P_TEMPLATE_FILE_DEFAULT = "<?xml version\\=\"1.0\" encoding\\=\"UTF-8\" standalone\\=\"no\"?><templates/>";

	public static final String P_MSDK_PATH = "msdkPath";

	public static final String P_SDK_MODE = "sdk_mode";
	public static final String IOS_DEVELOPER_CERTIFICATE = CoreConstants.IOS_DEVELOPMENT_TEAM_ID;
	public static final String P_UPDATE_SITE_IP = "uracle.update.site.ip";
	public static final String P_UPDATE_SITE_PORT = "uracle.update.site.port";
	public static final String P_MSDK_SITE_IP = "uracle.msdk.site.ip";
	public static final String P_MSDK_SITE_PORT = "uracle.msdk.site.port";
	public static final String P_NETWORK_TIMEOUT = "network_timeout";
	public static final String P_SVN_URL = "mdeploy.svn.url";
	public static final String P_SVN_USER = "mdeploy.svn.user";
	public static final String P_SVN_PASSWORD = "mdeploy.svn.password";

	public static String XCODE_HOME = "iphone_home_path";
	public static String APPLE_DEVELOPER_CRETIFICATE = "apple_developer_certificate";
	
	public static String LATEST_OPENED_EMERGENCY_VERSION = "LATEST_OPENED_EMERGENCY_VERSION";
	public static String LATEST_OPENED_IDE_VERSION = "LATEST_OPENED_IDE_VERSION";
	public static String NO_SHOW_EMERGENCY_STARTUP = "NO_SHOW_EMERGENCY_STARTUP";
	public static String NO_SHOW_IDE_STARTUP = "NO_SHOW_IDE_STARTUP";
}
