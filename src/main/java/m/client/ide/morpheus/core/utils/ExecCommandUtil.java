package m.client.ide.morpheus.core.utils;

import com.android.annotations.Nullable;
import com.android.utils.GrabProcessOutput;
import com.android.utils.GrabProcessOutput.IProcessOutput;
import com.android.utils.GrabProcessOutput.Wait;
import com.esotericsoftware.minlog.Log;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.execution.util.ExecUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Key;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.io.IdeUtilIoBundle;
import m.client.ide.morpheus.core.config.CoreSettingsState;
import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.npm.NpmUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("restriction")
public class ExecCommandUtil {
    private static final Logger LOG = Logger.getInstance(ExecCommandUtil.class);
    public static final String ENVIRONMENT_PATH = "PATH";

    public static void printCommands(String task, String[] commands) {
        if (PreferenceUtil.getShowDebugMessage()) {
            StringBuilder command = new StringBuilder(task + "] ");
            for (String cmd : commands) {
                command.append(cmd + " ");
            }
            CommonUtil.log(Log.LEVEL_DEBUG, ExecCommandUtil.class.getClass(), command.toString());
        }
    }

    public static @NotNull String findPathWithWhich(String command) {
        final GeneralCommandLine commdLine = new GeneralCommandLine()
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
                .withExePath("which")
                .withParameters(command);

//        return execProcessHandler(null, "'" + command + "' find path with which", commands);
        StringBuilder result = new StringBuilder();
        if (execCommandLine(commdLine, result)) {
            return result.toString();
        }

        return "";
    }

    public static boolean execCommandLine(@NotNull GeneralCommandLine cmd, @NotNull StringBuilder result) {
        CommonUtil.log(Log.LEVEL_DEBUG, "execCommandLine] Execution command : " + cmd.getCommandLineString());

        try {
            @NotNull String out = getProcessOutput(cmd);
            result.append(/*ScriptRunnerUtil.*/out);
            CommonUtil.log(Log.LEVEL_DEBUG, "execCommandLine] Complete : " + cmd.getCommandLineString() + " => " + result.toString());
        } catch (ExecutionException e) {
            CommonUtil.log(Log.LEVEL_ERROR, "execCommandLine [error : " + e.toString() + "] " + e.getMessage());
            return false;
        }
        return true;
    }

    public static @NotNull Process excuteCommand(String... commands) throws IOException {
        return excuteCommand(null, null, commands);
    }

    public static @NotNull Process excuteCommand(File cnw, String... commands) throws IOException {
        return excuteCommand(cnw, null, commands);
    }

    public static @NotNull Process excuteCommand(File cnw, Map<String, String> envs, String... commands) throws IOException {
        ProcessBuilder builder = createProcessBuilder(cnw, envs, commands);

        return builder.start();
    }

    private static @NotNull ProcessBuilder createProcessBuilder(Map<String, String> envs, String[] commands) {
        return createProcessBuilder("", envs, commands);
    }

    private static @NotNull ProcessBuilder createProcessBuilder(String cwd, Map<String, String> envs, String[] commands) {
        return createProcessBuilder(cwd == null || cwd.isEmpty() ? null : new File(cwd), envs, commands);
    }

    private static @NotNull ProcessBuilder createProcessBuilder(File cwd, Map<String, String> envs, String[] commands) {
        ProcessBuilder builder = new ProcessBuilder(commands);
        if (cwd != null) {
            CommonUtil.log(Log.LEVEL_DEBUG, "[createProcessBuilder] Setting cwd '" + cwd.getPath() + "'.");
            builder.directory(cwd);
        }

        if (envs != null) {
            Map<String, String> sysEnv = builder.environment();
            Iterator<String> iter = envs.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                String value = envs.get(key);

                String sysValue = sysEnv.get(key);
                if (sysValue == null) {
                    sysValue = value;
                } else {
                    if (!sysValue.endsWith(":")) {
                        sysValue += ":";
                    }
                    sysValue += value;
                }
                sysEnv.put(key, sysValue);
            }
        }
        return builder;
    }

    public static void executeCommandWithLog(String task, String... commands) {
        executeCommandWithLog(task, null, commands);
    }

    public static void executeCommandWithLog(String task, Map<String, String> envs, String... commands) {
        executeCommandWithLog(null, task, envs, commands);
    }

    public static void executeCommandWithLog(File cwd, String task, Map<String, String> envs, String... commands) {
        // TODO Auto-generated method stub
        try {
            CommonUtil.log(Log.LEVEL_DEBUG, "[executeCommandWithLog] Starting '" + task + "'.");

            ProcessBuilder builder = createProcessBuilder(cwd, envs, commands);
            Process process = builder.start();

            CommonUtil.log(Log.LEVEL_DEBUG, "[executeCommandWithLog] GrabProcessOutput '" + process + "'.");
            GrabProcessOutput.grabProcessOutput(process, Wait.WAIT_FOR_PROCESS, new IProcessOutput() {
                @Override
                public void out(@Nullable String line) {
                    // Ignore stdout
                    if (line != null) {
                        if (!line.isEmpty()) {
                            String log = "[executeCommandWithLog]" + line;
                            CommonUtil.log(Log.LEVEL_DEBUG, log);
                        }
                    } else {
                        String log = "[executeCommandWithLog] Complete '" + task + "'.";
                        CommonUtil.log(Log.LEVEL_DEBUG, log);
                    }
                }

                @Override
                public void err(@Nullable String line) {
                    if (line != null && !line.isEmpty() && !line.equals("null")) {
                        CommonUtil.log(Log.LEVEL_ERROR, "[executeCommandWithLog error] " + line);
                    }
                }
            });
            Thread.sleep(500);
            process.waitFor();
        } catch (Exception e) {
            String log = "[executeCommandWithLog error : " + e.toString() + "] " + e.getMessage();
            CommonUtil.log(Log.LEVEL_ERROR, log);
        }
    }

    public static boolean runtimeExecCommandWithLog(String task, String success, String[] envp, File dir, String... commands) {
        // 			Process process = Runtime.getRuntime().exec(commands);
        AtomicBoolean ret = new AtomicBoolean(false);
        try {

            CommonUtil.log(Log.LEVEL_DEBUG, "[process] Starting '" + task + "'. " + (dir == null ? "" : " [dir : " + dir.getAbsolutePath()));

            Process process = Runtime.getRuntime().exec(commands, envp, dir);

            GrabProcessOutput.grabProcessOutput(process, Wait.WAIT_FOR_PROCESS, new IProcessOutput() {
                @Override
                public void out(@Nullable String line) {
                    // Ignore stdout
                    if (line != null) {
                        if (!line.isEmpty()) {
                            CommonUtil.log(Log.LEVEL_DEBUG, "[out]" + line);

                            if (success != null && !success.isEmpty() && line.contains(success)) {
                                ret.set(true);
                            }
                        }
                    } else {
                        CommonUtil.log(Log.LEVEL_DEBUG, "[out] Complete '" + task + "'.");
                    }
                }

                @Override
                public void err(@Nullable String line) {
                    if (line != null && !line.isEmpty() && !line.equals("null")) {
                        CommonUtil.log(Log.LEVEL_ERROR, "[error] " + line);
                    }
                }
            });
            Thread.sleep(500);
            process.waitFor();
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, "[error : " + e.toString() + "] " + e.getMessage());
        }

        return ret.get();
    }

    public static @NotNull String execProcessHandler(String[] commands) {
        return execProcessHandler("", null, commands);
    }

    public static @NotNull String execProcessHandler(Project project, String task, String[] commands) {
        Map<String, String> envs = null;
        if (PreferenceUtil.getShowCLIDebug()) {
            envs = new HashMap<String, String>();
            envs.put("DEBUG", "*");
        }
        return execProcessHandler(task, project, null, commands, envs);
    }

    public static @NotNull String execProcessHandler(String task, File cwd, String[] commands) {
        Map<String, String> envs = null;
        if (PreferenceUtil.getShowCLIDebug()) {
            envs = new HashMap<String, String>();
            envs.put("DEBUG", "*");
        }
        return execProcessHandler(task, null, cwd, commands, envs);
    }

    public static @NotNull GeneralCommandLine createGeneralCommandLine(Project project, File cwd, String[] commands, @org.jetbrains.annotations.Nullable Map<String, String> envs) {
        GeneralCommandLine generalCommandLine = new GeneralCommandLine(commands);
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.withEnvironment(System.getenv());
        if (envs == null) {
            envs = new HashMap<String, String>();
        }

        Map<String, String> sysEnv = generalCommandLine.getEnvironment();
        String addedNpmPath = NpmUtils.addNpmPath(sysEnv.get(ENVIRONMENT_PATH));
        sysEnv.put(ENVIRONMENT_PATH, addedNpmPath);
        Iterator<String> iter = envs.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = envs.get(key);

            String sysValue = sysEnv.get(key) == null ? "" : sysEnv.get(key);
            if (!sysValue.isEmpty() && !sysValue.endsWith(":")) {
                sysValue += ":";
            }
            sysValue += value;
            sysEnv.put(key, sysValue);
        }

        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }

        if (cwd != null) {
            generalCommandLine.setWorkDirectory(cwd);
        } else if (project.getBasePath() != null) {
            File projectFolder = new File(project.getBasePath());
            generalCommandLine.setWorkDirectory(projectFolder);
        }

        return generalCommandLine;
    }

    public static @NotNull String execProcessHandler(String task, Project project, File cwd, String[] commands, @Nullable Map<String, String> envs) {
        return execProcessHandler(task, project, cwd, commands, envs, null);
    }

    public static @NotNull String execProcessHandler(String task, Project project, File cwd, String[] commands, @Nullable Map<String, String> envs, Runnable runnable) {
        GeneralCommandLine generalCommandLine = createGeneralCommandLine(project, cwd, commands, envs);

        printCommands("execProcessHandler] Execution command ", commands);
        final StringBuilder output = new StringBuilder();
        Project finalProject = project;
        Task.Modal modalTask = new Task.Modal(finalProject, task, false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    output.append(/*ScriptRunnerUtil.*/getProcessOutput(generalCommandLine));
                    printCommands("execProcessHandler] Complete ", commands);
                    CommonUtil.log(Log.LEVEL_DEBUG, finalProject, "===================> [output : " + output.toString());

                    SwingUtilities.invokeAndWait(() -> {
                        if (runnable != null) {
                            runnable.run();
                        }
                    });
                } catch (ExecutionException | InterruptedException | InvocationTargetException e) {
                    CommonUtil.log(Log.LEVEL_ERROR, finalProject, "execProcessHandler [error : " + e.toString() + "] " + e.getMessage());
                }
            }
        };
        ExecCommandUtil.runProcessWithProgressSynchronously(modalTask, task, false, project);

        return output.toString();
    }

    public static void runProcessWithProgressSynchronously(Task.Modal modalTask, String task, boolean b, Project project) {
        @NotNull ProgressManager progressmanager = ProgressManager.getInstance();
        progressmanager.runProcessWithProgressSynchronously(new Runnable() {
            @Override
            public void run() {
                modalTask.run(progressmanager.getProgressIndicator());
            }
        }, task, false, project);
    }

    /**
     * Execute the given command line, and return the process output as one result in a future.
     * <p>
     * This is a non-blocking equivalient to {@link ExecUtil#execAndGetOutput(GeneralCommandLine)}.
     */
    public static @NotNull CompletableFuture<ProcessOutput> execAndGetOutput(GeneralCommandLine cmd) {
        final CompletableFuture<ProcessOutput> future = new CompletableFuture<>();

        AppExecutorUtil.getAppExecutorService().submit(() -> {
            try {
                final ProcessOutput output = ExecUtil.execAndGetOutput(cmd);
                future.complete(output);
            } catch (ExecutionException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public static final Condition<Key> STDOUT_OUTPUT_KEY_FILTER = (key) -> {
        return ProcessOutputTypes.STDOUT.equals(key);
    };
    public static final Condition<Key> STDERR_OUTPUT_KEY_FILTER = (key) -> {
        return ProcessOutputTypes.STDERR.equals(key);
    };
    public static final Condition<Key> STDOUT_OR_STDERR_OUTPUT_KEY_FILTER;

    static {
        STDOUT_OR_STDERR_OUTPUT_KEY_FILTER = Conditions.or(STDOUT_OUTPUT_KEY_FILTER, STDERR_OUTPUT_KEY_FILTER);
    }

    public static @NotNull String getProcessOutput(@NotNull GeneralCommandLine commandLine) throws ExecutionException {
        if (CoreSettingsState.getInstance().isDevMode()) {
            return getProcessOutput(commandLine, STDOUT_OR_STDERR_OUTPUT_KEY_FILTER, 60000L);
        } else {
            return getProcessOutput(commandLine, STDOUT_OUTPUT_KEY_FILTER, 60000L);
        }
    }

    public static @NotNull String getProcessOutput(@NotNull GeneralCommandLine commandLine, @NotNull Condition<? super Key> outputTypeFilter, long timeout) throws ExecutionException {
        return getProcessOutput((ProcessHandler) (new OSProcessHandler(commandLine)), outputTypeFilter, timeout);
    }

    public static @NotNull String getProcessOutput(@NotNull ProcessHandler processHandler, final @NotNull Condition<? super Key> outputTypeFilter, long timeout) throws ExecutionException {
        LOG.assertTrue(!processHandler.isStartNotified());
        final StringBuilder outputBuilder = new StringBuilder();
        processHandler.addProcessListener(new ProcessAdapter() {
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                if (outputTypeFilter.value(outputType)) {
                    String text = event.getText();
                    if (STDOUT_OUTPUT_KEY_FILTER.value(outputType) && !text.isEmpty()) {
                        outputBuilder.append(text);
                    } else if (STDERR_OUTPUT_KEY_FILTER.value(outputType) && !text.isEmpty()) {
                        CommonUtil.log(Log.LEVEL_DEBUG, ExecCommandUtil.class, null, text);
                    }
                }
            }
        });
        processHandler.startNotify();
//        if(!checkEdtAndReadAction(processHandler)) {
//            return outputBuilder.toString();
//        }
        if (!processHandler.waitFor(timeout)) {
            throw new ExecutionException(IdeUtilIoBundle.message("script.execution.timeout", new Object[]{String.valueOf(timeout / 1000L)}));
        } else {
            return outputBuilder.toString();
        }
    }

    public static @NotNull ShellTerminalWidget getShellWidget(Project project, String tabName, String workingDir) {
        TerminalView terminalView = TerminalView.getInstance(project);
        return getShellWidget(terminalView, tabName, workingDir);
    }

    public static @NotNull ShellTerminalWidget getShellWidget(@NotNull TerminalView terminalView, String tabName, String workingDir) {
        Set<JBTerminalWidget> shellWidgets = terminalView.getWidgets();
        ShellTerminalWidget shellWidget = null;
        for (JBTerminalWidget widget : shellWidgets) {
            if (widget instanceof ShellTerminalWidget && StringUtils.equals(widget.getToolTipText(), tabName)) {
                shellWidget = (ShellTerminalWidget) widget;
                @NotNull ContentManager contentManager = terminalView.getToolWindow().getContentManager();
                Content content = contentManager.getContent(shellWidget);
                contentManager.setSelectedContent(content);
            }
        }

        if (shellWidget == null) {
            shellWidget = terminalView.createLocalShellWidget(workingDir, tabName);
            shellWidget.setToolTipText(tabName);
        } else if(workingDir != null && !workingDir.isEmpty()){
            String commandLine = "cd" + Const.SPACE_STRING + workingDir + Const.ENTER_STRING;
            try {
                shellWidget.executeCommand(commandLine);
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
        return shellWidget;
    }

//    Intellij 자체 에러 Message : Ignore
//    private static boolean checkEdtAndReadAction(ProcessHandler processHandler) {
//        Application application = ApplicationManager.getApplication();
//        if (application != null && application.isInternal() && !application.isHeadlessEnvironment()) {
//            String message = null;
//            if (application.isDispatchThread()) {
//                message = "Synchronous execution on EDT: ";
//            } else if (application.isReadAccessAllowed()) {
//                message = "Synchronous execution under ReadAction: ";
//            }
//
//            @NotNull Set<@NotNull Object> REPORTED_EXECUTIONS = ContainerUtil.newConcurrentSet();
//            if (message != null && REPORTED_EXECUTIONS.add(ExceptionUtil.currentStackTrace())) {
//                CommonUtil.log(Log.LEVEL_DEBUG, message + processHandler + ", see com.intellij.execution.process.OSProcessHandler#checkEdtAndReadAction() Javadoc for resolutions");
//                return false;
//            }
//        }
//        return true;
//    }

}
