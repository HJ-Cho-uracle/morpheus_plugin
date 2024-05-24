package m.client.ide.morpheus.launch.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.config.CoreConfigurable;
import m.client.ide.morpheus.core.constants.Const;
import m.client.ide.morpheus.core.npm.NpmConstants;
import m.client.ide.morpheus.core.utils.*;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.TerminalView;

import java.io.File;
import java.io.IOException;

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

        podInstall(project);
    }

    public static boolean podInstall(@NotNull Project project) {
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

        TerminalView terminalView = TerminalView.getInstance(project);
//            String commandLine = StringUtil.wrapDoubleQuatation(NpmUtils.findNpm()) + Const.SPACE_STRING + NpmConstants.INSTALL_COMMAND + ENTER_STRING;
        String commandLine = StringUtil.wrapDoubleQuatation("pod") + Const.SPACE_STRING + NpmConstants.INSTALL_COMMAND + ENTER_STRING;
        try {
            terminalView.createLocalShellWidget(iosProjectFolder.getAbsolutePath(), "Name").executeCommand(commandLine);
        } catch (IOException err) {
            err.printStackTrace();
        }

        return true;
    }

    private static String checkCocoapodsVersion() {
        String[] commands = {"pod", "--version"};

        return ExecCommandUtil.execProcessHandler(UIMessages.get(UIMessages.getCocoapodsVersion), null, commands);
    }
}
