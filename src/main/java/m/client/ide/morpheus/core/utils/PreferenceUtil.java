package m.client.ide.morpheus.core.utils;

import com.android.prefs.AndroidLocation;
import com.android.tools.idea.sdk.AndroidSdkPathStore;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import m.client.ide.morpheus.core.config.CoreSettingsState;
import m.client.ide.morpheus.core.constants.SettingConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@SuppressWarnings("restriction")
public class PreferenceUtil {
    private static final Logger LOG = Logger.getInstance(PreferenceUtil.class);

    public static @NotNull String getAndroidToolLocation() {
        String androidExec = OSUtil.isWindows() ? "android.bat" : "android";
        String path = CommonUtil.getPathString(PreferenceUtil.getAndroidSDKLocation(), "tools", androidExec);

        return path;
    }

    public static @Nullable String getAndroidSDKLocation() {
        @SuppressWarnings("deprecation")
        PropertiesComponent component = PropertiesComponent.getInstance();
        String osSdkLocation = component.getValue(SettingConstants.ANDROID_SDK_PATH_KEY);
        if (osSdkLocation == null || !new File(osSdkLocation).isDirectory()) {
            osSdkLocation = AndroidSdkPathStore.getInstance().getAndroidSdkPath().toString();
        }

        // If there's no SDK location or it's not a valid directory,
        // there's nothing we can do. When this is invoked from run()
        // the checkSdkLocationAndId method call should display a dialog
        // telling the user to configure the preferences.
        if (osSdkLocation == null || !new File(osSdkLocation).isDirectory()) {
            LOG.debug(PreferenceUtil.class.toString() + ".getAndroidSDKLocation() : Sdk location is null!");
            return null;
        }

        return osSdkLocation;
    }

    /**
     * Android command line tools folder 리턴
     *
     * @return
     */
    private static @Nullable File getCmdLineFolder() throws AndroidLocation.AndroidLocationException {
        // TODO Auto-generated method stub
        String fd_cmdline = SettingConstants.CMDLINETOOLS_FD + File.separator;

        String osSdkLocation = getAndroidSDKLocation();
        if (osSdkLocation == null) {
            LOG.debug(PreferenceUtil.class.toString() + ".getCmdLineFolder() : Sdk location is null!");
            return null;
        }

//		File androidBat = FileOp.append(osSdkLocation, bin, CommonUtil.sdkManagerCmdName());
        File cmdlineFolder = CommonUtil.getPathFile(osSdkLocation, fd_cmdline);

        if (!cmdlineFolder.exists() || !cmdlineFolder.isDirectory()) {
            LOG.debug(PreferenceUtil.class.toString() + ".getCmdLineFolder() : Commandline folder is not exist!");
            return null;
        }

        return cmdlineFolder;
    }


    public static void openSettings(@Nullable Project project, String id) {
        openSettings(project, id, "");
    }

    public static void openSettings(@Nullable Project project, String id, String filter) {
        ShowSettingsUtilImpl.showSettingsDialog(project, id, filter);
    }

    public static boolean isValidJDK(String jdkPath) {
        boolean exist = false;
        File jdkDir = new File(jdkPath, "bin");
        if (jdkDir.isDirectory()) {
            File[] files = jdkDir.listFiles();
            for (File file : files) {
                if (file.getName().equals("javac.exe"))
                    exist = true;
            }
        }

        if (!exist) {
            return false;
        }
        return true;
    }


    /**
     * SDK 모드를 리턴한다.
     * PreferenceConstants.SDK_MODE_MORPHEUS / PreferenceConstants.SDK_MODE_DEV
     *
     * @return
     */
    public static String getSdkMode() {
        String mode = CoreSettingsState.getInstance().getMSdkMode();
        String sdkMode = SettingConstants.SDK_MODE_MORPHEUS;
        if (mode != null && mode.length() > 0) {
            sdkMode = mode;
        }
        return sdkMode;
    }

    public static boolean getShowDebugMessage() {
        // TODO Auto-generated method stub
        return CoreSettingsState.getInstance().isDevMode() && CoreSettingsState.getInstance().isShowDebugMessage();
    }

    public static boolean getShowCLIDebug() {
        return CoreSettingsState.getInstance().isDevMode() && CoreSettingsState.getInstance().isShowCLIDebug();
    }

    public static String getIOSExportDestination() {
        return CoreSettingsState.getInstance().getIOSExportDestination();
    }

    public static void setIOSExportDestination(String destination) {
        CoreSettingsState.getInstance().setIOSExportDestination(destination);
    }

    /**
     * iOS Developer Certificate 값을 리턴 (사용자 저장)
     * PreferenceConstants.APPLE_DEVELOPER_CRETIFICATE
     *
     * @return
     */
    public static @NotNull String getIOSDeveloperCertificate() {
        String certificate = CoreSettingsState.getInstance().getDeveloperCertificate();
        return certificate == null ? "" : certificate;
    }

    public static void setIOSDeveloperCertificate(String selectedItem) {
        CoreSettingsState.getInstance().setDeveloperCertificate(selectedItem);
    }

    static final String URL_URACLE_UPDATE_SITE = "docs.morpheus.kr";
    static final int PORT_URACLE_UPDATE_SITE = 80;
    static final String URL_MSDK_SITE = "docs.morpheus.kr";
    static final int PORT_MSDK_SITE = 3690;
    static final int NETWORK_TIMEOUT = 3000;

    /**
     * MethodName	: getUpdateSiteIP
     * ClassName	: PreferenceUtil
     * Comment		: 업데이트 사이트 IP 리
     * Author		: johyeongjin
     * Datetime		: Dec 1, 2022 2:56:47 PM
     *
     * @return String
     * @return Uracle Update Site IP
     */
    public static String getUpdateSiteIP() {
        String ip = CoreSettingsState.getInstance().getUpdateSiteIp();
        String defaultIp = URL_URACLE_UPDATE_SITE;
        if (ip != null && ip.length() > 0) {
            defaultIp = ip;
        }
        return defaultIp;
    }

    public static void setUpdateSiteIP(String ip) {
        CoreSettingsState.getInstance().setUpdateSiteIp(ip);
    }

    /**
     * MethodName	: getUpdateSitePort
     * ClassName	: PreferenceUtil
     * Comment		: 업데이트 사이트 Port : default 80
     * Author		: johyeongjin
     * Datetime		: Dec 1, 2022 3:49:02 PM
     *
     * @return int
     * @return Uracle Update Site Port
     */
    public static int getUpdateSitePort() {
        String port = CoreSettingsState.getInstance().getUpdateSitePort();
        int defaultPort = PORT_URACLE_UPDATE_SITE;
        if (port != null && port.length() > 0) {
            try {
                defaultPort = Integer.parseInt(port);
            } catch (NumberFormatException e) { /* Ignore exception */}
        }
        return defaultPort;
    }

    public static void setUpdateSitePort(String stringPort) {
        CoreSettingsState.getInstance().setUpdateSitePort(stringPort);
    }

    /**
     * MethodName	: getMSDKSiteIP
     * ClassName	: PreferenceUtil
     * Comment		:
     * Author		: johyeongjin
     * Datetime		: Dec 1, 2022 3:53:07 PM
     *
     * @return String
     * @return
     */
    public static String getMSDKSiteIP() {
        String ip = CoreSettingsState.getInstance().getMSdkIp();
        String defaultIp = URL_MSDK_SITE;
        if (ip != null && ip.length() > 0) {
            defaultIp = ip;
        }
        return defaultIp;
    }

    public static void setMSDKSiteIP(String ip) {
        CoreSettingsState.getInstance().setMSdkIp(ip);
    }

    /**
     * MethodName	: getMSDKSitePort
     * ClassName	: PreferenceUtil
     * Comment		:
     * Author		: johyeongjin
     * Datetime		: Dec 1, 2022 3:54:14 PM
     *
     * @return int
     * @return
     */
    public static int getMSDKSitePort() {
        String port = CoreSettingsState.getInstance().getMSdkPort();
        int defaultPort = PORT_MSDK_SITE;
        if (port != null && port.length() > 0) {
            try {
                defaultPort = Integer.parseInt(port);
            } catch (NumberFormatException e) { /* Ignore exception */}
        }
        return defaultPort;
    }

    public static void setMSDKSitePort(String stringPort) {
        CoreSettingsState.getInstance().setMSdkPort(stringPort);
    }

    /**
     * MethodName	: getNetworkTimeout
     * ClassName	: PreferenceUtil
     * Comment		:
     * Author		: johyeongjin
     * Datetime		: Dec 1, 2022 3:49:50 PM
     *
     * @return int
     * @return Network test timeout (millisec)
     */
    public static int getNetworkTimeout() {
        String timeout = CoreSettingsState.getInstance().getNetworkTimeout();
        int defaultTimeout = NETWORK_TIMEOUT;
        if (timeout != null && timeout.length() > 0) {
            try {
                defaultTimeout = Integer.parseInt(timeout);
            } catch (NumberFormatException e) { /* Ignore exception */}
        }
        return defaultTimeout;
    }

    public static void setNetworkTimeout(String milliSec) {
        CoreSettingsState.getInstance().setNetworkTimeout(milliSec);
    }

    public static void openPreference(Project project, Class configuableClazz) {
        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }

        ShowSettingsUtil.getInstance().showSettingsDialog(project, configuableClazz);
    }
}
