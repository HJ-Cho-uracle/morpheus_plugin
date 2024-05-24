package m.client.ide.morpheus.ui.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.utils.ProjectUtil;
import m.client.ide.morpheus.ui.dialog.ExportIOSDialog;
import m.client.ide.morpheus.ui.dialog.ExportResourcesDialog;
import org.jetbrains.annotations.NotNull;

public class ExportResourceAction extends AbstractMorpheusAction {
    /**
     * @param anActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = getEventProject(anActionEvent);

        if (ProjectUtil.hasIOSProject(project)) {
            ExportResourcesDialog dialog = new ExportResourcesDialog(project, getTemplateText());
            dialog.show();
        }
    }
}
