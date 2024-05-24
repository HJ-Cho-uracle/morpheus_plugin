/*
 * Copyright 2016 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.launch.action;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.ide.ActivityTracker;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.util.Condition;
import com.intellij.util.ModalityUiUtil;
import icons.UIIcons;
import m.client.ide.morpheus.core.utils.RefreshDeviceInfoNotifier;
import m.client.ide.morpheus.launch.IOSLaunchTargetType;
import m.client.ide.morpheus.launch.IOSRunConfigurationType;
import m.client.ide.morpheus.launch.common.DeviceService;
import m.client.ide.morpheus.launch.common.IOSRunningDevice;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.launch.configuration.IOSRunConfigField;
import m.client.ide.morpheus.launch.configuration.IOSRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DeviceSelectorAction extends ComboBoxAction implements DumbAware {
    private final List<AnAction> actions = new ArrayList<>();
    private final List<Project> knownProjects = Collections.synchronizedList(new ArrayList<>());

    private Collection<IOSRunningDevice> devices;
    private SelectDeviceAction selectedDeviceAction;

    DeviceSelectorAction() {
        setSmallVariant(true);
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.addAll(actions);
        return group;
    }

    @Override
    protected boolean shouldShowDisabledActions() {
        return true;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Only show device menu when the device daemon process is running.
        final Project project = e.getProject();
        if (!isSelectorVisible(project)) {
            e.getPresentation().setVisible(false);
            return;
        }

        super.update(e);

        final Presentation presentation = e.getPresentation();
        if (!knownProjects.contains(project)) {
            knownProjects.add(project);
            final Application application = ApplicationManager.getApplication();
            application.getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
                @Override
                public void projectClosed(@NotNull Project closedProject) {
                    knownProjects.remove(closedProject);
                }
            });
            Runnable deviceListener = () -> queueUpdate(project, e.getPresentation());
            DeviceService.getInstance(project).addListener(deviceListener);

            project.getMessageBus().connect().subscribe(
                    RefreshDeviceInfoNotifier.REFRESH_DEVICE_TOPIC,
                    new RefreshDeviceInfoNotifier() {
                        @Override
                        public void beforeAction() {
                        }

                        @Override
                        public void afterAction() {
                            selectedDeviceAction = null;
                        }
                    });

            update(project, presentation);
        }

        if (devices.isEmpty()) {
            presentation.setText("<no devices>");
        } else if (selectedDeviceAction != null) {
            final Presentation template = selectedDeviceAction.getTemplatePresentation();
            presentation.setIcon(template.getIcon());
            presentation.setText(selectedDeviceAction.presentationName());
            presentation.setEnabled(true);
        } else {
            presentation.setText("<no device selected>");
        }
    }

    private void queueUpdate(@NotNull Project project, @NotNull Presentation presentation) {
        ModalityUiUtil.invokeLaterIfNeeded(
                ModalityState.defaultModalityState(),
                () -> update(project, presentation));
    }

    private void update(@NotNull Project project, @NotNull Presentation presentation) {
        if (project.isDisposed()) {
            return; // This check is probably unnecessary, but safe.
        }
        updateActions(project, presentation);
        updateVisibility(project, presentation);
    }

    private static void updateVisibility(final Project project, final @NotNull Presentation presentation) {
        final boolean visible = isSelectorVisible(project);
        presentation.setVisible(visible);

        final JComponent component = (JComponent) presentation.getClientProperty("customComponent");
        if (component != null) {
            component.setVisible(visible);
            if (component.getParent() != null) {
                component.getParent().doLayout();
                component.getParent().repaint();
            }
        }
    }

    private static boolean isSelectorVisible(@Nullable Project project) {
        return project != null && getSelectedIOSConfigField(project) != null;
    }

    public static @Nullable IOSRunConfigField getSelectedIOSConfigField(Project project) {
        final IOSRunConfigurationType configType = IOSRunConfigurationType.getInstance();

        final RunManager runManager = RunManager.getInstance(project);
        RunnerAndConfigurationSettings selectedConfiguration = runManager.getSelectedConfiguration();
        if (selectedConfiguration != null && selectedConfiguration.getType().equals(configType)) {
            final IOSRunConfiguration config = (IOSRunConfiguration) selectedConfiguration.getConfiguration();
            return config.getRunConfigField();
        }

        return null;
    }


    private void updateActions(@NotNull Project project, Presentation presentation) {
        actions.clear();
        selectedDeviceAction = null;

        @Nullable IOSRunConfigField selectedIosConfigField = getSelectedIOSConfigField(project);
        devices = LaunchUtil.getIOSRunningDevices(project, true);

        IOSLaunchTargetType deviceType = IOSLaunchTargetType.SIMULATOR;
        for (IOSRunningDevice device : devices) {
            if (devices.size() > 1 && deviceType != device.getType()) {
                actions.add(new Separator());
                deviceType = device.getType();
            }
            final SelectDeviceAction deviceAction = new SelectDeviceAction(device);
            actions.add(deviceAction);

            String deviceName = device.getName();
            if (selectedIosConfigField != null && selectedIosConfigField.getActionPresentationText().equals(deviceName)) {
                selectedDeviceAction = deviceAction;

                final Presentation template = deviceAction.getTemplatePresentation();
                presentation.setIcon(template.getIcon());
                presentation.setText(deviceAction.presentationName());
                presentation.setEnabled(true);
            }
        }

        ActivityTracker.getInstance().inc();
    }

    // Show the current device as selected when the combo box menu opens.
    @Override
    protected Condition<AnAction> getPreselectCondition() {
        return action -> action == selectedDeviceAction;
    }

    private static class SelectDeviceAction extends AnAction {
        @NotNull
        private final IOSRunningDevice device;

        SelectDeviceAction(@NotNull IOSRunningDevice device) {
            super(device.getName(), null, UIIcons.PhoneIcon);
            this.device = device;
        }

        public String presentationName() {
            return device.getName();
        }

        public IOSRunningDevice getDevice() {
            return device;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            final Project project = e.getProject();
            final @Nullable IOSRunConfigField iosRunConfigField = project == null ? null : getSelectedIOSConfigField(project);
            if (iosRunConfigField != null) {
                iosRunConfigField.setSelectedDevice(device);
            }
            final DeviceService service = project == null ? null : DeviceService.getInstance(project);
            if (service != null) {
                service.setSelectedDevice(device);
            }
        }
    }
}
