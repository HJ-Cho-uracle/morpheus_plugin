/*
 * Copyright 2020 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.launch.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import icons.FlutterIcons;
import m.client.ide.morpheus.core.utils.RefreshDeviceInfoNotifier;
import m.client.ide.morpheus.launch.common.DeviceService;
import m.client.ide.morpheus.launch.common.IOSRunningDevice;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DeviceSelectorRefresherAction extends AnAction {
    public DeviceSelectorRefresherAction() {
        super(FlutterIcons.RefreshItems);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        if (project != null) {
            RefreshDeviceInfoNotifier publisher = project.getMessageBus().syncPublisher(RefreshDeviceInfoNotifier.REFRESH_DEVICE_TOPIC);
            try {
                @NotNull DeviceService service = DeviceService.getInstance(project);
                @Nullable IOSRunningDevice selectedDevice = service.getSelectedDevice();

                Collection<IOSRunningDevice> devices = LaunchUtil.getIOSRunningDevices(project, true);
                for (IOSRunningDevice device : devices) {
                    if (device.equals(selectedDevice)) {
                        service.setSelectedDevice(device);
                        return;
                    }
                }
                service.setSelectedDevice(null);
            } finally {
                publisher.afterAction();
            }
        }
    }
}
