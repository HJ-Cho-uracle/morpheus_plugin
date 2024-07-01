package m.client.ide.morpheus.framework.eclipse.manifest;

import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.utils.XMLUtil;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class Manifest {
    private boolean useDirectView;
    private String log;
    private String lang;
    private String resourceBaseVersion;
    private String resourceTarget;
    private String resourceUpdateServer;
    private String resourceUpdateTrcode;
    private String resourceUpdateMode;
    private String startPageName;
    private String startPageOrient;
    private String startPageOrientTablet;
    private String defaultActionOrient;
    private String defaultActionOrientTablet;
    private String defaultActionAnimate;
    private String defaultActionIndicator;
    private String libraryExt;
    private String useHWAcceleration;
    private String useTheme;
    private String useYoutube;
    private ArrayList<String> libraryAddonList;
    private ArrayList<String> libraryPluginList;
    private ArrayList<ManifestNetwork> networkList;

    private boolean isUsePushPlugin;
    private Push push;
    private boolean isUsePreventionPlugin = false;
    private Prevention prevention;

    public Manifest() {
        setUseDirectView(false);
        setLog("y");
        setLang("ko");
        setResourceBaseVersion("000000");
        setResourceTarget("app");
        setStartPageName("www/html/index.html");
        setStartPageOrient("default");
        setStartPageOrientTablet("default");
        setDefaultActionOrient("default");
        setDefaultActionOrientTablet("default");
        setDefaultActionAnimate("default");
        setDefaultActionIndicator("n");
        setLibraryExt(Const.EMPTY_STRING);
        setLibraryAddonList(new ArrayList<String>());
        setLibraryPluginList(new ArrayList<String>());
        setNetworkList(new ArrayList<ManifestNetwork>());
        setResourceUpdateServer(Const.EMPTY_STRING);
        setResourceUpdateTrcode(Const.EMPTY_STRING);
        setResourceUpdateMode(Const.EMPTY_STRING);
        setUseHWAcceleration("n");
        setUseTheme("n");
        setUseYoutube("n");

        push = new Push();
        isUsePushPlugin = false;
        prevention = new Prevention();
        isUsePreventionPlugin = false;
    }

    public Manifest(boolean useDirectView, String log, String lang, String resourceBaseVersion, String resourceTarget,
                    String resourceUpdateServer, String resourceUpdateTrcode, String resourceUpdateMode, String startPageName,
                    String startPageOrient, String startPageOrientTablet, String defaultActionOrient,
                    String defaultActionOrientTablet, String defaultActionAnimate, String defaultActionIndicator,
                    String libraryExt, ArrayList<String> libraryAddonList, ArrayList<String> libraryPluginList,
                    ArrayList<ManifestNetwork> networkList,
                    String useHWAcceleration,
                    String useTheme,
                    String useYoutube,
                    Push push,
                    Prevention prevention) {

        this.useDirectView = useDirectView;
        this.log = log;
        this.lang = lang;
        this.resourceBaseVersion = resourceBaseVersion;
        this.resourceTarget = resourceTarget;
        this.resourceUpdateServer = resourceUpdateServer;
        this.resourceUpdateTrcode = resourceUpdateTrcode;
        this.resourceUpdateMode = resourceUpdateMode;
        this.startPageName = startPageName;
        this.startPageOrient = startPageOrient;
        this.startPageOrientTablet = startPageOrientTablet;
        this.defaultActionOrient = defaultActionOrient;
        this.defaultActionOrientTablet = defaultActionOrientTablet;
        this.defaultActionAnimate = defaultActionAnimate;
        this.defaultActionIndicator = defaultActionIndicator;
        this.libraryExt = libraryExt;
        this.libraryAddonList = libraryAddonList;
        this.libraryPluginList = libraryPluginList;
        this.networkList = networkList;
        this.useHWAcceleration = useHWAcceleration;
        this.useTheme = useTheme;
        this.useYoutube = useYoutube;
        this.push = push == null ? new Push() : push;
        this.prevention = prevention == null ? new Prevention() : prevention;
    }

    public String getDefaultActionAnimate() {
        return defaultActionAnimate;
    }

    public String getDefaultActionIndicator() {
        return defaultActionIndicator;
    }

    public String getDefaultActionOrient() {
        return defaultActionOrient;
    }

    public String getLang() {
        return lang;
    }

    public ArrayList<String> getLibraryAddonList() {
        return libraryAddonList;
    }

    public String getLibraryExt() {
        return libraryExt;
    }

    public ArrayList<String> getLibraryPluginList() {
        return libraryPluginList;
    }

    public String getLog() {
        return log;
    }

    public String getPushLog() {
        return push.getLog();
    }

    public void setPushLog(String pushLog) {
        push.setLog(pushLog);
    }

    public ManifestNetwork getManifestNetwork(String name) {
        for (ManifestNetwork _network : networkList) {
            if (name.equals(_network.getName())) {
                return _network;
            }
        }
        return null;
    }

    public ArrayList<ManifestNetwork> getNetworkList() {
        return networkList;
    }

    public String getResourceBaseVersion() {
        return resourceBaseVersion;
    }

    public String getResourceTarget() {
        return resourceTarget;
    }

    public String getResourceUpdateMode() {
        return resourceUpdateMode;
    }

    public String getResourceUpdateServer() {
        return resourceUpdateServer;
    }

    public String getResourceUpdateTrcode() {
        return resourceUpdateTrcode;
    }

    public String getStartPageName() {
        return startPageName;
    }

    public String getStartPageOrient() {
        return startPageOrient;
    }

    public boolean isUseDirectView() {
        return useDirectView;
    }

    public void setDefaultActionAnimate(String defaultActionAnimate) {
        this.defaultActionAnimate = defaultActionAnimate;
    }

    public void setDefaultActionIndicator(String defaultActionIndicator) {
        this.defaultActionIndicator = defaultActionIndicator;
    }

    public void setDefaultActionOrient(String defaultActionOrient) {
        this.defaultActionOrient = defaultActionOrient;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setLibraryAddonList(ArrayList<String> libraryAddonList) {
        this.libraryAddonList = libraryAddonList;
    }

    public void setLibraryExt(String libraryExt) {
        this.libraryExt = libraryExt;
    }

    public void setLibraryPluginList(ArrayList<String> libraryPluginList) {
        this.libraryPluginList = libraryPluginList;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setNetworkList(ArrayList<ManifestNetwork> networkList) {
        this.networkList = networkList;
    }

    public void setResourceBaseVersion(String resourceBaseVersion) {
        this.resourceBaseVersion = resourceBaseVersion;
    }

    public void setResourceTarget(String resourceTarget) {
        this.resourceTarget = resourceTarget;
    }

    public void setResourceUpdateMode(String resourceUpdateMode) {
        this.resourceUpdateMode = resourceUpdateMode;
    }

    public void setResourceUpdateServer(String resourceUpdateServer) {
        this.resourceUpdateServer = resourceUpdateServer;
    }

    public void setResourceUpdateTrcode(String resourceUpdateTrcode) {
        this.resourceUpdateTrcode = resourceUpdateTrcode;
    }

    public void setStartPageName(String startPageName) {
        this.startPageName = startPageName;
    }

    public void setStartPageOrient(String startPageOrient) {
        this.startPageOrient = startPageOrient;
    }

    public void setUseDirectView(boolean useDirectView) {
        this.useDirectView = useDirectView;
    }

    public String getStartPageOrientTablet() {
        return startPageOrientTablet;
    }

    public void setStartPageOrientTablet(String startPageOrientTablet) {
        this.startPageOrientTablet = startPageOrientTablet;
    }

    public String getUseHWAcceleration() {
        return useHWAcceleration;
    }

    public void setUseHWAcceleration(String useHWAcceleration) {
        this.useHWAcceleration = useHWAcceleration;
    }

    public String getUseTheme() {
        return useTheme;
    }

    public void setUseTheme(String useTheme) {
        this.useTheme = useTheme;
    }

    public String getDefaultActionOrientTablet() {
        return defaultActionOrientTablet;
    }

    public void setDefaultActionOrientTablet(String defaultActionOrientTablet) {
        this.defaultActionOrientTablet = defaultActionOrientTablet;
    }

    public String getUseYoutube() {
        return useYoutube;
    }

    public void setUseYoutube(String useYoutube) {
        this.useYoutube = useYoutube;
    }

    public String getPushReceiverVersion() {
        return push.getVersion();
    }

    public void setPushReceiverVersion(String pushReceiverVersion) {
        push.setVersion(pushReceiverVersion);
    }

    public String getPushReceiverServer() {
        return push.getServer();
    }

    public void setPushReceiverServer(String pushReceiverServer) {
        push.setServer(pushReceiverServer);
    }

    public String getPushReceiverTimeout() {
        return push.getTimeout();
    }

    public void setPushReceiverTimeout(String timeout) {
        push.setTimeout(timeout);
    }

    public String getPushReceiverGcmSenderId() {
        return push.getFcmSenderId();
    }

    public void setPushReceiverGcmSenderId(String pushReceiverGcmSenderId) {
        push.setFcmSenderId(pushReceiverGcmSenderId);
    }

    public String getPushReceiverAndroidPushType() {
        return push.getAndroidPushType();
    }

    public void setPushReceiverAndroidPushType(String pushReceiverAndroidPushType) {
        push.setAndroidPushType(pushReceiverAndroidPushType);
    }

    public String getPushUpnsAgentServiceType() {
        return push.getAgentServiceType();
    }

    public void setPushUpnsAgentServiceType(String pushUpnsAgentServiceType) {
        push.setAgentServiceType(pushUpnsAgentServiceType);
    }

    public String getPushUpnsAgentRestartInterval() {
        return push.getAgentRestartInterval();
    }

    public void setPushUpnsAgentRestartInterval(String pushUpnsAgentRestartInterval) {
        push.setAgentRestartInterval(pushUpnsAgentRestartInterval);
    }

    public String getPushUpnsReconnectInterval() {
        return push.getReconnectInterval();
    }

    public void setPushUpnsReconnectInterval(String reconnectInterval) {
        push.setReconnectInterval(reconnectInterval);
    }

    public String getPushUpnsReallocateInterval() {
        return push.getReallocateInterval();
    }

    public void setPushUpnsReallocateInterval(String reallocateInterval) {
        push.setReallocateInterval(reallocateInterval);
    }

    public String getPushUpnsRetryRegistCount() {
        return push.getRetryRegistCount();
    }

    public void setPushUpnsRetryRegistCount(String retryRegistCount) {
        push.setRetryRegistCount(retryRegistCount);
    }

    public boolean isUsePushPlugin() {
        return isUsePushPlugin;
    }

    public void setUsePushPlugin(boolean isUsePushPlugin) {
        this.isUsePushPlugin = isUsePushPlugin;
    }

    public String getPushPolicy() {
        return push.getPolicy();
    }

    public void setPushPolicy(String pushPolicy) {
        push.setPolicy(pushPolicy);
    }

    public String getPreventionServerAdress() {
        return prevention.getServerAdress();
    }

    public void setPreventionServerAdress(String preventionServerAdress) {
        prevention.setServerAdress(preventionServerAdress);
    }

    public String getPreventionRooting() {
        return prevention.getRooting();
    }

    public void setPreventionRooting(String preventionRooting) {
        prevention.setRooting(preventionRooting);
    }

    public String getPreventionAndroidHash() {
        return prevention.getAndroidHash();
    }

    public void setPreventionAndroidHash(String preventionAndroidHash) {
        prevention.setAndroidHash(preventionAndroidHash);
    }

    public String getPreventionAndroidVersion() {
        return prevention.getAndroidVersion();
    }

    public void setPreventionAndroidVersion(String preventionAndroidVersion) {
        prevention.setAndroidVersion(preventionAndroidVersion);
    }

    public String getPreventionIosHash() {
        return prevention.getIosHash();
    }

    public void setPreventionIosHash(String preventionIosHash) {
        prevention.setIosHash(preventionIosHash);
    }

    public String getPreventionIosVersion() {
        return prevention.getIosVersion();
    }

    public void setPreventionIosVersion(String preventionIosVersion) {
        prevention.setIosVersion(preventionIosVersion);
    }

    public boolean isUsePreventionPlugin() {
        return isUsePreventionPlugin;
    }

    public void setUsePreventionPlugin(boolean isUsePreventionPlugin) {
        this.isUsePreventionPlugin = isUsePreventionPlugin;
    }

    public String getSecurityIndexes() {
        return push.getSecurityIndexes();
    }

    public void setSecurityIndexes(String securityIndexes) {
        push.setSecurityIndexes(securityIndexes);
    }

    public String getUsePhoneNumber() {
        return push.getUsePhoneNumber();
    }

    public void setUsePhoneNumber(String usePhoneNumber) {
        push.setUsePhoneNumber(usePhoneNumber);
    }

    public String getUsePermission() {
        return push.getUsePermission();
    }

    public void setUsePermission(String usePermission) {
        push.setUsePermission(usePermission);
    }

    public Document toDocument() {
        Document doc = XMLUtil.getNewDocument();
        Element root = doc.createElement("manifest");

        root.setAttribute("useDirectView", String.valueOf(useDirectView));

        Element logElement = doc.createElement("log");
        logElement.setTextContent(getLog().toLowerCase());
        root.appendChild(logElement);

        Element langElement = doc.createElement("lang");
        langElement.setTextContent(getLang().toLowerCase());
        root.appendChild(langElement);

        Element resourceElement = doc.createElement("resource");
        Element resourceBaseVersionElement = doc.createElement("base_version");
        resourceBaseVersionElement.setTextContent(getResourceBaseVersion());
        resourceElement.appendChild(resourceBaseVersionElement);
        Element resourceTargetElement = doc.createElement("target");
        resourceTargetElement.setTextContent(getResourceTarget().toLowerCase());
        resourceElement.appendChild(resourceTargetElement);
        Element resourceUpdateElement = doc.createElement("update");
        Element resourceUpdateServerElement = doc.createElement("server");
        resourceUpdateServerElement.setTextContent(getResourceUpdateServer());
        resourceUpdateElement.appendChild(resourceUpdateServerElement);
        Element resourceUpdateTrCodeElement = doc.createElement("trcode");
        resourceUpdateTrCodeElement.setTextContent(getResourceUpdateTrcode());
        resourceUpdateElement.appendChild(resourceUpdateTrCodeElement);
        Element resourceUpdateModeElement = doc.createElement("mode");
        resourceUpdateModeElement.setTextContent(getResourceUpdateMode().toLowerCase());
        resourceUpdateElement.appendChild(resourceUpdateModeElement);
        resourceElement.appendChild(resourceUpdateElement);
        root.appendChild(resourceElement);

        Element startPageElement = doc.createElement("startpage");
        Element startPageNameElement = doc.createElement("name");
        startPageNameElement.setTextContent(getStartPageName());
        startPageElement.appendChild(startPageNameElement);
        Element startPageOrientElement = doc.createElement("orient");
        startPageOrientElement.setTextContent(getStartPageOrient().toLowerCase());
        startPageElement.appendChild(startPageOrientElement);
        Element startPageOrientTabletElement = doc.createElement("orient-tablet");
        startPageOrientTabletElement.setTextContent(getStartPageOrientTablet().toLowerCase());
        startPageElement.appendChild(startPageOrientTabletElement);
        root.appendChild(startPageElement);


        Element settingsElement = doc.createElement("settings");
        root.appendChild(settingsElement);

        //Push Plugin 설정
        if (isUsePushPlugin()) {
            Element pushElement = createPushElement(doc);
            settingsElement.appendChild(pushElement);
        }

        //Prevention Plugin 설정
        if (isUsePreventionPlugin()) {
            Element preventionElement = createPreventionElement(doc);
            settingsElement.appendChild(preventionElement);
        }

        Element defaultActionElement = doc.createElement("default_action");
        Element defaultActionOrientElement = doc.createElement("orient");
        defaultActionOrientElement.setTextContent(getDefaultActionOrient().toLowerCase());
        defaultActionElement.appendChild(defaultActionOrientElement);
        Element defaultActionTabletElement = doc.createElement("orient-tablet");
        defaultActionTabletElement.setTextContent(getDefaultActionOrientTablet().toLowerCase());
        defaultActionElement.appendChild(defaultActionTabletElement);

        Element defaultActionAnimateElement = doc.createElement("animate");
        defaultActionAnimateElement.setTextContent(getDefaultActionAnimate().toLowerCase());
        defaultActionElement.appendChild(defaultActionAnimateElement);
        Element defaultActionIndicatorElement = doc.createElement("indicator");
        defaultActionIndicatorElement.setTextContent(getDefaultActionIndicator().toLowerCase());
        defaultActionElement.appendChild(defaultActionIndicatorElement);
        root.appendChild(defaultActionElement);

        Element libraryElement = doc.createElement("library");
        Element libraryExtElement = doc.createElement("ext");
        libraryExtElement.setTextContent(libraryExt);
        libraryElement.appendChild(libraryExtElement);
        Element libraryAddonsElement = doc.createElement("addons");
        for (String addon : libraryAddonList) {
            Element pathElement = doc.createElement("path");
            pathElement.setTextContent(addon);
            libraryAddonsElement.appendChild(pathElement);
        }
        libraryElement.appendChild(libraryAddonsElement);
        Element libraryPluginsElement = doc.createElement("plugins");
        for (String plugin : libraryPluginList) {
            Element pathElement = doc.createElement("path");
            pathElement.setTextContent(plugin);
            libraryPluginsElement.appendChild(pathElement);
        }
        libraryElement.appendChild(libraryPluginsElement);
        root.appendChild(libraryElement);

        Element networkElement = doc.createElement("network");
        Element networkHttpElement = doc.createElement("http");
        Element networkSocketElement = doc.createElement("socket");
        for (ManifestNetwork network : networkList) {
            Element networkItemElement = doc.createElement(network.getName());
            Element pathElement = doc.createElement("path");
            pathElement.setTextContent(network.getPath());
            networkItemElement.appendChild(pathElement);
            Element addressElement = doc.createElement("address");
            addressElement.setTextContent(network.getAddress());
            networkItemElement.appendChild(addressElement);
            Element timeoutElement = doc.createElement("timeout");
            timeoutElement.setTextContent(network.getTimeout());
            networkItemElement.appendChild(timeoutElement);
            Element encodingElement = doc.createElement("encoding");
            encodingElement.setTextContent(network.getEncoding().toLowerCase());
            networkItemElement.appendChild(encodingElement);

            if (network instanceof ManifestHttpNetwork) {
                ManifestHttpNetwork httpNetwork = (ManifestHttpNetwork) network;

                Element typeElement = doc.createElement("type");
                typeElement.setTextContent(httpNetwork.getType().toString());
                networkItemElement.appendChild(typeElement);
                networkHttpElement.appendChild(networkItemElement);
            } else {
                ManifestSocketNetwork socketNetwork = (ManifestSocketNetwork) network;

                Element portElement = doc.createElement("port");
                portElement.setTextContent(socketNetwork.getPort());
                networkItemElement.appendChild(portElement);
                networkSocketElement.appendChild(networkItemElement);
            }
        }
        networkElement.appendChild(networkHttpElement);
        networkElement.appendChild(networkSocketElement);
        root.appendChild(networkElement);

        Element applicationElement = doc.createElement("application");
        Element androidElement = doc.createElement("android");
        Element useHWAccElement = doc.createElement("hardwareaccelerated");
        useHWAccElement.setTextContent(getUseHWAcceleration());
        Element useThemeElement = doc.createElement("user-theme");
        useThemeElement.setTextContent(getUseTheme());
        Element useYoutubeElement = doc.createElement("use-youtube");
        useYoutubeElement.setTextContent(getUseYoutube());

        androidElement.appendChild(useHWAccElement);
        androidElement.appendChild(useThemeElement);
        androidElement.appendChild(useYoutubeElement);

        applicationElement.appendChild(androidElement);
        root.appendChild(applicationElement);


        doc.appendChild(root);

        return doc;
    }

    /**
     * 푸시 플러그인 설정 엘리먼트 생성
     *
     * @param doc
     * @return
     */
    public Element createPushElement(Document doc) {
        Element pushElement = doc.createElement("push");

        Element receiverElement = doc.createElement("receiver");
        pushElement.appendChild(receiverElement);

        if (push == null) {
            return pushElement;
        }

        Element receiverPageElement = doc.createElement("log");
        receiverPageElement.setTextContent(push.getLog());
        receiverElement.appendChild(receiverPageElement);

        Element securityIndexesElement = doc.createElement("security-indexes");
        securityIndexesElement.setTextContent(push.getSecurityIndexes());
        receiverElement.appendChild(securityIndexesElement);

        Element receiverVersionElement = doc.createElement("version");
        receiverVersionElement.setTextContent(push.getVersion());
        receiverElement.appendChild(receiverVersionElement);

        Element receiverServerElement = doc.createElement("server");
        receiverServerElement.setTextContent(push.getServer());
        receiverElement.appendChild(receiverServerElement);

        Element receiverTimeoutElement = doc.createElement("timeout");
        receiverTimeoutElement.setTextContent(push.getTimeout());
        receiverElement.appendChild(receiverTimeoutElement);

        Element policyElement = doc.createElement("policy");
        policyElement.setTextContent(push.getPolicy());
        receiverElement.appendChild(policyElement);

        Element receiverFcmSenderIdElement = doc.createElement("fcm-sender-id");
        receiverFcmSenderIdElement.setTextContent(push.getFcmSenderId());
        receiverElement.appendChild(receiverFcmSenderIdElement);

        Element receiverAndroidPushTypeElement = doc.createElement("android-push-type");
        receiverAndroidPushTypeElement.setTextContent(push.getAndroidPushType());
        receiverElement.appendChild(receiverAndroidPushTypeElement);

        Element usePhoneNumberElement = doc.createElement("use-phone_number");
        usePhoneNumberElement.setTextContent(push.getUsePhoneNumber());
        receiverElement.appendChild(usePhoneNumberElement);

        Element usePermissionElement = doc.createElement("use-permission");
        usePermissionElement.setTextContent(push.getUsePermission());
        receiverElement.appendChild(usePermissionElement);

        Element upnsElement = doc.createElement("upns");
        pushElement.appendChild(upnsElement);

        Element upnsServiceTypeElement = doc.createElement("agent-service-type");
        upnsServiceTypeElement.setTextContent(push.getAgentServiceType());
        upnsElement.appendChild(upnsServiceTypeElement);

        Element upnsRestartIntervalElement = doc.createElement("agent-restart-interval");
        upnsRestartIntervalElement.setTextContent(push.getAgentRestartInterval());
        upnsElement.appendChild(upnsRestartIntervalElement);

        Element reconnectIntervalElement = doc.createElement("reconnect-interval");
        reconnectIntervalElement.setTextContent(push.getReconnectInterval());
        upnsElement.appendChild(reconnectIntervalElement);

        Element reallocateIntervalElement = doc.createElement("reallocate-interval");
        reallocateIntervalElement.setTextContent(push.getReallocateInterval());
        upnsElement.appendChild(reallocateIntervalElement);

        Element retryRegistCountElement = doc.createElement("retry-regist-count");
        retryRegistCountElement.setTextContent(push.getRetryRegistCount());
        upnsElement.appendChild(retryRegistCountElement);

        return pushElement;
    }

    public Element createPreventionElement(Document doc) {
        Element preventionElement = doc.createElement("prevention");

        Element serverElement = doc.createElement("server");
        serverElement.setTextContent(getPreventionServerAdress());
        preventionElement.appendChild(serverElement);

        Element rootingElement = doc.createElement("rooting");
        rootingElement.setTextContent(getPreventionRooting());
        preventionElement.appendChild(rootingElement);

        if (isUsePreventionDebug()) {
            Element debugElement = doc.createElement("debug");
            preventionElement.appendChild(debugElement);

            Element androidElement = doc.createElement("android");
            debugElement.appendChild(androidElement);

            Element androidHashElement = doc.createElement("hash");
            androidHashElement.setTextContent(getPreventionAndroidHash());
            androidElement.appendChild(androidHashElement);

            Element androidVersionElement = doc.createElement("version");
            androidVersionElement.setTextContent(getPreventionAndroidVersion());
            androidElement.appendChild(androidVersionElement);

            Element iosElement = doc.createElement("ios");
            debugElement.appendChild(iosElement);

            Element iosHashElement = doc.createElement("hash");
            iosHashElement.setTextContent(getPreventionIosHash());
            iosElement.appendChild(iosHashElement);

            Element iosVersionElement = doc.createElement("version");
            iosVersionElement.setTextContent(getPreventionIosVersion());
            iosElement.appendChild(iosVersionElement);
        }
        return preventionElement;
    }

    @Override
    public String toString() {
        String manifestContent = null;
        manifestContent = XMLUtil.writeXMLString(toDocument());

        return manifestContent;
    }

    public void updateManifestNetwork(@NotNull ManifestNetwork network) {
        ManifestNetwork obj = getManifestNetwork(network.getName());
        if (obj == null)
            return;

        int index = networkList.indexOf(obj);
        networkList.remove(index);
        networkList.add(index, network);
    }

    public boolean isUsePreventionDebug() {
        return prevention.isUseDebug();
    }

    public void setUsePreventionDebug(boolean isUsePreventionDebug) {
        prevention.setUseDebug(isUsePreventionDebug);
    }

}
