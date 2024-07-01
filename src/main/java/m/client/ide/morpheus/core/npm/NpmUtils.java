package m.client.ide.morpheus.core.npm;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import m.client.ide.morpheus.core.config.CoreConfigurable;
import m.client.ide.morpheus.core.config.CoreSettingsState;
import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.resource.LibraryType;
import m.client.ide.morpheus.core.utils.*;
import m.client.ide.morpheus.framework.eclipse.library.Library;
import m.client.ide.morpheus.framework.messages.FrameworkMessages;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.*;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NpmUtils {
    private static final String ENTER_STRING = OSUtil.isWindows() ? "\r\n" : Const.ENTER_STRING;

    public static String addNpmPath(String path) {
        String npmPath = findNpm();
        String npm = getNpmFileName();
        if (npmPath.endsWith(npm)) {
            npmPath = npmPath.substring(0, npmPath.length() - npm.length());
        }
        if (npmPath.endsWith("/")) {
            npmPath = npmPath.substring(0, npmPath.length() - "/".length());
        }
        if (path == null || path.isEmpty()) {
            return npmPath;
        }
        if (npmPath != null && !npmPath.isEmpty() && !path.contains(npmPath)) {
            path = path + (path.endsWith(":") ? "" : ":") + npmPath;
        }

        return path;
    }

    public static boolean npmInstall(Project project, String basePath) {
        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }

        if (NpmUtils.hasNpmFile()) {
            String commandLine = StringUtil.wrapDoubleQuatation(NpmUtils.getNpmFileName()) + Const.SPACE_STRING + NpmConstants.INSTALL_COMMAND + ENTER_STRING;
            try {
                @NotNull ShellTerminalWidget shellWidget = ExecCommandUtil.getShellWidget(project, project.getName(), basePath);
                shellWidget.executeCommand(commandLine);
            } catch (IOException err) {
                err.printStackTrace();
                return false;
            }
        } else {
            PreferenceUtil.openPreference(project, CoreConfigurable.class);
            return false;
        }

        return true;
    }

    public static boolean applyDependencies(Project project, List<Library> libraryList) {
        String npm = NpmUtils.findNpm();
        if (npm.isEmpty()) {
            String log = "[installMorpheusCLI error : Node is not exist or too row version.";
            CommonUtil.log(Log.LEVEL_ERROR, log);
            return false;
        }

        String[] commands = new String[libraryList.size() + 3];
        commands[0] = npm;
        commands[1] = "i";
        commands[2] = "@morpheus/cli";
        StringBuilder libraries = new StringBuilder();
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            String cliId = library.getCliId();
            if (library.getLibraryType().equals(LibraryType.ADDON) && cliId.indexOf("locale") >= 0) {
                continue;
            }
            commands[i + 3] = cliId;
        }

        ExecCommandUtil.execProcessHandler(FrameworkMessages.get(FrameworkMessages.applyDependencies), project, new File(project.getBasePath()), commands, null);

        return true;
    }

    public static boolean applyDependenciesWithTerminal(Project project, ArrayList<Library> libraryList) {
        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }

        if (NpmUtils.hasNpmFile()) {
            StringBuilder commandLine = new StringBuilder(StringUtil.wrapDoubleQuatation(NpmUtils.getNpmFileName()));
            commandLine.append(Const.SPACE_STRING).append("i");
            commandLine.append(Const.SPACE_STRING).append("@morpheus/cli");

            for (Library library : libraryList) {
                commandLine.append(Const.SPACE_STRING).append(library.getCliId());
            }

            try {
                @NotNull ShellTerminalWidget shellWidget = ExecCommandUtil.getShellWidget(project, project.getName(), project.getBasePath());
                shellWidget.executeCommand(commandLine.toString());
            } catch (IOException err) {
                err.printStackTrace();
                return false;
            }
        } else {
            PreferenceUtil.openPreference(project, CoreConfigurable.class);
            return false;
        }

        return true;
    }

    public enum NpmCommand {
        BUILD("운영 빌드", 1), START("개발 빌드\n(watch)", 20),
        CLEAN("clean", 50), LINT("lint", 100), LINTFIX("lintfix", 500),
        ETC(Const.EMPTY_STRING, 1000);

        private final String commandName;
        private final int priority;

        NpmCommand(String commandName, int priority) {
            this.commandName = commandName;
            this.priority = priority;
        }

        public String getCommandName() {
            return commandName;
        }
    }

    public static final Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            int into1 = 1000;
            int into2 = 1000;

            for (NpmCommand commmand : NpmCommand.values()) {
                if (commmand.name().equalsIgnoreCase(o1)) {
                    into1 = commmand.priority;
                }

                if (commmand.name().equalsIgnoreCase(o2)) {
                    into2 = commmand.priority;
                }
            }

            return Integer.compare(into1, into2);
        }
    };

    public static boolean hasNodeModulesFolder(@NotNull Project project) {
        @Nullable @SystemIndependent @NonNls String basePath = project.getBasePath();

        return basePath != null && FileUtils.fileExists(basePath + File.separator + NpmConstants.NODE_MODULES_FOLDER);
    }

    public static boolean hasNpmFile() {
        try {
            String findNpm = findNpm();
            if (new File(findNpm).isFile()) {
                return true;
            }
        } catch (IllegalStateException e) {
            try {
                String nodePath = CoreSettingsState.getInstance().getNpmPath();
                String findNpm = findNewNpm(nodePath);
                if (new File(findNpm).isFile()) {
                    return true;
                }
            } catch (IllegalStateException e1) {
                return false;
            }
        }
        return false;
    }

    public static @NotNull String getNpmPathWithCheck() {
        return getNpmPathWithCheck(ProjectManager.getInstance().getDefaultProject());
    }

    public static @NotNull String getNpmPathWithCheck(Project project) {
        String npm = findNpm();
        if (npm.isEmpty()) {
//            int ret = CommonUtil.openQuestion(NpmUtils.class.getSimpleName(),
//                    FrameworkMessages.get(FrameworkMessages.installNpmQuestion));
//            if (ret == JOptionPane.YES_OPTION) {
//                NodeInstaller.getInstance().installNodeJS(project);
//            }
            CommonUtil.openInfoDialog(FrameworkMessages.get(FrameworkMessages.installNpm), FrameworkMessages.get(FrameworkMessages.installNodeAndSet));

            final String[] commands = {"open", "https://nodejs.org/en/download"};
            ExecCommandUtil.executeCommandWithLog(UIMessages.get(UIMessages.OpenXCode), commands);

            PreferenceUtil.openPreference(null, CoreConfigurable.class);
        }
        return npm;
    }

    public static @NotNull String findNpm() {
        String npmPath = CoreSettingsState.getInstance().getNpmPath();
        String npm = getNpmFileName();
        if (npmPath.endsWith(npm)) {
            File npmFile = new File(npmPath);
            if (npmFile.exists()) {
                return npmFile.getAbsolutePath();
            }
        }

        npmPath = ExecCommandUtil.findPathWithWhich(npm).trim();
        if (!npmPath.isEmpty()) {
            File npmFile = new File(npmPath);
            if (npmFile.exists()) {
                npmPath = npmFile.getAbsolutePath();
                CoreSettingsState.getInstance().setNpmPath(npmPath);
                return npmPath;
            }
        }

        String path = System.getenv(ExecCommandUtil.ENVIRONMENT_PATH);
        String[] paths = path.split("" + File.pathSeparatorChar, 0);
        List<String> directories = new ArrayList<String>();
        for (String p : paths) {
            directories.add(p);
        }

        // ensure /usr/local/bin is included for OS X
        if (OSUtil.isMac()) {
            directories.add("/usr/local/bin");
        }

        String npmFileName = getNewNpmFileName();
        // search for Node.js in the PATH directories
        for (String directory : directories) {
            File npmFile = new File(directory, npmFileName);

            if (npmFile.exists()) {
                npmPath = npmFile.getAbsolutePath();
                CoreSettingsState.getInstance().setNpmPath(npmPath);
                return npmPath;
            }
        }
        return "";
    }

    @Contract(pure = true)
    private static @NotNull String getNpmFileName() {
        if (OSUtil.isWindows()) {
            return "lnpm.cmd";
        }

        return "npm";
    }

    private static @NotNull String findNewNpm(@NotNull String nodePath) {
        // TODO Auto-generated method stub
        String npmFileName = getNewNpmFileName();
        String npmPath = String.valueOf(nodePath.substring(0, nodePath.lastIndexOf("bin"))) + npmFileName;
        File npmFile = new File(npmPath);
        if (npmFile.exists()) {
            return npmFile.getAbsolutePath();
        }

        String path = System.getenv("PATH");
        String[] paths = path.split(File.pathSeparator, 0);
        List<String> directories = new ArrayList<String>();
        byte b;
        int i;
        String[] arrayOfString1;
        for (i = (arrayOfString1 = paths).length, b = 0; b < i; ) {
            String p = arrayOfString1[b];
            directories.add(p);

            b++;
        }

        if (OSUtil.isMac()) {
            directories.add("/usr/local/bin");
        }

        for (String directory : directories) {
            npmFile = new File(directory, npmFileName);

            if (npmFile.exists()) {
                return npmFile.getAbsolutePath();
            }
        }
        return "";
    }

    @Contract(pure = true)
    private static @NotNull String getNewNpmFileName() {
        if (OSUtil.isWindows()) {
            return "lib/node_modules/npm/bin/npm.cmd";
        }

        return "lib/node_modules/npm/bin/npm";
    }
}
