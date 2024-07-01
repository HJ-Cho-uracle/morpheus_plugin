package m.client.ide.morpheus.launch.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.config.CoreConfigurable;
import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.npm.NpmUtils;
import m.client.ide.morpheus.core.resource.LibraryType;
import m.client.ide.morpheus.core.utils.*;
import m.client.ide.morpheus.framework.cli.CLILibraryManager;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class PodInstallAction extends AnAction {
    private static final String ENTER_STRING = OSUtil.isWindows() ? "\r\n" : Const.ENTER_STRING;

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        boolean isMorpheus = MorpheusConfigManager.isMorpheusProject(getEventProject(e));
        e.getPresentation().setEnabled(isMorpheus);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        @Nullable Project project = anActionEvent.getProject();
        if (project == null) {
            CommonUtil.log(Log.LEVEL_ERROR, PodInstallAction.class, null, "Project is null");
            return;
        }

        podInstall(project, null);
    }

    public static boolean podInstall(@NotNull Project project, String afterCommand) {
        @Nullable File iosProjectFolder = LaunchUtil.getIOSProjectFolder(project);
        if (iosProjectFolder == null || !iosProjectFolder.exists()) {
            CommonUtil.openInfoDialog(UIMessages.get(UIMessages.PodInstall), UIMessages.get(UIMessages.IOSResourceNotExist));
            return false;
        }

        String version = checkCocoapodsVersion();
        if (version == null || version.isEmpty()) {
            CommonUtil.openInfoDialog(UIMessages.get(UIMessages.PodInstall), UIMessages.get(UIMessages.CocoapodsNotExist));
            PreferenceUtil.openPreference(project, CoreConfigurable.class);
            return false;
        }

        StringBuilder message =
                new StringBuilder(UIMessages.get(UIMessages.PodInstall) + " Selected!");
        // If an element is selected in the editor, add info about it.
        message.append("\nSelected Element: ").append(project.getName());

//            String commandLine = StringUtil.wrapDoubleQuatation(NpmUtils.findNpm()) + Const.SPACE_STRING + NpmConstants.INSTALL_COMMAND + ENTER_STRING;
        String commandLine = StringUtil.wrapDoubleQuatation("pod") + Const.SPACE_STRING + NpmConstants.INSTALL_COMMAND + ENTER_STRING;
        try {
            ShellTerminalWidget shellWidget = ExecCommandUtil.getShellWidget(project, project.getName(), iosProjectFolder.getAbsolutePath());

            npmInstallIfNeeded(project, iosProjectFolder, shellWidget);

            shellWidget.executeCommand(commandLine);
            if (afterCommand != null) {
                shellWidget.executeCommand(afterCommand);
            }
        } catch (IOException err) {
            CommonUtil.log(Log.LEVEL_ERROR, err.getMessage(), err);
        }

        return true;
    }

    private static void npmInstallIfNeeded(Project project, File iosProjectFolder, ShellTerminalWidget shellWidget) throws IOException {
        File podFile = FileUtil.getChildFile(iosProjectFolder, "Podfile");
        if (podFile == null || !podFile.exists()) {
            CommonUtil.openErrorDialog(UIMessages.get(UIMessages.podfileNotExist));
            return;
        }

        if (!checkMSDKLibrary(project, podFile)) {
            if (NpmUtils.hasNpmFile()) {
                String commandLine = "cd" + Const.SPACE_STRING + project.getBasePath();
                shellWidget.executeCommand(commandLine);
                commandLine = StringUtil.wrapDoubleQuatation("npm") + Const.SPACE_STRING + NpmConstants.INSTALL_COMMAND + ENTER_STRING;
                shellWidget.executeCommand(commandLine);
                commandLine = "cd" + Const.SPACE_STRING + iosProjectFolder.getAbsolutePath();
                shellWidget.executeCommand(commandLine);
            }
        }
    }

    private static boolean checkMSDKLibrary(Project project, @NotNull File podFile) {
        Map<String, Map<String, LibraryParam>> libraries = CLILibraryManager.getInstance(project).getLibraries();
        try {
            String str = FileUtils.readFileToString(podFile, StandardCharsets.UTF_8);
//            Set<String> groupKeys = libraries.keySet();
//            for(String groupKey : groupKeys) {
            String groupKey = LibraryType.ADDON.toString();
            Map<String, LibraryParam> groupLibraries = libraries.get(groupKey);
            if (groupLibraries != null) {
                Set<String> libraryKeys = groupLibraries.keySet();
                for (String libraryKey : libraryKeys) {
                    LibraryParam library = groupLibraries.get(libraryKey);
                    if (str.indexOf(library.getCliId()) < 0) {
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
            return false;
        }
        return true;
    }

    private static @NotNull String checkCocoapodsVersion() {
        String[] commands = {"pod", "--version"};

        return ExecCommandUtil.execProcessHandler(UIMessages.get(UIMessages.getCocoapodsVersion), null, commands);
    }
}
