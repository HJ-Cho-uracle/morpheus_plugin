/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.ui.action;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.ExecCommandUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenSimulatorAction extends AnAction {
    final boolean enabled;

    public OpenSimulatorAction(boolean enabled) {
        super("Open iOS Simulator");

        this.enabled = enabled;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(enabled);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        @Nullable final Project project = event.getProject();

        // Check to see if the simulator is already running. If it is, and we're here, that means there are
        // no running devices and we want to issue an extra call to start (w/ `-n`) to load a new simulator.
        // TODO(devoncarew): Determine if we need to support this code path.
        //if (XcodeUtils.isSimulatorRunning()) {
        //  if (XcodeUtils.openSimulator("-n") != 0) {
        //    // No point in trying if we errored.
        //    return;
        //  }
        //}

        openSimulator(project);
    }

    public static void openSimulator(@Nullable Project project, String... additionalArgs) {
        final List<String> params = new ArrayList<>(Arrays.asList(additionalArgs));
        params.add("-a");
        params.add("Simulator.app");

        final GeneralCommandLine cmd = new GeneralCommandLine().withExePath("open").withParameters(params);

        ExecCommandUtil.execAndGetOutput(cmd).thenAccept((ProcessOutput output) -> {
            if (output.getExitCode() != 0) {
                final StringBuilder textBuffer = new StringBuilder();
                if (!output.getStdout().isEmpty()) {
                    textBuffer.append(output.getStdout());
                }
                if (!output.getStderr().isEmpty()) {
                    if (!textBuffer.toString().isEmpty()) {
                        textBuffer.append("\n");
                    }
                    textBuffer.append(output.getStderr());
                }

                final String eventText = textBuffer.toString();
                final String msg = !eventText.isEmpty() ? eventText : "Process error - exit code: (" + output.getExitCode() + ")";
                CommonUtil.openErrorDialog("Error Opening Simulator", msg);
            }
        }).exceptionally(throwable -> {
            CommonUtil.openErrorDialog(
                    "Error Opening Simulator",
                    MessageBundle.message("morpheus.command.exception.message", throwable.getMessage()));
            return null;
        });
    }
}
