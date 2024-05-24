package m.client.ide.morpheus.ui.action;

import com.intellij.ide.GeneralSettings;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.OpenFileAction;
import com.intellij.ide.actions.OpenProjectFileChooserDescriptor;
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.ide.impl.OpenProjectTask;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.lightEdit.LightEditUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.fileChooser.impl.FileChooserUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.ex.FileTypeChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.PlatformProjectOpenProcessor;
import com.intellij.projectImport.ProjectOpenProcessor;
import com.intellij.util.concurrency.annotations.RequiresEdt;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.EclipseProjectNatureUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;

public class MorpheusOpenProjectAction extends OpenFileAction {
    /**
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        FileChooserDescriptor descriptor = new ProjectOnlyFileChooserDescriptor();
        VirtualFile toSelect = null;
        if (StringUtil.isNotEmpty(GeneralSettings.getInstance().getDefaultProjectDirectory())) {
            toSelect = VfsUtil.findFileByIoFile(new File(GeneralSettings.getInstance().getDefaultProjectDirectory()), true);
        }

        descriptor.putUserData(PathChooserDialog.PREFER_LAST_OVER_EXPLICIT, false);
        FileChooser.chooseFiles(descriptor, project, toSelect != null ? toSelect : this.getPathToSelect(), (files) -> {
            Iterator iterator = files.iterator();

            VirtualFile file = null;
            while(iterator.hasNext()) {
                file = (VirtualFile)iterator.next();
                if (!descriptor.isFileSelectable(file)) {
                    String message = MessageBundle.message("error.dir.not,morpheus.project", new Object[]{file.getPresentableUrl()});
                    Messages.showInfoMessage(project, message, IdeBundle.message("title.cannot.open.project", new Object[0]));
                    return;
                }
            }

            iterator = files.iterator();

            while(iterator.hasNext()) {
                file = (VirtualFile)iterator.next();
                doOpenFile(project, file);
            }

            if(file != null && file.isDirectory()) {
                doConvertProject(file);
            }
        });
    }

    protected void doConvertProject(VirtualFile projectDir) {
    }

    @RequiresEdt
    private static void doOpenFile(@Nullable Project project, @NotNull VirtualFile file) {
        Path filePath = file.toNioPath();
        if (Files.isDirectory(filePath, new LinkOption[0])) {
            openExistingDir(filePath, project);
        } else {
            if ((project == null || !file.equals(project.getProjectFile())) && OpenProjectFileChooserDescriptor.isProjectFile(file)) {
                int answer = shouldOpenNewProject(project, file);
                if (answer == 2) {
                    return;
                }

                if (answer == 0) {
                    Project openedProject = ProjectUtil.openOrImport(filePath, OpenProjectTask.build().withProjectToClose(project));
                    if (openedProject != null) {
                        FileChooserUtil.setLastOpenedFile(openedProject, filePath);
                    }

                    return;
                }
            }

            LightEditUtil.markUnknownFileTypeAsPlainTextIfNeeded(project, file);
            FileType type = FileTypeChooser.getKnownFileTypeOrAssociate(file, project);
            if (type != null) {
                if (project != null && !project.isDefault()) {
                    openFile(file, project);
                } else {
                    PlatformProjectOpenProcessor.createTempProjectAndOpenFile(filePath, OpenProjectTask.build().withProjectToClose(project));
                }

            }
        }
    }

    @Messages.YesNoCancelResult
    private static int shouldOpenNewProject(@Nullable Project project, @NotNull VirtualFile file) {
        if (file.getFileType() instanceof ProjectFileType) {
            return 0;
        } else {
            ProjectOpenProcessor provider = ProjectOpenProcessor.getImportProvider(file);
            return provider == null ? 2 : provider.askConfirmationForOpeningProject(file, project);
        }
    }

    /**
     * @param e
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    private static class ProjectOnlyFileChooserDescriptor extends OpenProjectFileChooserDescriptor {
        ProjectOnlyFileChooserDescriptor() {
            super(false);
            this.setTitle(IdeBundle.message("title.open.project", new Object[0]));
        }

        @Override
        public boolean isFileSelectable(@Nullable VirtualFile file) {
            File dir = new File(file.getPath());
            return super.isFileSelectable(file) && EclipseProjectNatureUtil.isEclipseMorpheusProject(dir);
        }
    }
}
