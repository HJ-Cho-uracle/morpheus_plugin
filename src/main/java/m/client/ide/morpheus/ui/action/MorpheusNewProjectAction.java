package m.client.ide.morpheus.ui.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.ide.impl.NewProjectUtil;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.ProjectViewImpl;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.ide.projectWizard.NewProjectWizard;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.impl.welcomeScreen.NewWelcomeScreen;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.OffsetIcon;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import icons.CoreIcons;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.cli.MorpheusCLIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;

public class MorpheusNewProjectAction extends AnAction implements DumbAware {

    public MorpheusNewProjectAction() {
        super();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (NewWelcomeScreen.isNewWelcomeScreen(e)) {
            e.getPresentation().setIcon(getMorpheusDecoratedIcon());
            e.getPresentation().setText(MessageBundle.message("welcome.morpheus.new.project.compact"));
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (!MorpheusCLIUtil.checkMorpheusCLI(null)) {
            return;
        }

        NewProjectWizard wizard = new NewProjectWizard(null, ModulesProvider.EMPTY_MODULES_PROVIDER, null);
        NewProjectUtil.createNewProject(wizard);

        @Nullable Project project = ProjectUtil.findAndFocusExistingProjectForPath(Path.of(wizard.getNewProjectFilePath()));
        if (project != null) {
            addViewContentManagerListener(project);
        }
    }

    private void addViewContentManagerListener(Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CommonUtil.log(Log.LEVEL_DEBUG, "MorpheusNewProjectAction.addViewContentManagerListener() => application => invokeLater");
            DumbService.getInstance(project).runWhenSmart(() -> {
                addListener(project);
            });
        });
    }

    private void addListener(Project project) {
        CommonUtil.log(Log.LEVEL_DEBUG, "MorpheusNewProjectAction.addViewContentManagerListener() => runWhenSmart");
        ProjectView view = ProjectView.getInstance(project);
        if (view instanceof ProjectViewImpl) {
            ProjectViewImpl viewImpl = (ProjectViewImpl) view;
            viewImpl.getContentManager().addContentManagerListener(new ContentManagerListener() {
                public void selectionChanged(@NotNull ContentManagerEvent event) {
                    CommonUtil.log(Log.LEVEL_DEBUG, "MorpheusNewProjectAction.addListener() selection changed => " + event);
                    if (event.getOperation() == ContentManagerEvent.ContentOperation.add) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            DumbService.getInstance(project).runWhenSmart(() -> {
                                CommonUtil.log(Log.LEVEL_DEBUG, "MorpheusNewProjectAction.addListener() change view : " + viewImpl.getCurrentViewId() +
                                        " => " + ProjectViewPane.ID);
                                viewImpl.changeView(ProjectViewPane.ID);
                            });
                        });
                    }
                }
            });
        }
    }

    public void showProjectInProjectWindow(@NotNull Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            DumbService.getInstance(project).runWhenSmart(() -> {
                ApplicationManager.getApplication().invokeLater(() -> {
                    ProjectView view = ProjectView.getInstance(project);
                    if (view instanceof ProjectViewImpl) {
                        ProjectViewImpl viewImpl = (ProjectViewImpl) view;
                        view.changeView(ProjectViewPane.ID);
                        view.getCurrentProjectViewPane();
                        Content @NotNull [] contents = viewImpl.getContentManager().getContents();
                        Key<String> SUB_ID_KEY = Key.create("pane-sub-id");
                        for (int i = 0; i < contents.length; i++) {
                            Content content = contents[i];
                            content.putUserData(SUB_ID_KEY, ProjectViewPane.ID);
                        }
                    }
                });
            });
        });
    }

    @NotNull
    Icon getMorpheusDecoratedIcon() {
        Icon icon = CoreIcons.icon16;
        Icon badgeIcon = new OffsetIcon(0, CoreIcons.icon16).scale(0.666f);

        LayeredIcon decorated = new LayeredIcon(2);
        decorated.setIcon(badgeIcon, 0, 7, 7);
        decorated.setIcon(icon, 1, 0, 0);
        return decorated;
    }
}
