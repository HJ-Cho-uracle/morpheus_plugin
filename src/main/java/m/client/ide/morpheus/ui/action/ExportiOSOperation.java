package m.client.ide.morpheus.ui.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.ExecCommandUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.framework.FrameworkConstants;
import m.client.ide.morpheus.launch.IOSRunConfigurationType;
import m.client.ide.morpheus.launch.common.IOSSimUtil;
import m.client.ide.morpheus.launch.configuration.IOSRunConfigField;
import m.client.ide.morpheus.launch.configuration.IOSRunConfiguration;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ExportiOSOperation {

    private Project project;
    private String tempPath;
    private String developerName;
    private String sdkVersion;
    private String sdkType;
    private String destinationPath;
    private String devTeam;

    public ExportiOSOperation(List resources, String tempPath, String developerName, String devTeam, String sdkVersion,
                              String sdkType, Project project, String destinationPath) {
        this.tempPath = tempPath;
        this.developerName = developerName;
        this.devTeam = devTeam;
        this.sdkVersion = sdkVersion;
        this.sdkType = sdkType;
        this.project = project;
        this.destinationPath = destinationPath;
    }

    public void run(ProgressIndicator indicator) throws InterruptedException {

        String tmpLocation = new File(tempPath, project.getName()).getAbsolutePath();
        try {
            indicator.setText(UIMessages.get(UIMessages.ExportiOSTask));

            String configLocation = IOSSimUtil.getBuildConfigLocation("export");
//			storeProperties(indicator, configLocation);
            storeLauncherProperties(indicator, configLocation);

            indicator.setText2(UIMessages.get(UIMessages.ExportiOSDeploying));
            String gradleLocation = IOSSimUtil.getGradleLocation();
            String[] commands = {gradleLocation, "--project-dir", tmpLocation, "-b",
                    CommonUtil.getPathString(configLocation, true, "export-ios.gradle"), "packaging"};

            ExecCommandUtil.executeCommandWithLog(UIMessages.get(UIMessages.ExportiOSDeploying), commands);
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
        } finally {
            // 파일 삭제
            indicator.setText2(UIMessages.get(UIMessages.ExportiOSDeleteTemp));
            try {
                if (OSUtil.isWindows()) {
                    String[] commands = {"cmd.exe", "/c", "rmdir", "/s", "/q", tmpLocation};
                    ExecCommandUtil.excuteCommand(commands);
                } else {
                    String[] commands = {"rm", "-rf", tmpLocation};
                    ExecCommandUtil.excuteCommand(commands);
                }
            } catch (IOException e) {
                CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
            }
        }
    }

    private void storeLauncherProperties(@NotNull ProgressIndicator indicator, String configLocation) throws Exception {
        indicator.setText2(UIMessages.get(UIMessages.ExportiOSTasking));

        final IOSRunConfigurationType configType = IOSRunConfigurationType.getInstance();
        final RunManager runManager = RunManager.getInstance(project);
        if (!runManager.getConfigurationsList(configType).isEmpty()) {
            RunnerAndConfigurationSettings selectedConfiguration = runManager.getSelectedConfiguration();
            if (selectedConfiguration != null && selectedConfiguration.getType().equals(configType)) {
                final IOSRunConfiguration config = (IOSRunConfiguration) selectedConfiguration.getConfiguration();
                IOSRunConfiguration iosRunConfig = config.clone();
                if (!iosRunConfig.isValidate(project)) {
                    return;
                }

                File buildFolder = CommonUtil.getPathFile(iosRunConfig.getIosProjectPath(), "build");
                if (buildFolder.exists()) {
                    FileUtil.deleteDirectory(buildFolder);
                }

                File gradlePropFile = CommonUtil.getPathFile(configLocation, FrameworkConstants.GRADLE_PROPERTIES_FILE); //$NON-NLS-1$ //$NON-NLS-2$
                IOSRunConfigField configField = iosRunConfig.getRunConfigField();
                configField.setIosCertificateName(developerName);
                configField.setIosDevelopmentTeam(devTeam);
                configField.setIosTargetSdkVersion(sdkVersion);
                configField.setIosTargetType(sdkType);

                File destination = new File(destinationPath);
                String name = destination.getName();
                configField.setOutputDir(destination.getParent());
                configField.setOutputFileName(name.substring(0, name.lastIndexOf('.')));
                configField.setPackagingLocation(IOSSimUtil.getPackageApplicationLocation());
                IOSSimUtil.setLaunchProperties(project, gradlePropFile, iosRunConfig, true);
            }
        }
    }
}
