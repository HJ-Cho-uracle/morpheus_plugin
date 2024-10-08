/*
 * Copyright 2018 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.core.utils;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessUtil;
import com.intellij.execution.process.ProcessInfo;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.messages.MorpheusNotifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XcodeUtils {

    public static boolean isSimulatorRunning() {
        final ProcessInfo[] processInfos = OSProcessUtil.getProcessList();
        for (ProcessInfo info : processInfos) {
            if (info.getExecutableName().equals("Simulator")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Open the iOS simulator.
     * <p>
     * If there's an error opening the simulator, display that to the user via
     * {@link MorpheusNotifier}.
     */
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
                MorpheusNotifier.showError("Error Opening Simulator", msg, project);
            }
        }).exceptionally(throwable -> {
            MorpheusNotifier.showError(
                    "Error Opening Simulator",
                    MessageBundle.message("morpheus.command.exception.message", throwable.getMessage()),
                    project);
            return null;
        });
    }
}
