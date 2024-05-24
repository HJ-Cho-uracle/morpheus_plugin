package m.client.ide.morpheus.ui.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.core.utils.ProjectUtil;
import m.client.ide.morpheus.launch.action.DeviceSelectorAction;
import m.client.ide.morpheus.ui.dialog.ExportIOSDialog;
import org.jetbrains.annotations.NotNull;

public class ExportAppleAction extends AbstractMorpheusAction {
    /**
     * @param e
     * @return
     */
    @Override
    protected boolean isVisible(@NotNull AnActionEvent e) {
        boolean visible = super.isVisible(e) && OSUtil.isMac();

        return visible && DeviceSelectorAction.getSelectedIOSConfigField(e.getProject()) != null;
    }

    /**
     * @param anActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = getEventProject(anActionEvent);

        if (ProjectUtil.hasIOSProject(project)) {
            ExportIOSDialog dialog = new ExportIOSDialog(project, getTemplateText());
            dialog.show();
        }
    }
}
