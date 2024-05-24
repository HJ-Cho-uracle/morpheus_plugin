package m.client.ide.morpheus.launch.common;

import com.android.annotations.Nullable;
import com.android.utils.GrabProcessOutput;
import com.android.utils.GrabProcessOutput.IProcessOutput;
import com.android.utils.GrabProcessOutput.Wait;
import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import m.client.ide.morpheus.core.config.CoreConfigurable;
import m.client.ide.morpheus.core.config.CoreSettingsState;
import m.client.ide.morpheus.core.constants.CoreConstants;
import m.client.ide.morpheus.core.messages.CoreMessages;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.npm.NpmUtils;
import m.client.ide.morpheus.core.utils.*;
import m.client.ide.morpheus.framework.FrameworkConstants;
import m.client.ide.morpheus.framework.messages.FrameworkMessages;
import m.client.ide.morpheus.launch.IOSLaunchTargetType;
import m.client.ide.morpheus.launch.configuration.IOSRunConfigField;
import m.client.ide.morpheus.launch.configuration.IOSRunConfiguration;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class IOSSimUtil {

    static final String DOWNLOADPAGE_URL = "http://docs.morpheus.kr/ide/morpheus/download/tools/";
    static final String REVISION_FILE_NAME = "revision";
    public static final String TOOLS_FOLDER_NAME = "tools";
    static final String TOOLS_FILE_NAME = "tools.zip";
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;
    /**
     * The file copy buffer size (30 MB)
     */
    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;
    private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

    public static void initialToolsData(@NotNull Project project) throws IOException {
        String dataPath = CommonUtil.getAppDataLocation();
        File toolsFolder = new File(dataPath, TOOLS_FOLDER_NAME);
        String serverRevision = getToolsRevision(new URL(DOWNLOADPAGE_URL + REVISION_FILE_NAME));
        if (toolsFolder.exists() && isLatest(toolsFolder, serverRevision) && !CoreSettingsState.getInstance().isToolUpdateForce()) {
            return;
        }
        CommonUtil.log(Log.LEVEL_DEBUG, "\n\r==========\t  m.client.ide.morpheus.launch.common.IOSSimUtil\n.initialToolsData() Start! \t==========");

        final Task.Modal modalTask = new Task.Modal(ProjectManager.getInstance().getDefaultProject(),
                CoreMessages.get(CoreMessages.InitToolsData), false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                String url = DOWNLOADPAGE_URL;
                String file = TOOLS_FILE_NAME;

                File tempFolder = new File(dataPath, "temp");

                boolean isMkDir = false;
                if (!tempFolder.exists()) {
                    tempFolder.mkdir();
                    isMkDir = true;
                }
                File zipFile = new File(tempFolder + File.separator + file);

                try {
                    String task2 = CoreMessages.get(CoreMessages.DownloadToolsData);
                    CommonUtil.log(Log.LEVEL_DEBUG, "\n\r==========\t " + task2 + "\t==========");
                    progressIndicator.setText2(task2);
                    FileUtil.downloadFileWithURL(new URL(url + file), zipFile);

                    task2 = CoreMessages.get(CoreMessages.ExtractToolsData);
                    CommonUtil.log(Log.LEVEL_DEBUG, "\n\r==========\t " + task2 + "\t==========");
                    progressIndicator.setText2(task2);
                    FileUtil.extract(zipFile, tempFolder);
                    zipFile.delete();

                    tempFolder = new File(tempFolder, TOOLS_FOLDER_NAME);
                    if (toolsFolder.exists()) {
                        FileUtil.deleteDirectory(toolsFolder);
                    }

                    task2 = CoreMessages.get(CoreMessages.CopyToolsDataToAppData);
                    CommonUtil.log(Log.LEVEL_DEBUG, "\n\r==========\t " + task2 + "\t==========");
                    progressIndicator.setText2(CoreMessages.get(CoreMessages.CopyToolsDataToAppData));
                    FileUtil.copyDirectory(tempFolder, toolsFolder, null, false);

                } catch (IOException e) {
                    CommonUtil.log(Log.LEVEL_ERROR, "Tools install failed : " + e.getLocalizedMessage(), e);
                } finally {
                    if (isMkDir) {
                        FileUtil.deleteDirectory(tempFolder.getParentFile());
                    } else {
                        FileUtil.deleteDirectory(tempFolder);
                    }
                }
            }
        };

        ProgressManager.getInstance().run(modalTask);

        iosSimNpmInstall(toolsFolder, serverRevision);
    }

    private static void iosSimNpmInstall(File toolsFolder, String serverRevision) {
        final Task.Modal modalTask = new Task.Modal(ProjectManager.getInstance().getDefaultProject(),
                CoreMessages.get(CoreMessages.InitToolsData), false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                String iosSimPath = FileUtil.getChildFile(toolsFolder, "ios-sim").getAbsolutePath();
                CommonUtil.log(Log.LEVEL_DEBUG, "\n\r==========\t  npm install \t==========");
                if (npmInstallWithCommand(iosSimPath)) {
                    CommonUtil.log(Log.LEVEL_DEBUG, "\n\r==========\t  npm audit fix \t==========");
                    if (!npmAuditFix(iosSimPath)) {
                        return;
                    }
                } else {
                    return;
                }

                CommonUtil.log(Log.LEVEL_DEBUG, "\n\r==========\t Tools ios-sim npm install successes! \t==========");
                File revision = new File(toolsFolder, REVISION_FILE_NAME);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(revision);
                    fos.write(serverRevision.getBytes());
                    fos.flush();
                } catch (IOException e) {
                    CommonUtil.log(Log.LEVEL_ERROR, "Tools ios-sim npm install : " + e.getLocalizedMessage(), e);
                }
            }
        };
        ProgressManager.getInstance().run(modalTask);
    }

    private static boolean npmInstallWithCommand(String basePath) {
        if (NpmUtils.hasNpmFile()) {
            String commands[] = {NpmUtils.findNpm(), NpmConstants.INSTALL_COMMAND};
            ExecCommandUtil.executeCommandWithLog(new File(basePath), CoreMessages.get(CoreMessages.npmInstall, basePath), null, commands);
        } else {
            PreferenceUtil.openPreference(null, CoreConfigurable.class);
            return false;
        }

        return true;
    }

    private static boolean npmAuditFix(String basePath) {
        if (NpmUtils.hasNpmFile()) {
            String commands[] = {NpmUtils.findNpm(), NpmConstants.NPM_AUDIT, NpmConstants.NPM_FIX, NpmConstants.NPM_FORCE};
            ExecCommandUtil.executeCommandWithLog(new File(basePath), CoreMessages.get(CoreMessages.npmAuditFix, basePath), null, commands);
        } else {
            PreferenceUtil.openPreference(null, CoreConfigurable.class);
            return false;
        }

        return true;
    }

    private static void replaceToolsPath(@NotNull File toolsFolder) {
        if (!toolsFolder.exists()) {
            CommonUtil.log(Log.LEVEL_ERROR, IOSSimUtil.class, "Replace Tools path failed! Tools folder is not exist!");
            return;
        }

        File gradleFile = new File(toolsFolder, "gradle/bin/gradle");
        if (!gradleFile.exists()) {
            CommonUtil.log(Log.LEVEL_ERROR, IOSSimUtil.class, "Replace Tools path failed! gradle file is not exist!");
            return;
        }

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(gradleFile);
            String data = new String(input.readAllBytes(), Charset.defaultCharset());
            input.close();

            output = FileUtil.openOutputStream(gradleFile, false);
            data = data.replace("%{TOOLS_PATH}", toolsFolder.getAbsolutePath());
            output.write(data.getBytes());
            output.close(); // don't swallow close Exception if copy completes normally
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, "Replace Tools path failed! : " + e.getLocalizedMessage(), e); // $NON-NLS-1#
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }


    public static @NotNull String getToolsRevision() {
        try {
            return getToolsRevision(new URL(DOWNLOADPAGE_URL + REVISION_FILE_NAME));
        } catch (MalformedURLException e) {
            CommonUtil.log(Log.LEVEL_ERROR, "getToolsRevision() failed : " + e.getLocalizedMessage(), e); // $NON-NLS-1#
        }
        return "";
    }

    public static @NotNull String getToolsRevision(@NotNull URL revision) {
        InputStream input = null;
        try {
            input = revision.openStream();
            String date = new String(input.readAllBytes(), Charset.defaultCharset());
            return date;
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, "getToolsRevision() failed : " + e.getLocalizedMessage(), e); // $NON-NLS-1#
        } finally {
            IOUtils.closeQuietly(input);
        }
        return "";
    }

    public static boolean isLatest(@NotNull File toolsFolder, String serverRevision) {
        String localRevision = "";
        try {
            File revision = new File(toolsFolder.getAbsolutePath() + File.separator + REVISION_FILE_NAME);
            localRevision = getToolsRevision(revision.toURI().toURL());
        } catch (MalformedURLException e) {
            return false;
        }

        return localRevision.compareTo(serverRevision) >= 0;
    }

    public static @Nullable File getToolsFolder() {
        /**
         * Comment		: resources/tools 파일을 application data 로 복사
         */
        String dataPath = CommonUtil.getAppDataLocation();
        File toolsFolder = new File(dataPath, TOOLS_FOLDER_NAME);
        if (toolsFolder.exists()) {
            return toolsFolder;
        }

        return null;
    }


    public static @NotNull String getGradleLocation() {
        @Nullable File toolsFolder = getToolsFolder();
        if (toolsFolder == null) {
            CommonUtil.log(Log.LEVEL_ERROR, CoreMessages.get(CoreMessages.GradleLocationError));
            return "";
        }

        Path gradlePath = CommonUtil.getPath(toolsFolder.getAbsolutePath(), "gradle", "bin");

        if (OSUtil.isMac()) {
            gradlePath = CommonUtil.getPath(gradlePath, "gradle");
        } else if (OSUtil.isWindows()) {
            gradlePath = CommonUtil.getPath(gradlePath, "gradle.bat");
        }

        return gradlePath.toAbsolutePath().toString();
    }

    public static @NotNull String getPackageApplicationLocation() {
        @Nullable File toolsFolder = getToolsFolder();
        if (toolsFolder == null) {
            CommonUtil.log(Log.LEVEL_ERROR, CoreMessages.get(CoreMessages.PackageApplicationLocationError));
            return "";
        }

        Path packageApplicationPath = CommonUtil.getPath(toolsFolder.getAbsolutePath(), "ios", "packaging", "PackageApplication");
        File packageApplicationFile = new File(packageApplicationPath.toString());

        if (packageApplicationFile.exists()) {
            packageApplicationFile.setExecutable(true);
            packageApplicationFile.setExecutable(true, false);
        }

        return packageApplicationFile.getAbsolutePath().toString();
    }

    public static @NotNull String getGradleLibsDebugLocation() {
        @Nullable File toolsFolder = getToolsFolder();
        if (toolsFolder == null) {
            CommonUtil.log(Log.LEVEL_ERROR, CoreMessages.get(CoreMessages.GradleLibsDebugLocationError));
            return "";
        }

        Path gradlePath = CommonUtil.getPath(toolsFolder.getAbsolutePath(), "gradle-libs", "debug");
        return gradlePath.toAbsolutePath().toString();
    }

    public static @NotNull String getGradleLibsReleaseLocation() {
        @Nullable File toolsFolder = getToolsFolder();
        if (toolsFolder == null) {
            CommonUtil.log(Log.LEVEL_ERROR, CoreMessages.get(CoreMessages.GradleLibsReleaseLocationError));
            return "";
        }

        Path gradlePath = CommonUtil.getPath(toolsFolder.getAbsolutePath(), "gradle-libs", "release");
        return gradlePath.toAbsolutePath().toString();
    }

    public static @NotNull String getIOSDeployLocation() {
//        if (checkIOSDeploy()) {
//            return getWitchIOSDeploy();
//        }

        @Nullable File toolsFolder = getToolsFolder();
        if (toolsFolder == null) {
            CommonUtil.log(Log.LEVEL_ERROR, CoreMessages.get(CoreMessages.IOSDeployLocationError));
            return "";
        }

        Path iosDeployPath = CommonUtil.getPath(toolsFolder.getAbsolutePath(), "ios-deploy", "ios-deploy");
        return iosDeployPath == null ? "" : iosDeployPath.toAbsolutePath().toString();
    }

    private static String getWitchIOSDeploy() {
        String[] commands;
        if (OSUtil.isMac()) {
            commands = new String[]{"which", "ios-deploy"};
        } else {
            commands = new String[]{"cmd", "/c", "where", "ios-deploy"};
        }
        StringBuilder command = new StringBuilder();
        try {
            String task = "getWitchIOSDeploy() ]";
            ExecCommandUtil.printCommands(task, commands);

            Process process = ExecCommandUtil.excuteCommand(commands);
            GrabProcessOutput.grabProcessOutput(process, Wait.WAIT_FOR_PROCESS, new IProcessOutput() {
                @Override
                public void out(@Nullable String line) {
                    // Ignore stdout
                    if (line != null) {
                        if (!line.isEmpty()) {
                            command.append(line);
                        }
                    }
                }

                @Override
                public void err(@Nullable String line) {
                    if (line != null && !line.isEmpty() && !line.equals("null")) {
                        String log = "[getWitchIOSDeploy error] " + line;
                        CommonUtil.log(Log.LEVEL_ERROR, log);
                    }
                }
            });
            Thread.sleep(500);
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            String log = "[getWitchIOSDeploy error : " + e.toString() + "] " + e.getMessage();
            CommonUtil.log(Log.LEVEL_ERROR, log);
        }
        return command.toString();
    }

    public static @NotNull String installIOSDeploy() {
        String[] commands = {"npm", "install", "-g", "ios-deploy"};

        return ExecCommandUtil.execProcessHandler(FrameworkMessages.get(FrameworkMessages.installIosDeploy), null, commands);
    }

    private static @NotNull String getIOSDeployVersion() {
        String[] commands = {"ios-deploy", "-V"};

        return ExecCommandUtil.execProcessHandler(FrameworkMessages.get(FrameworkMessages.getIosDeployVersion), null, commands);
    }

    public static boolean checkIOSDeploy() {
        String version = getIOSDeployVersion();
        if (version == null || version.isEmpty()) {
            int ret = CommonUtil.openQuestion(FrameworkMessages.get(FrameworkMessages.morpheusCliCheck),
                    FrameworkMessages.get(FrameworkMessages.iosDeployInstallQuestion));
            if (ret == JOptionPane.YES_OPTION) {
                installIOSDeploy();
                version = getIOSDeployVersion();
            } else {
                return false;
            }
        }

        return version != null && !version.isEmpty();
    }

    public static @NotNull String getiOSSimLocation() {
        @Nullable File toolsFolder = getToolsFolder();
        if (toolsFolder == null) {
            CommonUtil.log(Log.LEVEL_ERROR, CoreMessages.get(CoreMessages.IOSSimLocationError));
            return "";
        }

        Path iosSimPath = CommonUtil.getPath(toolsFolder.getAbsolutePath(), "ios-sim", "bin", "ios-sim");
        return iosSimPath.toAbsolutePath().toString();
    }

    public static @Nullable String getBuildConfigLocation(String type) {
        @Nullable File toolsFolder = getToolsFolder();
        if (toolsFolder == null) {
            CommonUtil.log(Log.LEVEL_ERROR, CoreMessages.get(CoreMessages.ModPbxprojLocationError));
            return "";
        }

        Path configPath = CommonUtil.getPath(toolsFolder.getAbsolutePath(), "config");
        return configPath.toAbsolutePath() + File.separator + type;
    }

    public static @NotNull String findXCWorkspace(String iosProjectPath) {
        File iosProjectFolder = new File(iosProjectPath);
        if (!iosProjectFolder.isDirectory()) {
            return "";
        }

        File[] res = iosProjectFolder.listFiles();
        File xcWorkspace = null;
        if (res != null) {
            for (File r : res) {
                if (r.isDirectory()) {
                    if (r.getName().endsWith(FrameworkConstants.PROJECT_XCODE_WORKSPACE)) {
                        xcWorkspace = r;
                        break;
                    }
                }
            }
        }
        return xcWorkspace == null ? "" : xcWorkspace.getAbsolutePath();
    }

    public static void setLaunchProperties(@NotNull Project project, File gradlePropFile, @NotNull IOSRunConfiguration runConfigState, boolean isExport) throws Exception {

        String projectName = project.getName(); //$NON-NLS-1$ //$NON-NLS-2$
        IOSRunConfigField configField = runConfigState.getRunConfigField();

        Properties gradleProp = new Properties();

        IOSLaunchTargetType iosLaunchType = configField.getIosLaunchType();
        gradleProp.setProperty("applicationName", runConfigState.getApplicationName());
        gradleProp.setProperty(CoreConstants.IOS_LAUNCH_TYPE, iosLaunchType.toString());
        gradleProp.setProperty(CoreConstants.PROJECT_NAME, projectName);
        gradleProp.setProperty(CoreConstants.IOS_PROJECT_PATH, findXCWorkspace(runConfigState.getIosProjectPath()));
        gradleProp.setProperty(CoreConstants.OUTPUT_DIR/*"outputDir"*/, configField.getOutputDir());
        gradleProp.setProperty(CoreConstants.CURRENT_DIR/*"currentDir"*/, runConfigState.getIosProjectPath());

        gradleProp.setProperty(CoreConstants.IOS_DESTINATION, configField.getIosDestination());
        gradleProp.setProperty(CoreConstants.IOS_SIMULATOR_UUID, configField.getSimulatorUUid());

        gradleProp.setProperty(CoreConstants.IOS_SIMULATOR_DIR/*"iosSimDir"*/, Objects.requireNonNullElse(IOSSimUtil.getiOSSimLocation(), ""));
        gradleProp.setProperty(CoreConstants.IOS_DEPLOY_DIR/*"iosDeployDir"*/, Objects.requireNonNullElse(IOSSimUtil.getIOSDeployLocation(), ""));
        gradleProp.setProperty(CoreConstants.IOS_DEVICE_SERIAL_NUMBER, Objects.requireNonNullElse(configField.getIosDeviceSerialNumber(), ""));
        gradleProp.setProperty(CoreConstants.IOS_DEVICE_VERSION, Objects.requireNonNullElse(configField.getIosDeviceVersion(), ""));
        gradleProp.setProperty(CoreConstants.IOS_DEPLOYMENT_TARGET, Objects.requireNonNullElse(configField.getDeploymentTarget(), ""));
        gradleProp.setProperty(CoreConstants.IOS_TARGET_SDK_VERSION, Objects.requireNonNullElse(configField.getIosTargetSdkVersion(), ""));
        gradleProp.setProperty(CoreConstants.IOS_TARGET_TYPE, Objects.requireNonNullElse(configField.getIosTargetType(), ""));

        gradleProp.setProperty(CoreConstants.IOS_DEVICE_TYPE_ID, Objects.requireNonNullElse(configField.getIosDeviceTypeId(), ""));

        gradleProp.setProperty(CoreConstants.IOS_CERTIFICATE_NAME, Objects.requireNonNullElse(configField.getIosCertificateName(), ""));
        gradleProp.setProperty(CoreConstants.IOS_DEVELOPMENT_TEAM_ID, Objects.requireNonNullElse(configField.getIosDevelopmentTeam(), ""));
        gradleProp.setProperty(CoreConstants.IOS_LAUNCH_TEST, Objects.requireNonNullElse(configField.getIsLaunchTest(), "FALSE"));

        if (isExport) {
            gradleProp.setProperty(CoreConstants.OUTPUT_FILENAME/*"outputDir"*/, configField.getOutputFileName());
            gradleProp.setProperty(CoreConstants.PACKAGING_LOCATION/*"outputDir"*/, configField.getPackagingLocation());
        }

        CommonUtil.setProperties(gradlePropFile, gradleProp, projectName);
    }
}
