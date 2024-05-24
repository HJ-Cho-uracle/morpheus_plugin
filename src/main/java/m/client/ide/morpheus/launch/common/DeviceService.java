/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.launch.common;

import com.google.common.collect.ImmutableSet;
import com.intellij.ide.ActivityTracker;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ex.ProjectRootManagerEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides the list of available devices (mobile phones or emulators) that appears in the dropdown menu.
 */
public class DeviceService {
    @NotNull
    private final Project project;

    private final AtomicReference<ImmutableSet<Runnable>> listeners = new AtomicReference<>(ImmutableSet.of());

    private boolean refreshInProgress = false;
    private IOSRunningDevice selectedDevice;

    @NotNull
    public static DeviceService getInstance(@NotNull final Project project) {
        return Objects.requireNonNull(project.getService(DeviceService.class));
    }

    private DeviceService(@NotNull final Project project) {
        this.project = project;

        // Watch for Java SDK changes. (Used to get the value of ANDROID_HOME.)
        ProjectRootManagerEx.getInstanceEx(project).addProjectJdkListener(this::refreshDeviceDaemon);
    }

    /**
     * Adds a callback for any changes to the status, device list, or selection.
     */
    public void addListener(@NotNull Runnable callback) {
        listeners.updateAndGet((old) -> {
            final List<Runnable> changed = new ArrayList<>(old);
            changed.add(callback);
            return ImmutableSet.copyOf(changed);
        });
    }

    public void removeListener(@NotNull Runnable callback) {
        listeners.updateAndGet((old) -> {
            final List<Runnable> changed = new ArrayList<>(old);
            changed.remove(callback);
            return ImmutableSet.copyOf(changed);
        });
    }

    public boolean isRefreshInProgress() {
        return refreshInProgress;
    }

    /**
     * Returns the currently selected device.
     * <p>
     * <p>When there is no device list (perhaps because the daemon isn't running), this will be null.
     */
    @Nullable
    public IOSRunningDevice getSelectedDevice() {
        return selectedDevice;
    }

    public void setSelectedDevice(@Nullable IOSRunningDevice device) {
        selectedDevice = device;
        fireChangeEvent();
    }

    private void fireChangeEvent() {
        SwingUtilities.invokeLater(() -> {
            if (project.isDisposed()) return;
            for (Runnable listener : listeners.get()) {
                try {
                    listener.run();
                } catch (Exception e) {
                    LOG.warn("DeviceDaemon listener threw an exception", e);
                }
            }
        });
    }

    /**
     * Updates the device daemon to what it should be based on current configuration.
     * <p>
     * <p>This might mean starting it, stopping it, or restarting it.
     */
    private void refreshDeviceDaemon() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            DumbService.getInstance(project).waitForSmartMode();
            if (project.isDisposed()) return;
            refreshInProgress = false;
            ActivityTracker.getInstance().inc();
        });
    }

    private static final Logger LOG = Logger.getInstance(DeviceService.class);
}
