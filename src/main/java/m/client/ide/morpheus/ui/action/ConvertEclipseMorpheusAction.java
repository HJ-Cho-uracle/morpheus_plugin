package m.client.ide.morpheus.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.utils.EclipseProjectNatureUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ConvertEclipseMorpheusAction extends AnAction {
    /**
     * @param anActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

    }

    /**
     * @param e
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        e.getPresentation().setEnabled(isEnable(e));
    }

    protected boolean isEnable(@NotNull AnActionEvent e) {
        @Nullable Project project = getEventProject(e);
        if (project == null) {
            return false;
        }

        boolean isOldMorpheus = EclipseProjectNatureUtil.isEclipseMorpheusProject(new File(project.getBasePath()));

        return isOldMorpheus;
    }

    protected boolean isVisible(@NotNull AnActionEvent e) {
        @Nullable Project project = getEventProject(e);
        return project != null;
    }

}
