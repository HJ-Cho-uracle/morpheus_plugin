package m.client.ide.morpheus.launch.common;

import com.esotericsoftware.minlog.Log;
import com.intellij.execution.impl.EditConfigurationsDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.ExecCommandUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.framework.FrameworkConstants;
import m.client.ide.morpheus.launch.IOSDeviceType;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.Principal;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LaunchUtil {


    public static void openRunConfigurationEditor(Project project) {
//        AnAction editRunConfigurationsAction = ActionManager.getInstance().getAction("editRunConfigurations");
        if (project == null || !project.isDisposed()) {
            if (project == null) {
                project = ProjectManager.getInstance().getDefaultProject();
            }

            (new EditConfigurationsDialog(project)).show();
        }
    }

    public static class IOSDeviceInfo {
        private String serial;
        private IOSDeviceType deviceType;
        private String version;
        private String deploymentTarget;

        public IOSDeviceInfo(String serial, IOSDeviceType deviceType, String version, String deploymentTarget) {
            this.serial = serial;
            this.deviceType = deviceType;
            this.version = version;
            this.deploymentTarget = deploymentTarget;
        }

        public String getSerial() {
            return serial;
        }

        public IOSDeviceType getDeviceType() {
            return deviceType;
        }

        public String getVersion() {
            return version;
        }

        public String getActionPresentationText() {
            return getDeviceType().name() + " " + getVersion() + "(" + getSerial() + ")";
        }

        public String getDeploymentTarget() {
            return deploymentTarget;
        }
    }

    public static class SimulatorInfo {
        private String platform;
        private String name;
        private String uuid;
        private String os;
        private String deviceTypeId;

        public SimulatorInfo(String platform, String name, String uuid, String os, String deviceTypeId) {
            super();
            this.platform = platform;
            this.name = name;
            this.uuid = uuid;
            this.os = os;
            this.deviceTypeId = deviceTypeId;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getDeviceTypeId() {
            // TODO Auto-generated method stub
            return deviceTypeId;
        }

        public String getDisplayName() {
            return name + "(" + platform + " " + os + ")";
        }

        public String toString() {
            StringBuilder builder = new StringBuilder("platform=");
            builder.append(platform)
                    .append(",name=")
                    .append(name)
                    .append(",OS=")
                    .append(os);

            return builder.toString();
        }
    }

    public static class DeploymentInfo {
        public enum DeployInfo {
            hardwareModel(0), modelName(1), sdkName(2),
            architectureName(3), productVersion(4), buildVersion(5);
            private final int value;

            DeployInfo(int value) {
                this.value = value;
            }
        }

        private final String serial;
        private String deviceName;
        private final String[] infos;

        public DeploymentInfo(@NotNull String serial, @NotNull String deviceName, @NotNull String[] infos) {
            this.serial = serial;
            this.deviceName = deviceName;
            this.infos = infos;
        }

        private String getInfo(DeployInfo info) {
            return infos.length >= info.value ? infos[info.value] : "";
        }

        public String getSerial() {
            return serial;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public String getHardwareModel() {
            return getInfo(DeployInfo.hardwareModel);
        }

        public String getModelName() {
            return getInfo(DeployInfo.modelName);
        }

        public String getSdkName() {
            return getInfo(DeployInfo.sdkName);
        }

        public String getArchitectureName() {
            return getInfo(DeployInfo.architectureName);
        }

        public String getProductVersion() {
            return getInfo(DeployInfo.productVersion);
        }

        public String getBuildVersion() {
            return getInfo(DeployInfo.buildVersion);
        }
    }

    public static class CertificateInfo {
        private final String certificateName;
        private final String developmentTeam;
        private final boolean isTest;

        public CertificateInfo(String certificateName, String developmentTeam, boolean isTest) {
            this.certificateName = certificateName;
            this.developmentTeam = developmentTeam;
            this.isTest = isTest;
        }

        public String getCertificateName() {
            return certificateName;
        }

        public String getDevelopmentTeam() {
            return developmentTeam;
        }

        public boolean isTest() {
            return isTest;
        }
    }

    private static HashMap<String, ArrayList<SimulatorInfo>> iosTSimulatorList;

    public static HashMap<String, ArrayList<SimulatorInfo>> getIosSimulatorList() {
        return getIosSimulatorList(false);
    }

    public static HashMap<String, ArrayList<SimulatorInfo>> getIosSimulatorList(boolean isRefresh) {
        if (isRefresh || iosTSimulatorList == null) {
            iosTSimulatorList = new HashMap<>();
            refreshSimulatorList();
        }

        return iosTSimulatorList;
    }

    private static void refreshSimulatorList() {
        if (iosTSimulatorList == null) {
            iosTSimulatorList = new HashMap<>();
        } else {
            iosTSimulatorList.clear();
        }

        // xcrun simctl list --json devices available
        try {
            Process processUDID = Runtime.getRuntime().exec("xcrun simctl list --json devices");
            BufferedReader in = new BufferedReader(new InputStreamReader(processUDID.getInputStream()));
            String line = null;

            String json = "";
            while ((line = in.readLine()) != null) {
                if (line != null && !line.isEmpty())
                    json += line;
            }

            if (json.isEmpty()) {
                return;
            }

            JSONParser parser = new JSONParser();
            Object root = parser.parse(json.trim());
            if (root instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) root;
                Object object = jsonObject.get("devices");
                if (object instanceof JSONObject) {
                    JSONObject devices = (JSONObject) object;
                    Iterator<?> keys = devices.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        object = devices.get(key);
                        if (!(object instanceof JSONArray))
                            continue;

                        String[] segs = key.split("\\.");
                        String deviceGroup = segs.length > 1 ? segs[segs.length - 1] : segs[0];
                        String[] types = deviceGroup.split("-");
                        String platform = types[0] + " Simulator";
                        String os = "";
                        for (int i = 1; i < types.length; i++) {
                            os += types[i] + (i < types.length - 1 ? "." : "");
                        }
                        if (platform.isEmpty() || os.isEmpty())
                            continue;

                        ArrayList<SimulatorInfo> simList = new ArrayList<SimulatorInfo>();
                        JSONArray deviceInfos = (JSONArray) object;
                        for (int i = 0; i < deviceInfos.size(); i++) {
                            object = deviceInfos.get(i);
                            if (!(object instanceof JSONObject))
                                continue;

                            JSONObject deviceInfo = (JSONObject) object;
                            Boolean aviable = (Boolean) deviceInfo.get("isAvailable");
                            if (aviable == false)
                                continue;

                            String name = (String) deviceInfo.get("name");
                            String udid = (String) deviceInfo.get("udid");

                            String deviceTypeId = (String) deviceInfo.get("deviceTypeIdentifier");

                            simList.add(new SimulatorInfo(platform, name, udid, os, deviceTypeId));
                        }
                        iosTSimulatorList.put(deviceGroup, simList);
                    }
                }
            }
        } catch (ParseException | IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
    }

    private static Collection<IOSRunningDevice> runningDevices;

    public static Collection<IOSRunningDevice> getIOSRunningDevices(Project project) {
        return getIOSRunningDevices(project, false);
    }

    public static Collection<IOSRunningDevice> getIOSRunningDevices(Project project, boolean isRefresh) {
        if (isRefresh || runningDevices == null) {
            runningDevices = new ArrayList<>();
            refreshRunningDeviceInfo(project);
        }

        return runningDevices;
    }

    private static void refreshRunningDeviceInfo(Project project) {
        if (runningDevices == null) {
            runningDevices = new ArrayList<>();
        } else {
            runningDevices.clear();
        }
        refreshSimulatorInfo();
        refreshDeviceList(project);
        refreshIOSTargetSDKList();

        for (SimulatorInfo simulator : simulatorInfos) {
            runningDevices.add(new IOSRunningDevice(simulator));
        }
        Set<String> keyset = devices.keySet();
        for (String key : keyset) {
            IOSDeviceInfo device = devices.get(key);
            runningDevices.add(new IOSRunningDevice(device));
        }
    }

    private static ArrayList<SimulatorInfo> simulatorInfos;

    public static @NotNull ArrayList<SimulatorInfo> getIOSSimulators() {
        return getIOSSimulators(false);
    }

    public static @NotNull ArrayList<SimulatorInfo> getIOSSimulators(boolean isRefresh) {
        if (isRefresh || simulatorInfos == null) {
            simulatorInfos = new ArrayList<>();
            refreshSimulatorInfo();
        }
        return simulatorInfos;
    }

    private static void refreshSimulatorInfo() {
        if (simulatorInfos == null) {
            simulatorInfos = new ArrayList<>();
        } else {
            simulatorInfos.clear();
        }

        HashMap<String, ArrayList<SimulatorInfo>> targetSDKList = getIosSimulatorList(true);
        Iterator<String> keys = targetSDKList.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.indexOf("iOS") < 0) continue;

            simulatorInfos.addAll(iosTSimulatorList.get(key));
        }
    }

    private static HashMap<String, IOSDeviceInfo> devices;

    public static HashMap<String, IOSDeviceInfo> getIOSDevices(Project project) {
        return getIOSDevices(project, false);
    }

    public static HashMap<String, IOSDeviceInfo> getIOSDevices(Project project, boolean isRefresh) {
        if (isRefresh || devices == null) {
            devices = new HashMap<String, IOSDeviceInfo>();
            refreshDeviceList(project);
        }

        return devices;
    }

    private static void refreshDeviceList(Project project) {
        if (devices == null) {
            devices = new HashMap<>();
        } else {
            devices.clear();
        }

        BufferedReader in = null;
        String[] commands = {"system_profiler", "SPUSBDataType"};

        try {
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process process = builder.start();
            process.waitFor();

            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            String token = "Serial Number:";
            String tkVersion = "Version:";

            while ((line = in.readLine()) != null) {
                if (line.contains(IOSDeviceType.iPhone.toString()) || line.contains(IOSDeviceType.iPad.toString())) {
                    IOSDeviceType type = null;
                    if (line.contains(IOSDeviceType.iPhone.toString()))
                        type = IOSDeviceType.iPhone;
                    else
                        type = IOSDeviceType.iPad;

                    String serial = "", version = "";
                    while ((line = in.readLine()) != null) {
                        if (line.contains(tkVersion)) {
                            version = line.split(tkVersion)[1].trim();
                        }
                        if (line.contains(token)) {
                            serial = line.split(token)[1].trim();
                        }

                        if (serial.isEmpty() == false && version.isEmpty() == false) {
                            devices.put(serial, new IOSDeviceInfo(serial, type, version, getDeploymentTarget(serial)));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
    }

    public static @NotNull String getDeploymentTarget(String serial) {
        BufferedReader in = null;
        String[] commands = {IOSSimUtil.getIOSDeployLocation(), "-version"};

        try {
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process process = builder.start();
            process.waitFor();

            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            String token = "[....] Using";

            while ((line = in.readLine().trim()) != null) {
                if (line.startsWith(token) && line.contains(serial)) {
                    String deviceName = "";
                    if (line.indexOf('\'') >= 0 && line.lastIndexOf('\'') > 0) {
                        deviceName = line.substring(line.indexOf('\'') + 1, line.lastIndexOf('\''));
                    }
                    if (line.indexOf('(') >= 0 && line.lastIndexOf(')') > 0) {
                        line = line.substring(line.indexOf('(') + 1, line.lastIndexOf(')'));
                        String[] splits = line.split(", ");
                        DeploymentInfo deployInfo = new DeploymentInfo(serial, deviceName, splits);
                        return deployInfo.getProductVersion();
                    }
                }
            }
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
        return "";
    }

    private static ArrayList<String> targetSDKList;

    public static ArrayList<String> getIOSTargetSDKList() {
        return getIOSTargetSDKList(false);
    }

    public static ArrayList<String> getIOSTargetSDKList(boolean isRefresh) {
        if (isRefresh || targetSDKList == null) {
            targetSDKList = new ArrayList<String>();
            refreshIOSTargetSDKList();
        }

        return targetSDKList;
    }

    private static void refreshIOSTargetSDKList() {
        if (targetSDKList == null) {
            targetSDKList = new ArrayList<String>();
        } else {
            targetSDKList.clear();
        }

        String xcodebuild = "/usr/bin/xcodebuild";
        String[] commands = {xcodebuild, "-showsdks"};
        String output = ExecCommandUtil.execProcessHandler(commands);

        StringTokenizer tokens = new StringTokenizer(output, System.getProperty("line.separator"));
        while (tokens.hasMoreTokens()) {
            String line = tokens.nextToken();
            if (line.contains("iOS") && line.contains("-sdk")) {
                String[] result = line.split("\\s+");
                targetSDKList.add(result[result.length - 1]);
            }
        }
    }

    private static Hashtable<String, CertificateInfo> iosCertificateInfos;

    public static Hashtable<String, CertificateInfo> getIosCertificateInfos() throws IOException, InterruptedException {
        return getIosCertificateInfos(false);
    }

    public static Hashtable<String, CertificateInfo> getIosCertificateInfos(boolean isRefresh) throws IOException, InterruptedException {
        if (isRefresh || iosCertificateInfos == null) {
            iosCertificateInfos = new Hashtable<>();
            refreshIOSCertificateInfos();
        }

        return iosCertificateInfos;
    }

    private static void refreshIOSCertificateInfos() {
        String security = "/usr/bin/security";
        String[] commands = {security, "find-identity", "-v"};

        String output = ExecCommandUtil.execProcessHandler(commands);

        StringTokenizer tokens = new StringTokenizer(output, System.getProperty("line.separator"));
        while (tokens.hasMoreTokens()) {
            String line = tokens.nextToken();
            String[] result = line.split("\"");
            if (result.length == 2) {
                String certificateName = result[1];
                if (certificateName != null && !certificateName.isEmpty()) {
                    try {
                        AtomicBoolean isTest = new AtomicBoolean();
                        String developmentTeam = findIOSDevelopmentTeam(certificateName, isTest);
                        iosCertificateInfos.put(certificateName,  new CertificateInfo(certificateName, developmentTeam, isTest.get()));
                    } catch (IOException | InterruptedException e) {
                        CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
                    }
                }
            }
        }
    }

    private static @NotNull String findIOSDevelopmentTeam(String developerId, AtomicBoolean isTest) throws IOException, InterruptedException {
        String security = "/usr/bin/security";
        BufferedReader in = null;

        if (developerId == null || developerId.isEmpty()) {
            return "";
        }

        String[] commands = {security, "find-certificate", "-c", developerId, "-p"};

        String output = ExecCommandUtil.execProcessHandler(commands);

        try {
            String organization = readSubjectFromCertificate(output).get("O");
            isTest.set(organization.contains("Co.") == false && organization.contains("Ltd.") == false);
            return readSubjectFromCertificate(output).get("OU");
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, "findIOSDevelopmentTeam : " + e.getLocalizedMessage(), e);
        }
        return "";
    }

    public static @NotNull ArrayList<String> getIOSDeveloperList() throws IOException, InterruptedException {
        return getIOSDeveloperList(false);
    }

    public static @NotNull ArrayList<String> getIOSDeveloperList(boolean isRefresh) throws IOException, InterruptedException {
        Hashtable<String, CertificateInfo> certificateInfos = getIosCertificateInfos(isRefresh);

        return new ArrayList<>(List.of(certificateInfos.keySet().toArray(new String[0])));
    }

    public static String getIOSDevelopmentTeam(String certificateName) {
        return getIOSDevelopmentTeam(certificateName, new AtomicBoolean());
    }

    public static String getIOSDevelopmentTeam(String certificateName, AtomicBoolean isTest) {
        try {
            Hashtable<String, CertificateInfo> certificateInfos = getIosCertificateInfos();
            if(certificateInfos == null || certificateInfos.size() == 0) {
                return "";
            }
            CertificateInfo certificateInfo = certificateInfos.get(certificateName);
            if(certificateInfo != null) {
                isTest.set(certificateInfo.isTest());
                return certificateInfo.developmentTeam;
            }
        } catch (IOException | InterruptedException e) {
            CommonUtil.log(Log.LEVEL_ERROR, "getIOSDevelopmentTeam : " + e.getLocalizedMessage(), e);
        }

        return "";
    }

    public static @NotNull Hashtable<String, String> readSubjectFromCertificate(@NotNull File file) throws Exception {
        String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

        return readSubjectFromCertificate(key);
    }


    public static @NotNull Hashtable<String, String> readSubjectFromCertificate(@NotNull String key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(key.replaceAll("-----BEGIN CERTIFICATE-----", "").replaceAll("-----END CERTIFICATE-----", "").replaceAll("\\s+", ""));

        X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(decoded));

        Hashtable<String, String> attributes = new Hashtable<String, String>();
        Principal subjectDN = certificate.getSubjectDN();
        if (subjectDN != null) {
            String[] listes = subjectDN.toString().split(",");
            if (listes != null) {
                for (String list : listes) {
                    String[] tokens = list.trim().split("=");
                    if (tokens != null && tokens.length == 2) {
                        attributes.put(tokens[0], tokens[1]);
                    }
                }
            }
        }
        return attributes;
    }

    public static void updateIPhoneOSDeploymentTarget(Project project, String version) {
        updateXCodeProjectConfig(getXCodeProjectFile(project), "IPHONEOS_DEPLOYMENT_TARGET", version);
        updateXCodeProjectConfig(getPopsProjectFile(project), "IPHONEOS_DEPLOYMENT_TARGET", version);
    }

    public static boolean updateXCodeProjectConfig(@NotNull File xcodeConfigFile, String configName/* "IPHONEOS_DEPLOYMENT_TARGET" */, String value) {
        boolean isChange = false;
        StringBuffer sBuffer = new StringBuffer();
        InputStreamReader isr = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        InputStream stream = null;
        try {
            String line = null;
            isr = new InputStreamReader(xcodeConfigFile.toURI().toURL().openStream());
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                sBuffer.append(line);
                sBuffer.append("\n");
            }

            int startIndex = 0;
            int endIndex = 0;
            while (startIndex != -1) {
                startIndex = findIndexContents(sBuffer, configName, startIndex);
                if (startIndex != -1) {
                    endIndex = findIndexContents(sBuffer, ";", startIndex);
                    if (endIndex != -1) {
                        String changeStr = configName + " = " + value + ";";

                        //변경된 내용이 있을 경우
                        if (changeStr != null) {
                            sBuffer.replace(startIndex, endIndex + 1, changeStr);
                            isChange = true;
                        }
                    }
                }
            }

            if (isChange) {
                stream = new ByteArrayInputStream(sBuffer.toString().getBytes("UTF-8"));
                br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xcodeConfigFile), "utf-8"));

                while ((line = br.readLine()) != null) {
                    bw.write(line + "\n");
                }
                bw.flush();
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
            return false;
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return isChange;
    }

    public static File getXCodeProjectFile(@NotNull Project project) {
        File iosHome = getIOSProjectFolder(project);
        File proejctHome = FileUtil.getChildFile(iosHome, project.getName() + FrameworkConstants.PROJECT_XCODE_SUFFIX);
        if (!proejctHome.exists()) {
            proejctHome = FileUtil.getChildFile(iosHome, project.getName() + ".iOS" + FrameworkConstants.PROJECT_XCODE_SUFFIX);
        }
        return FileUtil.getChildFile(proejctHome, FrameworkConstants.PROJECT_XCODE_PBXPROJ_FILE);
    }

    private static File getPopsProjectFile(Project project) {
        File iosHome = getIOSProjectFolder(project);
        File proejctHome = FileUtil.getChildFile(iosHome, "Pods", "Pods" + FrameworkConstants.PROJECT_XCODE_SUFFIX);
        if (!proejctHome.exists()) {
            proejctHome = FileUtil.getChildFile(iosHome, project.getName() + ".iOS" + FrameworkConstants.PROJECT_XCODE_SUFFIX);
        }
        return FileUtil.getChildFile(proejctHome, FrameworkConstants.PROJECT_XCODE_PBXPROJ_FILE);
    }

    public static String getIOSProjectPath(@NotNull Project project) {
        return FileUtil.getChildFile(project, UIMessages.get(UIMessages.IOSProjectPath)).getPath();
    }

    public static File getIOSProjectFolder(@NotNull Project project) {
        return FileUtil.getChildFile(project, UIMessages.get(UIMessages.IOSProjectPath));
    }

    public static File getResFolder(@NotNull Project project) {
        return FileUtil.getChildFile(project, UIMessages.get(UIMessages.ResFolderPath));
    }

    private static int findIndexContents(StringBuffer fileContents, String str, int nIndex) {
        return fileContents.indexOf(str, nIndex + 1);
    }


    public static @NotNull String getChromePathForWindows() {
        String path = null;
        try {
            path = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
                    "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\Chrome.exe", "");
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }

        if (path != null && !path.isEmpty() && new File(path).isFile()) {
            return path;
        }

        if (OSUtil.is64()) {
            path = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
        } else {
            String appdataPath = System.getenv("LOCALAPPDATA");
            path = appdataPath + "\\Google\\Chrome\\Application\\chrome.exe";
        }

        return path;
    }
}
