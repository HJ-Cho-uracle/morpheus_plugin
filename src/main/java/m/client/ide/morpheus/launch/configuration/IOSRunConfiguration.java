/*
 * Copyright 2016 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.launch.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import m.client.ide.morpheus.core.MorpheusInitializer;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.ExecCommandUtil;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.framework.messages.FrameworkMessages;
import m.client.ide.morpheus.launch.IOSLaunchTargetType;
import m.client.ide.morpheus.launch.action.PodInstallAction;
import m.client.ide.morpheus.launch.common.IOSSimUtil;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.launch.configeditor.IOSConfigurationEditorForm;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Run configuration used for launching a Flutter app using the Flutter SDK.
 */
public class IOSRunConfiguration extends LocatableConfigurationBase<LaunchState>
        implements LaunchState.RunConfig, RunConfigurationWithSuppressedDefaultRunAction {

    private String iosProjectPath;
    private String applicationName;
    private String projectName;
    private String bundleId;

    private IOSRunConfigField runConfigField;
    private boolean firstRun = true;

    public IOSRunConfiguration(final @NotNull Project project, final @NotNull ConfigurationFactory factory, final @NotNull String name) {
        super(project, factory, name);

        MorpheusConfigManager morpheusConfig = new MorpheusConfigManager(project);

        iosProjectPath = LaunchUtil.getIOSProjectPath(project);
        applicationName = morpheusConfig.getIOSAppName();
        projectName = project.getName();
        bundleId = morpheusConfig.getBundleId();

        runConfigField = new IOSRunConfigField(project);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new IOSConfigurationEditorForm(getProject());
    }

    @NotNull
    @Override
    public LaunchState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        final Project project = env.getProject();
        final LaunchState launcher = new LaunchState(env, project.getProjectFile().getParent(), project.getProjectFile(), this);
        return launcher;
    }

    @NotNull
    @Override
    public GeneralCommandLine getCommand(@NotNull ExecutionEnvironment env) throws ExecutionException {
        String gradleLocation = IOSSimUtil.getGradleLocation();
        String configLocation = IOSSimUtil.getBuildConfigLocation("run");
        @NotNull Project project = env.getProject();

        String xcWorkspace = IOSSimUtil.findXCWorkspace(iosProjectPath);
        if (xcWorkspace == null) {
            CommonUtil.openWarningDialog("Warning", FrameworkMessages.get(FrameworkMessages.XCWorkspaceNotExist));
            return new GeneralCommandLine();
        }

        String[] commands = {gradleLocation, "--project-dir", iosProjectPath, "-b",
                CommonUtil.getPathString(configLocation, true, "run-ios.gradle"), "launch"};

        Map<String, String> envs = null;

        String iosSim = IOSSimUtil.getiOSSimLocation();
        if (iosSim != null && iosSim.length() > 0) {
            envs = new HashMap<String, String>();
            File f = new File(iosSim);
            if (f.exists()) {
                String path = envs.get(ExecCommandUtil.ENVIRONMENT_PATH);
                if (path != null && !path.isEmpty()) {
                    path = path + ";" + f.getParent();
                } else {
                    path = f.getParent();
                }
                envs.put(ExecCommandUtil.ENVIRONMENT_PATH, path);
            }
        }

        @NotNull GeneralCommandLine generalCommandLine = ExecCommandUtil.createGeneralCommandLine(project, null, commands, envs);
        return generalCommandLine;
    }

    /**
     * @param target
     * @return
     */
    @Override
    public boolean canRunOn(@NotNull ExecutionTarget target) {
        return super.canRunOn(target);
    }

    @Override
    public IOSRunConfiguration clone() {
        final IOSRunConfiguration clone = (IOSRunConfiguration) super.clone();
        if (runConfigField != null) {
            clone.setRunConfigField(runConfigField.copy());
        }

        return clone;
    }

    public IOSRunConfigField getRunConfigField() {
        return runConfigField;
    }

    public void setRunConfigField(IOSRunConfigField runConfigField) {
        this.runConfigField = runConfigField;
    }

    /**
     * @throws RuntimeConfigurationException
     */
    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        runConfigField.checkRunnable(getProject());
    }

    public String getIosProjectPath() {
        return iosProjectPath;
    }

    public void setIosProjectPath(String iosProjectPath) {
        this.iosProjectPath = iosProjectPath;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    @Override
    public void writeExternal(@NotNull final Element element) throws WriteExternalException {
        super.writeExternal(element);
        runConfigField.writeTo(element);
    }

    @Override
    public void readExternal(@NotNull final Element element) throws InvalidDataException {
        super.readExternal(element);
        runConfigField = IOSRunConfigField.readFrom(getProject(), element);
    }

    public boolean isValidate(Project project) {
        IOSLaunchTargetType iosLaunchType = runConfigField.getIosLaunchType();

        if(!MorpheusInitializer.checkToolsData(project)) {
            return false;
        }

        if (IOSSimUtil.findXCWorkspace(getIosProjectPath()).isEmpty()) {
            CommonUtil.openWarningDialog("Warning", FrameworkMessages.get(FrameworkMessages.XCWorkspaceNotExist));
            PodInstallAction.podInstall(project);
            return false;
        }

        if (iosLaunchType == IOSLaunchTargetType.DEVICE) {
            if (runConfigField.getIosDevelopmentTeam().isEmpty()) {
                CommonUtil.openWarningDialog("Warning", FrameworkMessages.get(FrameworkMessages.SelectDevelopment));
                LaunchUtil.openRunConfigurationEditor(project);
                return false;
            }
            if (runConfigField.getIosDeviceSerialNumber().isEmpty()) {
                CommonUtil.openWarningDialog("Warning", FrameworkMessages.get(FrameworkMessages.SelectDevice));
                LaunchUtil.openRunConfigurationEditor(project);
                return false;
            }
        } else {
            if (runConfigField.getIosTargetType().isEmpty() || runConfigField.getIosTargetSdkVersion().isEmpty()) {
                CommonUtil.openWarningDialog("Warning", FrameworkMessages.get(FrameworkMessages.SelectSimulator));
                LaunchUtil.openRunConfigurationEditor(project);
                return false;
            }
        }

        return true;
    }
}
