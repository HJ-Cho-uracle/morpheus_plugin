package m.client.ide.morpheus.core.config.webserver;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import m.client.ide.morpheus.core.constants.SettingConstants;
import m.client.ide.morpheus.core.utils.CommonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "m.client.ide.morpheus.core.config.webserver.WebserverSettingsState",
        storages = @Storage("CoreSettings.xml")
)

public class WebserverSettingsState implements PersistentStateComponent<WebserverSettingsState> {
    static final String 	URL_URACLE_UPDATE_SITE = "docs.morpheus.kr";
    static final String		PORT_URACLE_UPDATE_SITE = "80";
    static final String 	URL_MSDK_SITE = "docs.morpheus.kr";
    static final String		PORT_MSDK_SITE = "3690";
    static final String 	NETWORK_TIMEOUT = "3000";


    private String sChromePath;
    private boolean bAddComment;
    private String sSdkMode;
    private boolean bShowDebugMessage;
    private boolean bAskEmulatorVersion;
    private String sSdkPath;
    private String sUpdateIp;
    private String sUpdateSitePort;
    private String sMSdkIp;
    private String sMsdkPort;
    private String sTimeout;
    private String initialDataPath;


    @NotNull
    public static WebserverSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(WebserverSettingsState.class);
    }

    @Nullable
    @Override
    public WebserverSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull WebserverSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getUpdateSiteIp() {
        return Objects.requireNonNullElse(sUpdateIp, URL_URACLE_UPDATE_SITE);
    }

    public void setUpdateSiteIp(String ip) {
        this.sUpdateIp = ip;
    }

    public String getUpdateSitePort() {
        return Objects.requireNonNullElse(sUpdateSitePort, PORT_URACLE_UPDATE_SITE);
    }

    public void setUpdateSitePort(String stringPort) {
        this.sUpdateSitePort = stringPort;
    }

    public String getMSdkIp() {
        return Objects.requireNonNullElse(sMSdkIp, URL_MSDK_SITE);
    }

    public void setMSdkIp(String ip) {
        this.sMSdkIp = ip;
    }

    public String getMSdkPort() {
        return Objects.requireNonNullElse(sMsdkPort, PORT_MSDK_SITE);
    }

    public void setMSdkPort(String stringPort) {
        this.sMsdkPort = stringPort;
    }

    public String getNetworkTimeout() {
        return Objects.requireNonNullElse(sTimeout, NETWORK_TIMEOUT);
    }

    public void setNetworkTimeout(String milliSec) {
        this.sTimeout = milliSec;
    }

    public String getMSdkMode() {
        return Objects.requireNonNullElse(sSdkMode, SettingConstants.SDK_MODE_MORPHEUS);
    }

    public void setMSdkMode(String sdkMode) {
        this.sSdkMode = sdkMode;
    }
    public String getMSdkPath() {
        String sdkPath = CommonUtil.getPathString(CommonUtil.getAppDataLocation(), SettingConstants.MSDK_FD);

        if(isDevMode()) {
            if(sSdkPath != null && !sSdkPath.isEmpty())
                sdkPath = sSdkPath;
        }

        return sdkPath;
    }

    public void setMSdkPath(String sdkPath) {
        this.sSdkPath = sdkPath;
    }

    public boolean isShowDebugMessage() {
        return bShowDebugMessage;
    }

    public void setShowDebugMessage(boolean bShowDebugMessage) {
        this.bShowDebugMessage = bShowDebugMessage;
    }

    public boolean isAskEmulatorVersion() {
        return bAskEmulatorVersion;
    }

    public void setAskEmulatorVersion(boolean bAskEmulatorVersion) {
        this.bAskEmulatorVersion = bAskEmulatorVersion;
    }

    public String getChromePath() {
        return Objects.requireNonNullElse(sChromePath, "");
    }

    public void setChromePath(String sChromePath) {
        this.sChromePath = sChromePath;
    }

    public boolean isAddComment() {
        return bAddComment;
    }

    public void setAddComment(boolean bAddComment) {
        this.bAddComment = bAddComment;
    }

    public String getInitialDataPath() {
        return Objects.requireNonNullElse(this.initialDataPath, "");
    }

    public void setInitialDataPath(String dataPath) {
        this.initialDataPath = dataPath;
    }

    public boolean isDevMode() {
        return getMSdkMode().equals(SettingConstants.SDK_MODE_DEV);
    }
}

