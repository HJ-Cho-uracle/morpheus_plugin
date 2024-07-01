package m.client.ide.morpheus.ui.action;

import com.intellij.ide.JavaUiBundle;
import com.intellij.ide.actions.ImportProjectAction;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.ProjectViewImpl;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.ide.util.newProjectWizard.AddModuleWizard;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import com.intellij.util.containers.ContainerUtil;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.EclipseProjectNatureUtil;
import com.esotericsoftware.minlog.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ImportEclipseMorpheusAction extends ImportProjectAction {
    /**
     * @param anActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        List<Module> moduleList = doImport(null);
        Project project = null;
        for (Module module : moduleList) {
            project = module.getProject();
            if (project != null) {
                break;
            }
        }

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

    /**
     * @param isInNewSubmenu
     * @param isInJavaIde
     * @return
     */
    @Override
    public @NotNull String getActionText(boolean isInNewSubmenu, boolean isInJavaIde) {
        return MessageBundle.message("action.ImportMorpheusProject.text");
    }

    public static List<Module> doImport(@Nullable Project project) {
        AddModuleWizard wizard = selectFileAndCreateWizard(project, (Component) null);
        if (wizard != null && (wizard.getStepCount() <= 0 || wizard.showAndGet())) {
            List<Module> moduleList = createFromWizard(project, wizard);
            return moduleList;
        } else {
            return Collections.emptyList();
        }
    }

    public static @Nullable AddModuleWizard selectFileAndCreateWizard(@Nullable Project project, @Nullable Component dialogParent) {
        FileChooserDescriptor directoryDescriptor = new FileChooserDescriptor(false, true,
                false, false, false, false) {
            public boolean isFileSelectable(@Nullable VirtualFile file) {
                File dir = new File(file.getPath());
                return super.isFileSelectable(file) && EclipseProjectNatureUtil.isEclipseMorpheusProject(dir);
            }
        };

        directoryDescriptor.setTitle(JavaUiBundle.message("chooser.title.select.file.or.directory.to.import", new Object[0]));
        @Nullable VirtualFile selectedFile = FileChooser.chooseFile(directoryDescriptor, project, null);

        List<ProjectImportProvider> providers = getProviders(project);
        return selectFileAndCreateWizard(project, dialogParent, directoryDescriptor, (ProjectImportProvider[]) providers.toArray(new ProjectImportProvider[0]));
    }

    public static @NotNull List<ProjectImportProvider> getProviders(@Nullable Project project) {
        ProjectImportProvider[] providers = (ProjectImportProvider[]) ProjectImportProvider.PROJECT_IMPORT_PROVIDER.getExtensions();
        return ContainerUtil.filter(providers, (provider) -> {
            return project == null ? provider.canCreateNewProject() : provider.canImportModule();
        });
    }
}
