package m.client.ide.morpheus.core.npm;

import com.android.annotations.Nullable;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.terminal.JBTerminalWidget;
import m.client.ide.morpheus.core.config.CoreConfigurable;
import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.ExecCommandUtil;
import m.client.ide.morpheus.core.utils.PreferenceUtil;
import m.client.ide.morpheus.framework.messages.FrameworkMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class NodeInstaller {

    private static NodeInstaller nodenodeInstaller;

    public static NodeInstaller getInstance() {
        if (nodenodeInstaller == null) {
            nodenodeInstaller = new NodeInstaller();
        }
        return nodenodeInstaller;
    }

    private static OS os = OS.getOs();

    enum OS {
        WIN("win", "zip"),
        LINUX("linux", "tar.gz"),
        MAC("darwin", "tar.gz");

        private final String nodeOsName;
        private final String nodeArchiveExtension;

        OS(String nodeOsName, String nodeArchiveExtension) {
            this.nodeOsName = nodeOsName;
            this.nodeArchiveExtension = nodeArchiveExtension;
        }

        static OS getOs() {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("windows")) {
                return WIN;
            } else if (osName.contains("linux")) {
                return LINUX;
            }

            return MAC;
        }
    }

    public @NotNull void installNodeJS(Project project) {
        if (os == OS.WIN) {
            installNodeJSForWin(project);
        } else {
            if (project == null) {
                installNodeJSForMac();
                CommonUtil.openInfoDialog(FrameworkMessages.get(FrameworkMessages.installNpm), FrameworkMessages.get(FrameworkMessages.installNpmTerminal));
            } else {
                installNodeJSForMac(project);
                CommonUtil.openInfoDialog(FrameworkMessages.get(FrameworkMessages.installNpm), FrameworkMessages.get(FrameworkMessages.setNpmPath));
            }
            PreferenceUtil.openPreference(null, CoreConfigurable.class);
        }
    }

    private @NotNull void installNodeJSForMac() {
        // installs NVM (Node Version Manager)
        String[] installNvm = {"sh", "-c", "curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash"};
        GeneralCommandLine commandLine = createGeneralCommandLine(GeneralCommandLine.ParentEnvironmentType.CONSOLE, null, null, installNvm, null);
        StringBuilder result = new StringBuilder();
        ExecCommandUtil.execCommandLine(commandLine, result);
    }

    private @NotNull String execInstallNodeJSForMac() {
        // installs NVM (Node Version Manager)
        String[] installNvm = {"sh", "-c", "curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash"};
        GeneralCommandLine commandLine = createGeneralCommandLine(GeneralCommandLine.ParentEnvironmentType.CONSOLE, null, null, installNvm, null);
        StringBuilder result = new StringBuilder();
        if (!ExecCommandUtil.execCommandLine(commandLine, result)) {
            return "";
        }

        // download and install Node.js => Error exitcode 127 /bin/sh: nvm: command not found
//        String[] installNode = {"sh", "-c", "nvm install 20"};
//        commandLine = ExecCommandUtil.createGeneralCommandLine(null, null, installNode, null);
        String[] params = {"nvm", "install", "20"};
        commandLine = new GeneralCommandLine().withExePath("sh").withParameters(params);
        result = new StringBuilder();
        if (!ExecCommandUtil.execCommandLine(commandLine, result)) {
            return "";
        }

        // verifies the right NPM version is in the environment
        String[] whichNpm = {"which", "npm"}; //should print `10.5.2`
        commandLine = createGeneralCommandLine(GeneralCommandLine.ParentEnvironmentType.CONSOLE, null, null, whichNpm, null);
        result = new StringBuilder();
        if (!ExecCommandUtil.execCommandLine(commandLine, result)) {
            return "";
        }

        return result.toString();
    }

    private @NotNull void installNodeJSForMac(@NotNull Project project) {
        // installs NVM (Node Version Manager)
        ShellTerminalWidget terminal = getTerminalWidget(project, project.getBasePath());
        String installNvm = "curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash";
        try {
            terminal.executeCommand(installNvm);
        } catch (NullPointerException | IOException err) {
            return;
        }

        // download and install Node.js
        String installNode = "nvm install 20";
        try {
            terminal.executeCommand(installNode);
        } catch (IOException err) {
            return;
        }

        // verifies the right Node.js version is in the environment
        String nodeVersion = "node -v"; // should print `v20 .13.0`
        try {
            terminal.executeCommand(nodeVersion);
        } catch (IOException err) {
            return;
        }

        // verifies the right NPM version is in the environment
        String npmVersion = "npm -v"; //should print `10.5.2`
        try {
            terminal.executeCommand(npmVersion);
        } catch (IOException err) {
            return;
        }

        // verifies the right NPM version is in the environment
        String whichNpm = "which npm"; //should print `10.5.2`
        try {
            terminal.executeCommand(whichNpm);
        } catch (IOException err) {
            return;
        }
    }

    private void installNodeJSForWin(@NotNull Project project) {
        // download and install Node.js
        String install = "choco install nodejs-lts --version=\"20.13.0\"";
        GeneralCommandLine cmd = new GeneralCommandLine(install)
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);

        StringBuilder result = new StringBuilder();
        if (!ExecCommandUtil.execCommandLine(cmd, result)) {
            return;
        }

        // verifies the right Node.js version is in the environment
        String nodeVersion = "node -v"; // should print `v20 .13.0`
        cmd = new GeneralCommandLine(nodeVersion)
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);

        result = new StringBuilder();
        if (!ExecCommandUtil.execCommandLine(cmd, result)) {
            return;
        }


        // verifies the right NPM version is in the environment
        String npmVersion = "npm -v"; //should print `10.5.2`
        cmd = new GeneralCommandLine(npmVersion)
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);

        result = new StringBuilder();
        if (!ExecCommandUtil.execCommandLine(cmd, result)) {
            return;
        }

//        return result.toString().trim();
    }

    public static @NotNull GeneralCommandLine createGeneralCommandLine(GeneralCommandLine.ParentEnvironmentType type, Project project, File cwd, String[] commands, @Nullable Map<String, String> envs) {
        GeneralCommandLine generalCommandLine = new GeneralCommandLine(commands);
        generalCommandLine.withParentEnvironmentType(type);
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.withEnvironment(System.getenv());

        if (envs == null) {
            envs = new HashMap<String, String>();
        }
        if (PreferenceUtil.getShowCLIDebug()) {
            envs.put("DEBUG", "*");
        }
//        String addedNpmPath = NpmUtils.addNpmPath(generalCommandLine.getEnvironment().get(ENVIRONMENT_PATH));
//        envs.put(ENVIRONMENT_PATH, addedNpmPath);
        generalCommandLine.withEnvironment(envs);

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

    public static @Nullable ShellTerminalWidget getTerminalWidget(Project project, String basePath) {
        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }
        if (project == null) {
            return null;
        }
        TerminalView terminalView = project.getService(TerminalView.class);
        if (terminalView == null) {
            return null;
        }

        if (basePath == null || basePath.isEmpty()) {
            basePath = project.getBasePath();
        }

        Set<JBTerminalWidget> widgets = terminalView.getWidgets();
        for (JBTerminalWidget widget : widgets) {
            if (widget.getProject().equals(project)) {
                ShellTerminalWidget shellWidget = (ShellTerminalWidget) Objects.requireNonNull(widget);
                String commandLine = "cd" + Const.SPACE_STRING + basePath + Const.EMPTY_STRING;
                try {
                    shellWidget.executeCommand(commandLine);
                } catch (IOException err) {
                    err.printStackTrace();
                }
                return shellWidget;
            }
        }

        return terminalView.createLocalShellWidget(basePath, project.getName());
    }

}
