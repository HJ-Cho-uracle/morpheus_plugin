/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.launch;

import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.launch.configuration.IOSRunConfiguration;
import m.client.ide.morpheus.launch.configuration.LaunchState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Runner for non-Bazel run configurations (using the Flutter SDK).
 */
public class IOSRunner extends LaunchState.Runner<IOSRunConfiguration> {
    public IOSRunner() {
        super(IOSRunConfiguration.class);
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "IOSRunner";
    }

    @Override
    public boolean canRun(IOSRunConfiguration config) {
        @NotNull Project project = config.getProject();
        if (!MorpheusConfigManager.isMorpheusProject(project)) {
            return false;
        }

        @Nullable File iosProjectFolder = LaunchUtil.getIOSProjectFolder(project);
        if (iosProjectFolder == null || !iosProjectFolder.exists()) {
            return false;
        }

        return true;
    }
}
