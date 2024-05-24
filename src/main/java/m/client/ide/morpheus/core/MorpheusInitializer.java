/*
 * Copyright 2016 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.core;

import com.esotericsoftware.minlog.Log;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.startup.StartupActivity;
import m.client.ide.morpheus.core.android.IntelliJAndroidSdk;
import m.client.ide.morpheus.core.config.CoreSettingsState;
import m.client.ide.morpheus.core.messages.CoreMessages;
import m.client.ide.morpheus.core.messages.MorpheusNotifier;
import m.client.ide.morpheus.core.npm.NpmUtils;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.core.utils.ProjectUtil;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.launch.IOSLaunchTargetType;
import m.client.ide.morpheus.launch.IOSRunConfigurationType;
import m.client.ide.morpheus.launch.common.IOSSimUtil;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.launch.configuration.IOSRunConfigField;
import m.client.ide.morpheus.launch.configuration.IOSRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static m.client.ide.morpheus.launch.common.IOSSimUtil.TOOLS_FOLDER_NAME;

/**
 * Runs actions after the project has started up and the index is up to date.
 *
 * @see ProjectOpenActivity for actions that run earlier.
 * @see MorpheusProjectOpenProcessor for additional actions that
 * may run when a project is being imported.
 */
public class MorpheusInitializer implements StartupActivity {
    //    private static final String analyticsClientIdKey = "io.flutter.analytics.clientId";
    private static final String analyticsOptOutKey = "io.flutter.analytics.optOut";
    private static final String analyticsToastShown = "io.flutter.analytics.toastShown";

//  private static Analytics analytics;

    private boolean toolWindowsInitialized = false;

    @Override
    public void runActivity(@NotNull Project project) {

        // Check to see if we're on a supported version of Android Studio; warn otherwise.
        performAndroidStudioCanaryCheck(project);

        ensureAndroidSdk(project);

        if (MorpheusConfigManager.isMorpheusProject(project)) {
            if (OSUtil.isMac()) {
                autoCreateRunConfig(project);
                ensureRunConfigSelected(project);
            }
        }
    }

    public static boolean checkToolsData(Project project) {
        String dataPath = CommonUtil.getAppDataLocation();
        File toolsFolder = new File(dataPath, TOOLS_FOLDER_NAME);
        if (!toolsFolder.exists()) {
            int ret = CommonUtil.openQuestion(CoreMessages.get(CoreMessages.InitToolsData),
                    CoreMessages.get(CoreMessages.ToolsNotExist) + CoreMessages.get(CoreMessages.ToolsInitQuestion));
            if (ret == JOptionPane.YES_OPTION) {
                initialToolsData(project);
            }
            return false;
        }
        String serverRevision = IOSSimUtil.getToolsRevision();
        if (!IOSSimUtil.isLatest(toolsFolder, serverRevision) || CoreSettingsState.getInstance().isToolUpdateForce()) {
            int ret = CommonUtil.openQuestion(CoreMessages.get(CoreMessages.InitToolsData), CoreMessages.get(CoreMessages.ToolsInitQuestion));
            if (ret == JOptionPane.YES_OPTION) {
                initialToolsData(project);
            }
            return false;
        }

        return true;
    }

    private static void initialToolsData(@NotNull Project project) {
        @NotNull String npm = NpmUtils.getNpmPathWithCheck(project);
        if (npm.isEmpty()) {
            return;
        }

        try {
            IOSSimUtil.initialToolsData(project);
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
    }

    /**
     * Automatically set Android SDK based on ANDROID_HOME.
     */
    private void ensureAndroidSdk(@NotNull Project project) {
        if (ProjectRootManager.getInstance(project).getProjectSdk() != null) {
            return; // Don't override user's settings.
        }

        final IntelliJAndroidSdk wanted = IntelliJAndroidSdk.fromEnvironment();
        if (wanted == null) {
            return; // ANDROID_HOME not set or Android SDK not created in IDEA; not clear what to do.
        }

        ApplicationManager.getApplication().runWriteAction(() -> wanted.setCurrent(project));
    }


    /**
     * Ensures a ios run configuration is selected in the run pull down if selected configuration is null.
     */
    private void ensureRunConfigSelected(@NotNull Project project) {
        if (project.isDisposed()) return;
        final IOSRunConfigurationType configType = IOSRunConfigurationType.getInstance();

        final RunManager runManager = RunManager.getInstance(project);
        if (!runManager.getConfigurationsList(configType).isEmpty()) {
            if (runManager.getSelectedConfiguration() == null) {
                final List<RunnerAndConfigurationSettings> iosRunConfigs = runManager.getConfigurationSettingsList(configType);
                if (!iosRunConfigs.isEmpty()) {
                    runManager.setSelectedConfiguration(iosRunConfigs.get(0));
                }
            }
        }
    }

    /**
     * Creates a iOS run configuration if none exists.
     */
    public static void autoCreateRunConfig(@NotNull Project project) {
        assert ApplicationManager.getApplication().isReadAccessAllowed();
        if (project.isDisposed()) return;

        if (!OSUtil.isMac() || !MorpheusConfigManager.isMorpheusProject(project) ||
                !ProjectUtil.hasIOSProject(project)) {
            return;
        }

        final IOSRunConfigurationType configType = IOSRunConfigurationType.getInstance();
        final RunManager runManager = RunManager.getInstance(project);
        if (!runManager.getConfigurationsList(configType).isEmpty()) {
            // Don't create a run config if one already exists.
            return;
        }

        final RunnerAndConfigurationSettings settings = runManager.createConfiguration(project.getName(), configType.getFactory());
        final IOSRunConfiguration config = (IOSRunConfiguration) settings.getConfiguration();

        // Set config name.
        config.setName("ios");

        // Set fields.
        final IOSRunConfigField fields = new IOSRunConfigField(project);
        fields.setIosLaunchType(IOSLaunchTargetType.SIMULATOR);
        @NotNull ArrayList<LaunchUtil.SimulatorInfo> simInfos = LaunchUtil.getIOSSimulators();
        if (simInfos.size() > 0) {
            fields.setIosTargetSdkVersion(simInfos.get(0).getOs());
        }

        runManager.addConfiguration(settings);
//        runManager.setSelectedConfiguration(settings);
    }

    private void performAndroidStudioCanaryCheck(Project project) {
        if (!CommonUtil.isAndroidStudio()) {
            return;
        }

        final ApplicationInfo info = ApplicationInfo.getInstance();
        if (info.getFullVersion().contains("Canary") && !info.getBuild().isSnapshot()) {
            MorpheusNotifier.showWarning(
                    "Unsupported Android Studio version",
                    "Canary versions of Android Studio are not supported by the Flutter plugin.",
                    project);
        }
    }
}
