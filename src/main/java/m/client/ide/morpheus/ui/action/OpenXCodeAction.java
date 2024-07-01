package m.client.ide.morpheus.ui.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.FrameworkConstants;
import m.client.ide.morpheus.launch.action.PodInstallAction;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class OpenXCodeAction extends AbstractMorpheusAction {
    public OpenXCodeAction() {
        super(MessageBundle.message("action.ui.OpenXCodeAction.text"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        @Nullable Project project = anActionEvent.getProject();
        if (project == null) {
            CommonUtil.log(Log.LEVEL_ERROR, OpenXCodeAction.class, anActionEvent.getProject(), "Project is null");
            return;
        }

        @Nullable File iosProjectFolder = LaunchUtil.getIOSProjectFolder(project);
        if (iosProjectFolder == null || !iosProjectFolder.exists()) {
            CommonUtil.openInfoDialog(UIMessages.get(UIMessages.PodInstall), UIMessages.get(UIMessages.IOSResourceNotExist));
            return;
        }

        final String commands = "open " + iosProjectFolder.getAbsolutePath() + File.separator + project.getName() + FrameworkConstants.PROJECT_XCODE_WORKSPACE;
        PodInstallAction.podInstall(project, commands);
    }
}
