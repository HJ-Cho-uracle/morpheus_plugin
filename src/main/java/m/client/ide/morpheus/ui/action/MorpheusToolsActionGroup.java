package m.client.ide.morpheus.ui.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import m.client.ide.morpheus.core.config.CoreSettingsState;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MorpheusToolsActionGroup extends DefaultActionGroup {

    /**
     * @param e
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        e.getPresentation().setEnabled(isEnable(e));
        updateVisibility(e.getPresentation(), isVisible(e));
    }

    protected static void updateVisibility(final @NotNull Presentation presentation, boolean isMorpheus) {
        final JComponent component = (JComponent) presentation.getClientProperty(AbstractMorpheusAction.customComponentKey);
        if (component != null) {
            component.setVisible(isMorpheus);
            if (component.getParent() != null) {
                component.getParent().doLayout();
                component.getParent().repaint();
            }
        } else {
            presentation.setEnabled(isMorpheus);
        }
    }

    protected boolean isEnable(@NotNull AnActionEvent e) {
        @Nullable Project project = getEventProject(e);
        if (project == null) {
            return false;
        }

        boolean isMorpheus = MorpheusConfigManager.isMorpheusProject(project);

        return isMorpheus || CoreSettingsState.getInstance().isDevMode();
    }

    protected boolean isVisible(@NotNull AnActionEvent e) {
        @Nullable Project project = getEventProject(e);
        if (project == null) {
            return false;
        }

        boolean isMorpheus = MorpheusConfigManager.isMorpheusProject(project);

        return isMorpheus || CoreSettingsState.getInstance().isDevMode();
    }
}
