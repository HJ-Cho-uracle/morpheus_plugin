package m.client.ide.morpheus.core.npm.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import m.client.ide.morpheus.core.npm.NpmUtils;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.cli.MorpheusCLIUtil;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NpmInstallAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        boolean isMorpheus = MorpheusConfigManager.isMorpheusProject(getEventProject(e));
        isMorpheus &= NpmUtils.hasNpmFile();

        e.getPresentation().setEnabled(isMorpheus);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        @Nullable Project project = anActionEvent.getProject();
        if (project == null) {
            CommonUtil.log(Log.LEVEL_ERROR, NpmInstallAction.class, null, "Project is null");
            return;
        }

        StringBuilder message =
                new StringBuilder(anActionEvent.getPresentation().getText() + " Selected!");
        // If an element is selected in the editor, add info about it.
        Navigatable selectedElement = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        if (selectedElement != null) {
            message.append("\nSelected Element: ").append(selectedElement);
        }

        NpmUtils.npmInstall(project, project.getBasePath());
    }
}
