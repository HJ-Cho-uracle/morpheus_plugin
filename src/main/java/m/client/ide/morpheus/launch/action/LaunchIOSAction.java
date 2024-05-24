package m.client.ide.morpheus.launch.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.ui.action.AbstractMorpheusAction;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class LaunchIOSAction extends AbstractMorpheusAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        @Nullable Project project = anActionEvent.getProject();
        if (project == null) {
            CommonUtil.log(Log.LEVEL_ERROR, LaunchIOSAction.class, anActionEvent.getProject(), "Project is null");
            return;
        }

        @Nullable File iosProjectFolder = LaunchUtil.getIOSProjectFolder(project);
        if (iosProjectFolder == null || !iosProjectFolder.exists()) {
            CommonUtil.openInfoDialog(UIMessages.get(UIMessages.OpenXCode), UIMessages.get(UIMessages.IOSResourceNotExist));
            return;
        }

    }
}
