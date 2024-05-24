package m.client.ide.morpheus.ui.action;

import com.intellij.ide.JavaUiBundle;
import com.intellij.ide.actions.ImportProjectAction;
import com.intellij.ide.util.newProjectWizard.AddModuleWizard;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;
import com.intellij.util.containers.ContainerUtil;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.EclipseProjectNatureUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class ImportEclipseMorpheusAction extends ImportProjectAction {
    /**
     * @param anActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        doImport(null);
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
        return wizard != null && (wizard.getStepCount() <= 0 || wizard.showAndGet()) ? createFromWizard(project, wizard) : Collections.emptyList();
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
