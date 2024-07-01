package m.client.ide.morpheus.framework.eclipse.manifest;

import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.core.utils.XMLUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

public class ManifestUtil {
    public static Manifest getManifest(Document doc) {
        ArrayList<String> libraryAddonList = new ArrayList<String>();
        ArrayList<String> libraryPluginList = new ArrayList<String>();
        ArrayList<ManifestNetwork> networkList = new ArrayList<ManifestNetwork>();

        if (doc != null) {
            Element root = doc.getDocumentElement();
            Element logElement = XMLUtil.getFirstChildElementByName(root, "log");
            Element langElement = XMLUtil.getFirstChildElementByName(root, "lang");

            Element resourceElement = XMLUtil.getFirstChildElementByName(root, "resource");
            Element resourceBaseVersionElement = XMLUtil.getFirstChildElementByName(resourceElement, "base_version");
            Element resourceTargetElement = XMLUtil.getFirstChildElementByName(resourceElement, "target");
            Element resourceUpdateElement = XMLUtil.getFirstChildElementByName(resourceElement, "update");
            Element resourceUpdateServerElement = XMLUtil.getFirstChildElementByName(resourceUpdateElement, "server");
            Element resourceUpdateTrCodeElement = XMLUtil.getFirstChildElementByName(resourceUpdateElement, "trcode");
            Element resourceUpdateModeElement = XMLUtil.getFirstChildElementByName(resourceUpdateElement, "mode");

            Element startPageElement = XMLUtil.getFirstChildElementByName(root, "startpage");
            Element startPageNameElement = XMLUtil.getFirstChildElementByName(startPageElement, "name");
            Element startPageOrientElement = XMLUtil.getFirstChildElementByName(startPageElement, "orient");
            Element startPageOrientElementTablet = XMLUtil.getFirstChildElementByName(startPageElement, "orient-tablet");

            Element defaultActionElement = XMLUtil.getFirstChildElementByName(root, "default_action");
            Element defaultActionOrientElement = XMLUtil.getFirstChildElementByName(defaultActionElement, "orient");
            Element defaultActionOrientTabletElement = XMLUtil.getFirstChildElementByName(defaultActionElement, "orient-tablet");
            Element defaultActionAnimateElement = XMLUtil.getFirstChildElementByName(defaultActionElement, "animate");
            Element defaultActionIndicatorElement = XMLUtil.getFirstChildElementByName(defaultActionElement, "indicator");

            Element libraryElement = XMLUtil.getFirstChildElementByName(root, "library");
            Element libraryExtElement = XMLUtil.getFirstChildElementByName(libraryElement, "ext");
            Element libraryAddonsElement = XMLUtil.getFirstChildElementByName(libraryElement, "addons");
            if (libraryAddonsElement != null) {
                NodeList libraryAddonsPathNodeList = libraryAddonsElement.getElementsByTagName("path");
                for (int i = 0; i < libraryAddonsPathNodeList.getLength(); i++) {
                    Element addonPath = (Element) libraryAddonsPathNodeList.item(i);
                    libraryAddonList.add(addonPath.getTextContent());
                }
            }

            Element libraryPluginsElement = XMLUtil.getFirstChildElementByName(libraryElement, "plugins");
            if (libraryPluginsElement != null) {
                NodeList libraryPluginsPathNodeList = libraryPluginsElement.getElementsByTagName("path");
                for (int i = 0; i < libraryPluginsPathNodeList.getLength(); i++) {
                    Element pluginPath = (Element) libraryPluginsPathNodeList.item(i);
                    libraryPluginList.add(pluginPath.getTextContent());
                }
            }

            Element networkElement = XMLUtil.getFirstChildElementByName(root, "network");
            Element networkHttpElement = XMLUtil.getFirstChildElementByName(networkElement, "http");
            if (networkHttpElement != null) {
                NodeList httpNodeList = networkHttpElement.getChildNodes();
                for (int i = 0; i < httpNodeList.getLength(); i++) {
                    if (httpNodeList.item(i) instanceof Element) {
                        Element httpElement = (Element) httpNodeList.item(i);
                        Element pathElement = XMLUtil.getFirstChildElementByName(httpElement, "path");
                        Element addressElement = XMLUtil.getFirstChildElementByName(httpElement, "address");
                        Element timeoutElement = XMLUtil.getFirstChildElementByName(httpElement, "timeout");
                        Element encodingElement = XMLUtil.getFirstChildElementByName(httpElement, "encoding");
                        Element typeElement = XMLUtil.getFirstChildElementByName(httpElement, "type");

                        String type = null;
                        if (typeElement != null)
                            type = typeElement.getTextContent();
                        else
                            type = "normal";

                        ManifestNetwork network = new ManifestHttpNetwork(httpElement.getNodeName(), pathElement.getTextContent(),
                                addressElement.getTextContent(), timeoutElement.getTextContent(), encodingElement.getTextContent(),
                                ManifestNetworkType.fromString(type));
                        networkList.add(network);
                    }
                }
            }

            Element networkSocketElement = XMLUtil.getFirstChildElementByName(networkElement, "socket");
            if (networkSocketElement != null) {
                NodeList socketNodeList = networkSocketElement.getChildNodes();
                for (int i = 0; i < socketNodeList.getLength(); i++) {
                    if (socketNodeList.item(i) instanceof Element) {
                        Element socketElement = (Element) socketNodeList.item(i);
                        Element pathElement = XMLUtil.getFirstChildElementByName(socketElement, "path");
                        Element addressElement = XMLUtil.getFirstChildElementByName(socketElement, "address");
                        Element timeoutElement = XMLUtil.getFirstChildElementByName(socketElement, "timeout");
                        Element encodingElement = XMLUtil.getFirstChildElementByName(socketElement, "encoding");
                        Element portElement = XMLUtil.getFirstChildElementByName(socketElement, "port");

                        ManifestNetwork network = new ManifestSocketNetwork(socketElement.getNodeName(), pathElement.getTextContent(),
                                addressElement.getTextContent(), timeoutElement.getTextContent(), encodingElement.getTextContent(),
                                portElement.getTextContent());
                        networkList.add(network);
                    }
                }
            }

            String useHWAcceleration = "software";
            String useTheme = "n";
            String useYoutube = "n";
            Element applicationElement = XMLUtil.getFirstChildElementByName(root, "application");
            if (applicationElement != null) {
                Element androidElement = XMLUtil.getFirstChildElementByName(applicationElement, "android");
                if (androidElement != null) {
                    useHWAcceleration = XMLUtil.getFirstChildElementByName(androidElement, "hardwareaccelerated").getTextContent();
                    useTheme = XMLUtil.getFirstChildElementByName(androidElement, "user-theme").getTextContent();

                    Element useYoutubeElement = XMLUtil.getFirstChildElementByName(androidElement, "use-youtube");
                    if (useYoutubeElement != null) {
                        useYoutube = XMLUtil.getFirstChildElementByName(androidElement, "use-youtube").getTextContent();
                    }
                }
            }

            boolean useDirectView = false;
            if (root.hasAttribute("useDirectView")) {
                useDirectView = Boolean.parseBoolean(root.getAttribute("useDirectView").trim());
            }

            String startpageOrientTablet = "default";
            if (startPageOrientElementTablet != null) {
                startpageOrientTablet = startPageOrientElementTablet.getTextContent();
            }

            String defaultActionOrientTablet = "default";
            if (defaultActionOrientTabletElement != null) {
                defaultActionOrientTablet = defaultActionOrientTabletElement.getTextContent();
            }

            Push push = null;
            Prevention prevention = null;

            Element settingsElement = XMLUtil.getFirstChildElementByName(root, "settings");
            if (settingsElement != null) {
                Element pushElement = XMLUtil.getFirstChildElementByName(settingsElement, "push");
                if (pushElement != null) {
                    push = genPushModel(pushElement);
                }

                Element preventionElement = XMLUtil.getFirstChildElementByName(settingsElement, "prevention");
                if (preventionElement != null) {
                    prevention = genPreventionModel(preventionElement);
                }
            }

            Manifest manifest = new Manifest(useDirectView, logElement.getTextContent(), langElement.getTextContent(),
                    resourceBaseVersionElement.getTextContent(), resourceTargetElement.getTextContent(),
                    resourceUpdateServerElement.getTextContent(), resourceUpdateTrCodeElement.getTextContent(),
                    resourceUpdateModeElement.getTextContent(), startPageNameElement.getTextContent(),
                    startPageOrientElement.getTextContent(), startpageOrientTablet, defaultActionOrientElement.getTextContent(),
                    defaultActionOrientTablet,
                    defaultActionAnimateElement.getTextContent(), defaultActionIndicatorElement.getTextContent(),
                    libraryExtElement.getTextContent(), libraryAddonList, libraryPluginList, networkList,
                    useHWAcceleration,
                    useTheme,
                    useYoutube,
                    push,
                    prevention);

            manifest.setUsePushPlugin(push != null);
            manifest.setUsePreventionPlugin(prevention != null);

            return manifest;
        }
        return null;
    }

    private static Prevention genPreventionModel(Element preventionElement) {
        Prevention prevention = new Prevention();
        Element pServerAdressElement = XMLUtil.getFirstChildElementByName(preventionElement, "server");
        if (pServerAdressElement != null) {
            prevention.setServerAdress(pServerAdressElement.getTextContent().trim());
        }

        Element pRooting = XMLUtil.getFirstChildElementByName(preventionElement, "rooting");
        if (pRooting != null) {
            prevention.setRooting(pRooting.getTextContent().trim());
        }

        Element debugElement = XMLUtil.getFirstChildElementByName(preventionElement, "debug");
        if (debugElement != null) {
            prevention.setUseDebug(true);

            Element androidElement = XMLUtil.getFirstChildElementByName(debugElement, "android");
            if (androidElement != null) {
                Element hash = XMLUtil.getFirstChildElementByName(androidElement, "hash");
                if (hash != null) {
                    prevention.setAndroidHash(hash.getTextContent().trim());
                }

                Element version = XMLUtil.getFirstChildElementByName(androidElement, "version");
                if (version != null) {
                    prevention.setAndroidVersion(version.getTextContent().trim());
                }
            }

            Element iosElement = XMLUtil.getFirstChildElementByName(debugElement, "ios");
            if (iosElement != null) {
                Element hash = XMLUtil.getFirstChildElementByName(iosElement, "hash");
                if (hash != null) {
                    prevention.setIosHash(hash.getTextContent().trim());
                }

                Element version = XMLUtil.getFirstChildElementByName(iosElement, "version");
                if (version != null) {
                    prevention.setIosVersion(version.getTextContent().trim());
                }
            }
        }

        return prevention;
    }

    private static Push genPushModel(Element pushElement) {
        Push push = new Push();
        Element receiverElement = XMLUtil.getFirstChildElementByName(pushElement, "receiver");
        if (receiverElement != null) {

            Element versionElement = XMLUtil.getFirstChildElementByName(receiverElement, "version");
            if (versionElement != null) {
                push.setVersion(versionElement.getTextContent().trim());
            }

            Element serverElement = XMLUtil.getFirstChildElementByName(receiverElement, "server");
            if (serverElement != null) {
                push.setServer(serverElement.getTextContent().trim());
            }

            Element timeoutElement = XMLUtil.getFirstChildElementByName(receiverElement, "timeout");
            if (timeoutElement != null) {
                push.setTimeout(timeoutElement.getTextContent().trim());
            }

            Element fcmElement = XMLUtil.getFirstChildElementByName(receiverElement, "fcm-sender-id");
            if (XMLUtil.getFirstChildElementByName(receiverElement, "fcm-sender-id") != null) {
                push.setFcmSenderId(fcmElement.getTextContent().trim());
            }

            Element androidPushTypeElement = XMLUtil.getFirstChildElementByName(receiverElement, "android-push-type");
            if (androidPushTypeElement != null) {
                push.setAndroidPushType(androidPushTypeElement.getTextContent().trim());
            }

            Element pushLogElement = XMLUtil.getFirstChildElementByName(receiverElement, "log");
            if (pushLogElement != null) {
                push.setLog(pushLogElement.getTextContent().trim());
            }

            Element policyElement = XMLUtil.getFirstChildElementByName(receiverElement, "policy");
            if (policyElement != null) {
                push.setPolicy(policyElement.getTextContent().trim());
            }

            Element securityIndexesElement = XMLUtil.getFirstChildElementByName(receiverElement, "security-indexes");
            if (securityIndexesElement != null) {
                push.setSecurityIndexes(securityIndexesElement.getTextContent().trim());
            }

            Element usePhoneElement = XMLUtil.getFirstChildElementByName(receiverElement, "use-phone_number");
            if (usePhoneElement != null) {
                push.setUsePhoneNumber(usePhoneElement.getTextContent().trim());
            }

            Element usePermissionElement = XMLUtil.getFirstChildElementByName(receiverElement, "use-permission");
            if (usePermissionElement != null) {
                push.setUsePermission(usePermissionElement.getTextContent().trim());
            }
        }

        Element upnsElement = XMLUtil.getFirstChildElementByName(pushElement, "upns");
        if (upnsElement != null) {
            Element agentServiceTypeElement = XMLUtil.getFirstChildElementByName(upnsElement, "agent-service-type");
            if (agentServiceTypeElement != null) {
                push.setAgentServiceType(agentServiceTypeElement.getTextContent().trim());
            }

            Element agentRestartIntervalElement = XMLUtil.getFirstChildElementByName(upnsElement, "agent-restart-interval");
            if (agentRestartIntervalElement != null) {
                push.setAgentRestartInterval(agentRestartIntervalElement.getTextContent().trim());
            }

            Element reconnectIntervalElement = XMLUtil.getFirstChildElementByName(upnsElement, "reconnect-interval");
            if (reconnectIntervalElement != null) {
                push.setReconnectInterval(reconnectIntervalElement.getTextContent().trim());
            }

            Element reallocateIntervalElement = XMLUtil.getFirstChildElementByName(upnsElement, "reallocate-interval");
            if (reallocateIntervalElement != null) {
                push.setReallocateInterval(reallocateIntervalElement.getTextContent().trim());
            }

            Element retryRegistCountElement = XMLUtil.getFirstChildElementByName(upnsElement, "retry-regist-count");
            if (retryRegistCountElement != null) {
                push.setRetryRegistCount(retryRegistCountElement.getTextContent().trim());
            }
        }

        return push;
    }

    public static @Nullable Manifest getManifest(@NotNull File manifestFile) {
        if (manifestFile.exists()) {
            Document doc = XMLUtil.getDocument(manifestFile);
            return getManifest(doc);
        }
        return null;
    }

    public static @Nullable Manifest getManifest(@NotNull String manifestContent) {
        if (manifestContent.length() > 0) {
            Document doc = XMLUtil.getDocument(manifestContent);
            return getManifest(doc);
        }
        return null;
    }

    public static File getManifestFile(@NotNull File projectFolder) {
        return FileUtil.getChildFile(projectFolder, "assets", "res", "Manifest.xml");
    }
}
