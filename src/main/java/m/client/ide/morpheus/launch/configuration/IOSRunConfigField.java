package m.client.ide.morpheus.launch.configuration;

import com.esotericsoftware.minlog.Log;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.launch.IOSDeviceType;
import m.client.ide.morpheus.launch.IOSLaunchTargetType;
import m.client.ide.morpheus.launch.common.IOSRunningDevice;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class IOSRunConfigField {
    public static final String DEVICE_TYPE_FAMILY = "iphone";
    public static final String DEVICE_TYPE_IPHONE = "iphoneos";
    public static final String DEVICE_TYPE_IPHONE_SIMULATOR = "iphonesimulator";

    private final Project project;
    IOSLaunchTargetType iosLaunchType = IOSLaunchTargetType.SIMULATOR;
    IOSDeviceType deviceFamily = IOSDeviceType.iPhone;

    boolean retina = true;

    private String iosDestination = "";
    private String simulatorUUid = "";

    private String outputDir;
    private String outputFileName;
    private String currentDir;
    private String iosDeviceSerialNumber;
    private String iosDeviceVersion;
    private String iosDeploymentTarget = "";
    private String iosTargetSdkVersion;
    private String iosTargetType;

    private String iosDeviceTypeId = "";

    private String iosCertificateName;
    private String iosDevelopmentTeam;
    private String isLaunchTest;
    private String simDisplayName = "FALSE";
    private String iosDeployTarget = "";
    private String packageLocation;

    public IOSRunConfigField(@NotNull Project project) {
        this.project = project;

        currentDir = LaunchUtil.getIOSProjectPath(project);
        outputDir = CommonUtil.getPathString(currentDir, "output");

        LaunchUtil.SimulatorInfo simulatorInfo = getDefaultSimulatorInfo();
        if (simulatorInfo != null) {
            String id = simulatorInfo.getDeviceTypeId().trim();
            String deviceTypeId = id.substring(id.lastIndexOf('.') + 1) + ", " + simulatorInfo.getOs();

            iosTargetType = DEVICE_TYPE_IPHONE_SIMULATOR;
            simDisplayName = simulatorInfo.getDisplayName();
            iosDeviceTypeId = deviceTypeId;
            iosDestination = simulatorInfo.toString();
            simulatorUUid = simulatorInfo.getUuid();
            iosTargetSdkVersion = simulatorInfo.getOs();
        }

        HashMap<String, LaunchUtil.IOSDeviceInfo> devices = LaunchUtil.getIOSDevices(project);
        Set<String> keyset = devices.keySet();
        if (keyset.size() > 0) {
            iosDeviceSerialNumber = (String) keyset.toArray()[0];
            LaunchUtil.IOSDeviceInfo device = devices.get(iosDeviceSerialNumber);
            if (device != null) {
                iosDeviceVersion = device.getVersion();
                iosDeploymentTarget = device.getDeploymentTarget();
            }

            initCertification();
        }
    }

    private void initCertification() {
        if (iosCertificateName != null && !iosCertificateName.isEmpty()) {
            return;
        }

        Hashtable<String, LaunchUtil.CertificateInfo> iosCertificateInfos = null;
        try {
            iosCertificateInfos = LaunchUtil.getIosCertificateInfos();
            for (String certificateName : iosCertificateInfos.keySet()) {
                LaunchUtil.CertificateInfo certificateInfo = iosCertificateInfos.get(certificateName);
                String developmentTeam = certificateInfo.getDevelopmentTeam();
                if (developmentTeam != null && !developmentTeam.isEmpty()) {
                    setIosCertificateName(certificateName);
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            iosCertificateName = "";
        }
    }

    public String getActionPresentationText() {
        if (iosLaunchType == IOSLaunchTargetType.DEVICE) {
            return iosDeviceTypeId + " " + iosDeviceVersion + "(" + iosDeviceSerialNumber + ")";
        } else if (iosLaunchType == IOSLaunchTargetType.SIMULATOR) {
            return simDisplayName == null ? "" : simDisplayName;
        }
        return "";
    }

    private LaunchUtil.SimulatorInfo getDefaultSimulatorInfo() {
        ArrayList<LaunchUtil.SimulatorInfo> list = LaunchUtil.getIOSSimulators();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    public IOSDeviceType getDeviceFamily() {
        return deviceFamily;
    }

    public void setDeviceFamily(IOSDeviceType deviceFamily) {
        this.deviceFamily = deviceFamily;
    }

    public boolean isRetina() {
        return retina;
    }

    public void setRetina(boolean retina) {
        this.retina = retina;
    }

    public IOSLaunchTargetType getIosLaunchType() {
        return iosLaunchType;
    }

    public void setIosLaunchType(IOSLaunchTargetType iosLaunchType) {
        this.iosLaunchType = iosLaunchType;
    }

    public String getIosDestination() {
        return iosDestination;
    }

    public void setIosDestination(String iosDestination) {
        this.iosDestination = iosDestination;
    }

    public String getSimulatorUUid() {
        return simulatorUUid;
    }

    public void setSimulatorUUid(String simulatorUUid) {
        this.simulatorUUid = simulatorUUid;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String fileName) {
        this.outputFileName = fileName;
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    public String getIosDeviceSerialNumber() {
        return iosDeviceSerialNumber;
    }

    public void setIosDeviceSerialNumber(String iosDeviceSerialNumber) {
        this.iosDeviceSerialNumber = iosDeviceSerialNumber;
    }

    public String getIosDeviceVersion() {
        return iosDeviceVersion;
    }

    public String getDeploymentTarget() {
        return iosDeploymentTarget == null ? "" : iosDeploymentTarget;
    }

    public void setIosDeviceVersion(String iosDeviceVersion) {
        this.iosDeviceVersion = iosDeviceVersion;
    }

    public @NotNull String getIosTargetSdkVersion() {
        return iosTargetSdkVersion == null ? "" : iosTargetSdkVersion;
    }

    public void setIosTargetSdkVersion(String iosTargetSdkVersion) {
        this.iosTargetSdkVersion = iosTargetSdkVersion;
    }

    public @NotNull String getIosTargetType() {
        return iosTargetType == null ? "" : iosTargetType;
    }

    public void setIosTargetType(String iosTargetType) {
        this.iosTargetType = iosTargetType;
    }

    public String getIosDeviceTypeId() {
        return iosDeviceTypeId;
    }

    public void setIosDeviceTypeId(String iosDeviceTypeId) {
        this.iosDeviceTypeId = iosDeviceTypeId;
    }

    public String getIosCertificateName() {
        return iosCertificateName;
    }

    public void setIosCertificateName(String iosCertificateName) {
        if (StringUtil.equals(this.iosCertificateName, iosCertificateName)) {
            return;
        }
        this.iosCertificateName = iosCertificateName;

        AtomicBoolean isTest = new AtomicBoolean(false);
        iosDevelopmentTeam = LaunchUtil.getIOSDevelopmentTeam(iosCertificateName, isTest);
        isLaunchTest = isTest.get() ? "TRUE" : "FALSE";
    }

    public String getIosDevelopmentTeam() {
        return iosDevelopmentTeam;
    }

    public void setIosDevelopmentTeam(String iosDevelopmentTeam) {
        this.iosDevelopmentTeam = iosDevelopmentTeam;
    }

    public String getIsLaunchTest() {
        return isLaunchTest;
    }

    public void setIsLaunchTest(String isLaunchTest) {
        this.isLaunchTest = isLaunchTest;
    }

    public String getSimDisplayName() {
        return simDisplayName;
    }

    public void setSimDisplayName(String simDisplayName) {
        this.simDisplayName = simDisplayName;
    }

    public void checkRunnable(Project project) throws RuntimeConfigurationError {
        final Result main = verify(project);
        if (!main.canLaunch()) {
            throw new RuntimeConfigurationError(main.getError());
        }

        if (main.get() == null) {
            throw new RuntimeConfigurationError("Entrypoint isn't within a Flutter pub root");
        }
    }

    /**
     * Verifies that the given path points to an entrypoint file within a Flutter app.
     * <p>
     * If there is an error, {@link Result#canLaunch} will return false and the error is available via {@link Result#getError}
     */
    @NotNull
    public static Result verify(Project project) {
        if (!ApplicationManager.getApplication().isReadAccessAllowed()) {
            throw new IllegalStateException("need read access");
        }

        final VirtualFile dir = findAppDir(project);
        if (dir == null) {
            return error(MessageBundle.message("entrypoint.not.in.app.dir"));
        }

        return new Result(dir, null);
    }

    @Nullable
    private static VirtualFile findAppDir(@NotNull Project project) {
        @Nullable File iosProjectFolder = LaunchUtil.getIOSProjectFolder(project);
        if (iosProjectFolder == null || !iosProjectFolder.exists()) {
            CommonUtil.openInfoDialog(UIMessages.get(UIMessages.OpenXCode), UIMessages.get(UIMessages.IOSResourceNotExist));
            return null;
        }

        return LocalFileSystem.getInstance().findFileByPath(FileUtil.toSystemDependentName(iosProjectFolder.getAbsolutePath()));
    }

    private static boolean inProject(@Nullable VirtualFile file, @NotNull Project project) {
        return file != null && ProjectRootManager.getInstance(project).getFileIndex().isInContent(file);
    }

    public void writeTo(Element element) {
        ElementIO.addOption(element, "iosLaunchType", iosLaunchType == null ? IOSLaunchTargetType.SIMULATOR.name() : iosLaunchType.toString());
        ElementIO.addOption(element, "deviceFamily", deviceFamily == null ? IOSDeviceType.iPhone.name() : deviceFamily.toString());

        ElementIO.addOption(element, "retina", retina ? Boolean.TRUE.toString() : Boolean.FALSE.toString());

        ElementIO.addOption(element, "iosDestination", iosDestination);
        ElementIO.addOption(element, "simulatorUUid", simulatorUUid);

        ElementIO.addOption(element, "outputDir", outputDir);
        ElementIO.addOption(element, "outputFileName", outputFileName);
        ElementIO.addOption(element, "packagingLocation", packageLocation);
        ElementIO.addOption(element, "currentDir", currentDir);
        ElementIO.addOption(element, "iosDeviceSerialNumber", iosDeviceSerialNumber);
        ElementIO.addOption(element, "iosDeviceVersion", iosDeviceVersion);
        ElementIO.addOption(element, "iosDeploymentTarget", iosDeploymentTarget);
        ElementIO.addOption(element, "iosTargetSdkVersion", iosTargetSdkVersion);
        ElementIO.addOption(element, "iosTargetType", iosTargetType);

        ElementIO.addOption(element, "iosDeviceTypeId", iosDeviceTypeId);

        ElementIO.addOption(element, "iosCertificateName", iosCertificateName);
        ElementIO.addOption(element, "iosDevelopmentTeam", iosDevelopmentTeam);
        ElementIO.addOption(element, "isLaunchTest", isLaunchTest);
        ElementIO.addOption(element, "simDisplayName", simDisplayName);
    }

    public static IOSRunConfigField readFrom(Project project, Element element) {
        final Map<String, String> options = ElementIO.readOptions(element);

        // Use old field name of bazelTarget if the newer one has not been set.
        IOSRunConfigField iosConfigField = new IOSRunConfigField(project);
        iosConfigField.setIosLaunchType(IOSLaunchTargetType.fromString(options.get("iosLaunchType")));
        iosConfigField.setDeviceFamily(IOSDeviceType.fromString(options.get("deviceFamily")));

        iosConfigField.setRetina(Boolean.parseBoolean(options.get("retina")));

        iosConfigField.setIosDestination(options.get("iosDestination"));
        iosConfigField.setSimulatorUUid(options.get("simulatorUUid"));

        iosConfigField.setOutputDir(options.get("outputDir"));
        iosConfigField.setCurrentDir(options.get("currentDir"));
        iosConfigField.setIosDeviceSerialNumber(options.get("iosDeviceSerialNumber"));
        iosConfigField.setIosDeviceVersion(options.get("iosDeviceVersion"));
        iosConfigField.setDeploymentTarget(options.get("iosDeploymentTarget"));
        iosConfigField.setIosTargetSdkVersion(options.get("iosTargetSdkVersion"));
        iosConfigField.setIosTargetType(options.get("iosTargetType"));

        iosConfigField.setIosDeviceTypeId(options.get("iosDeviceTypeId"));

        iosConfigField.setIosCertificateName(options.get("iosCertificateName"));
        iosConfigField.setIosDevelopmentTeam(options.get("iosDevelopmentTeam"));
        iosConfigField.setIsLaunchTest(options.get("isLaunchTest"));
        iosConfigField.setSimDisplayName(options.get("simDisplayName"));

        return iosConfigField;
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull Result error(@NotNull String message) {
        return new Result(null, message);
    }

    public IOSRunConfigField copy() {
        IOSRunConfigField copy = new IOSRunConfigField(project);

        copy.iosLaunchType = iosLaunchType;
        copy.deviceFamily = deviceFamily;

        copy.retina = retina;

        copy.iosDestination = iosDestination;
        copy.simulatorUUid = simulatorUUid;

        copy.outputDir = outputDir;
        copy.currentDir = currentDir;
        copy.iosDeviceSerialNumber = iosDeviceSerialNumber;
        copy.iosDeviceVersion = iosDeviceVersion;
        copy.iosDeploymentTarget = iosDeploymentTarget;
        copy.iosTargetSdkVersion = iosTargetSdkVersion;
        copy.iosTargetType = iosTargetType;

        copy.iosDeviceTypeId = iosDeviceTypeId;

        copy.iosCertificateName = iosCertificateName;
        copy.iosDevelopmentTeam = iosDevelopmentTeam;
        copy.isLaunchTest = isLaunchTest;
        copy.simDisplayName = simDisplayName;

        return copy;
    }

    public void setSelectedDevice(@NotNull IOSRunningDevice device) {
        iosLaunchType = device.getType();
        switch (iosLaunchType) {
            case DEVICE:
                LaunchUtil.IOSDeviceInfo deviceInfo = device.getDeviceInfo();
                if (deviceInfo != null) {
                    iosDeviceTypeId = deviceInfo.getDeviceType().name();
                    iosDeviceSerialNumber = deviceInfo.getSerial();
                    iosDeviceVersion = deviceInfo.getVersion();
                    iosDeploymentTarget = deviceInfo.getDeploymentTarget();

                    ArrayList<String> targetSDKs = null;
                    try {
                        targetSDKs = LaunchUtil.getIOSTargetSDKList();
                    } catch (Exception e) {
                        CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
                    }
                    for (String target : targetSDKs) {
                        if (!target.contains("simulator")) {
                            iosTargetSdkVersion = target.split("os")[1];
                            iosTargetType = DEVICE_TYPE_IPHONE;
                            break;
                        }
                    }
                    initCertification();
                }
                break;
            default:
                LaunchUtil.SimulatorInfo simInfo = device.getSimulatorInfo();
                simDisplayName = simInfo.getDisplayName();
                simulatorUUid = simInfo.getUuid();

                String id = simInfo.getDeviceTypeId().trim();
                String deviceTypeId = id.substring(id.lastIndexOf('.') + 1) + ", " + simInfo.getOs();
                iosDeviceTypeId = deviceTypeId;
                iosTargetType = DEVICE_TYPE_IPHONE_SIMULATOR;
                iosTargetSdkVersion = simInfo.getOs();
        }
    }

    public void setDeploymentTarget(String deploymentTarget) {
        this.iosDeploymentTarget = deploymentTarget;
    }

    public String getPackagingLocation() {
        return packageLocation;
    }

    public void setPackagingLocation(String location) {
        packageLocation = location;
    }

    /**
     * The result of {@link #verify}; either a MainFile or an error.
     */
    public static class Result {
        @Nullable
        private final VirtualFile file;

        @Nullable
        private final String error;

        private Result(@Nullable VirtualFile file, @Nullable String error) {
            assert (file == null || error == null);
            assert (file != null || error != null);
            this.file = file;
            this.error = error;
        }

        /**
         * Returns true if the Flutter app can be launched.
         * <p>
         * If false, the error can be found by calling {@link #getError}.
         */
        public boolean canLaunch() {
            return error == null;
        }

        /**
         * Returns the error message to display if this file is not launchable.
         */
        @NotNull
        public String getError() {
            if (error == null) {
                throw new IllegalStateException("called getError when there is no error");
            }
            return error;
        }

        /**
         * Unwraps the MainFile. Valid only if there's not an error.
         */
        public VirtualFile get() {
            if (file == null) {
                throw new IllegalStateException("called getLaunchable when there is an error: " + error);
            }
            return file;
        }
    }
}
