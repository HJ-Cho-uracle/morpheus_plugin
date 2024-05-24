/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package m.client.ide.morpheus.launch.configuration;

import com.esotericsoftware.minlog.Log;
import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.framework.FrameworkConstants;
import m.client.ide.morpheus.launch.common.IOSSimUtil;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Launches a flutter app, showing it in the console.
 * <p>
 * Normally creates a debugging session, which is needed for hot reload.
 */
public class LaunchState extends CommandLineState {
    // We use the profile launch type, contributed by the Android IntelliJ plugins
    // in 2017.3 and Android Studio 3.0, if it's available. This allows us to support
    // their 'profile' launch button, next to the regular run and debug ones.
    public static final String ANDROID_PROFILER_EXECUTOR_ID = "Android Profiler";

    private final @NotNull VirtualFile workDir;

    /**
     * The file or directory holding the Flutter app's source code.
     * This determines how the analysis server resolves URI's (for breakpoints, etc).
     * <p>
     * If a file, this should be the file containing the main() method.
     */
    private final @NotNull VirtualFile sourceLocation;

    private final @NotNull RunConfig runConfig;

    public LaunchState(@NotNull ExecutionEnvironment env,
                       @NotNull VirtualFile workDir,
                       @NotNull VirtualFile sourceLocation,
                       @NotNull RunConfig runConfig) {
        super(env);
        this.workDir = workDir;
        this.sourceLocation = sourceLocation;
        this.runConfig = runConfig;

        install(this, env, workDir);
    }

    private void install(@NotNull CommandLineState launcher, @NotNull ExecutionEnvironment env, @NotNull VirtualFile workDir) {
        // Create our own console builder.
        //
        // We need to filter input to this console without affecting other consoles, so we cannot use a consoleFilterInputProvider.
        final GlobalSearchScope searchScope = GlobalSearchScopes.executionScope(env.getProject(), env.getRunProfile());
        final TextConsoleBuilder builder = new TextConsoleBuilderImpl(env.getProject(), searchScope) {
            @NotNull
            @Override
            protected ConsoleView createConsole() {
                return new ConsoleViewImpl(env.getProject(), true);
            }
        };

        launcher.setConsoleBuilder(builder);
    }

    protected RunContentDescriptor launch(@NotNull ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        Project project = env.getProject();
        final String executorId = env.getExecutor().getId();
        CommonUtil.log(Log.LEVEL_DEBUG, LaunchState.class.getName() + ".launch() ] Executor ID ========> " + executorId);

        try {
            if(runConfig instanceof IOSRunConfiguration) {
                IOSRunConfiguration iosRunConfig = (IOSRunConfiguration) runConfig;
                if(!iosRunConfig.isValidate(project)) {
                    return null;
                }

                File gradlePropFile = CommonUtil.getPathFile(IOSSimUtil.getBuildConfigLocation("run"), FrameworkConstants.GRADLE_PROPERTIES_FILE); //$NON-NLS-1$ //$NON-NLS-2$
                IOSSimUtil.setLaunchProperties(project, gradlePropFile, iosRunConfig, false);

                File buildFolder = CommonUtil.getPathFile(iosRunConfig.getRunConfigField().getCurrentDir(), "build");
                if(buildFolder.exists()) {
                    FileUtil.deleteDirectory(buildFolder);
                }
            }

            @NotNull GeneralCommandLine generalCommandLine = runConfig.getCommand(env);
            CommonUtil.log(Log.LEVEL_DEBUG, LaunchState.class.getName() + ".launch() ] " + generalCommandLine.getCommandLineString());

            OSProcessHandler processHandler = new OSProcessHandler(generalCommandLine);
            @NotNull ExecutionResult result = setUpConsoleAndActions(processHandler);
            processHandler.startNotify();

            final RunContentDescriptor descriptor = new RunContentBuilder(result, env).showRunContent(env.getContentToReuse());

            try {
                final Field f = descriptor.getClass().getDeclaredField("myDisplayName");
                f.setAccessible(true);
                f.set(descriptor, descriptor.getDisplayName() + " (" + project.getName() + ")");
            } catch (IllegalAccessException | NoSuchFieldException e) {
                LOG.info(e);
            }
            return descriptor;
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
        return null;
    }

    @NotNull
    protected ExecutionResult setUpConsoleAndActions(@NotNull ProcessHandler processHandler) throws ExecutionException {
        final ConsoleView console = createConsole(getEnvironment().getExecutor());
        if (console != null) {
            console.attachToProcess(processHandler);
        }

        // Add observatory actions.
        // These actions are effectively added only to the Run tool window.
        // For Debug see FlutterDebugProcess.registerAdditionalActions()
        final Computable<Boolean> observatoryAvailable = () -> !processHandler.isProcessTerminated();
        final List<AnAction> actions = new ArrayList<>(Arrays.asList(
                super.createActions(console, processHandler, getEnvironment().getExecutor())));

        return new DefaultExecutionResult(console, processHandler, actions.toArray(new AnAction[0]));
    }

    @Override
    public @NotNull
    ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        throw new ExecutionException("not implemented"); // Not used; launch() does this.
    }

    @Override
    protected @NotNull
    ProcessHandler startProcess() throws ExecutionException {
        // This can happen if there isn't a custom runner defined in plugin.xml.
        // The runner should extend LaunchState.Runner (below).
        throw new ExecutionException("need to implement LaunchState.Runner for " + runConfig.getClass());
    }

    /**
     * A run configuration that works with Launcher.
     */
    public interface RunConfig extends RunProfile {
        Project getProject();

        @Override
        @NotNull
        LaunchState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException;

        @NotNull
        GeneralCommandLine getCommand(ExecutionEnvironment environment) throws ExecutionException;
    }

    /**
     * A runner that automatically invokes {@link #launch}.
     */
    public static abstract class Runner<C extends RunConfig> extends GenericProgramRunner {
        private final Class<C> runConfigClass;

        public Runner(Class<C> runConfigClass) {
            this.runConfigClass = runConfigClass;
        }

        @SuppressWarnings("SimplifiableIfStatement")
        @Override
        public boolean canRun(final @NotNull String executorId, final @NotNull RunProfile profile) {
            if (!DefaultRunExecutor.EXECUTOR_ID.equals(executorId) &&
                    !DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) &&
                    !ANDROID_PROFILER_EXECUTOR_ID.equals(executorId)) {
                return false;
            }

            if (!(profile instanceof RunConfig)) {
                return false;
            }

            // If the app is running and the launch mode is the same, then we can run.
            final RunConfig config = (RunConfig) profile;
            final ProcessHandler process = getRunningAppProcess(config);
            if (process != null) {
                 return false;
            }

            return runConfigClass.isInstance(profile) && canRun(runConfigClass.cast(profile));
        }

        /**
         * Subclass hook for additional checks.
         */
        protected boolean canRun(C config) {
            return true;
        }

        @Override
        protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env)
                throws ExecutionException {
            if (!(state instanceof LaunchState)) {
                LOG.error("unexpected RunProfileState: " + state.getClass());
                return null;
            }

            final LaunchState launchState = (LaunchState) state;
            final String executorId = env.getExecutor().getId();

            // See if we should issue a hot-reload.
            final List<RunContentDescriptor> runningProcesses =
                    ExecutionManager.getInstance(env.getProject()).getContentManager().getAllDescriptors();

            final ProcessHandler process = getRunningAppProcess(launchState.runConfig);
            if (process != null) {
                 return null;
            }

            // Else, launch the app.
            return launchState.launch(env);
        }
    }

    /**
     * Returns the currently running app for the given RunConfig, if any.
     */
    @Nullable
    public static ProcessHandler getRunningAppProcess(RunConfig config) {
        final Project project = config.getProject();
        final List<RunContentDescriptor> runningProcesses =
                ExecutionManager.getInstance(project).getContentManager().getAllDescriptors();

        for (RunContentDescriptor descriptor : runningProcesses) {
            final ProcessHandler process = descriptor.getProcessHandler();
            if (process != null && !process.isProcessTerminated() && process.getUserData(IOS_RUN_CONFIG_KEY) == config) {
                return process;
            }
        }

        return null;
    }

    private static final Key<RunConfig> IOS_RUN_CONFIG_KEY = new Key<>("IOS_RUN_CONFIG_KEY");

    private static final Logger LOG = Logger.getInstance(LaunchState.class);
}
