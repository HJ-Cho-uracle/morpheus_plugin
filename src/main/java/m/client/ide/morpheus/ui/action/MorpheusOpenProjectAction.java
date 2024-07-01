package m.client.ide.morpheus.ui.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.OpenFileAction;
import com.intellij.ide.actions.OpenProjectFileChooserDescriptor;
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.ide.impl.OpenProjectTask;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.ProjectViewImpl;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.fileChooser.impl.FileChooserUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectOpenProcessor;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.MorpheusInitializer;
import m.client.ide.morpheus.core.npm.NpmUtils;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.EclipseProjectNatureUtil;
import m.client.ide.morpheus.core.utils.ExecCommandUtil;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.framework.eclipse.MorpheusProjectConvertTask;
import m.client.ide.morpheus.framework.eclipse.library.Library;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MorpheusOpenProjectAction extends OpenFileAction {
    @Override
    protected void setShortcutSet(@NotNull ShortcutSet shortcutSet) {
    }

    /**
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (OSUtil.isMac() && !MorpheusInitializer.checkToolsData()) {
            return;
        }

        Project project = e.getProject();
        FileChooserDescriptor descriptor = new ProjectOnlyFileChooserDescriptor();
        VirtualFile toSelect = FileChooserUtil.getLastOpenedFile(project);

        descriptor.putUserData(PathChooserDialog.PREFER_LAST_OVER_EXPLICIT, false);
        FileChooser.chooseFile(descriptor, project, toSelect != null ? toSelect : this.getPathToSelect(), (file) -> {
            if (!descriptor.isFileSelectable(file)) {
                String message = MessageBundle.message("error.dir.not,morpheus.project", new Object[]{file.getPresentableUrl()});
                Messages.showInfoMessage(project, message, IdeBundle.message("title.cannot.open.project", new Object[0]));
                return;
            }

            @Nullable CompletableFuture<Project> futureProject = null;
            CommonUtil.log(Log.LEVEL_DEBUG, "Do Open File : " + file);
            futureProject = openExistingDir(file.toNioPath(), project);

            List<Library> libraryList = doConvertProject(file);

            if (futureProject != null) {
                VirtualFile finalFile = file;
                futureProject.thenAccept(openedProject -> {
                    CommonUtil.log(Log.LEVEL_DEBUG, "MorpheusOpenProjectAction CompletableFuture openedProject => " + openedProject);
                    FileChooserUtil.setLastOpenedFile(openedProject, Path.of(finalFile.getParent().getPath()));
                    if (openedProject == null) {
                        return;
                    }

                    SwingUtilities.invokeLater(() -> NpmUtils.applyDependencies(openedProject, libraryList));
                    MorpheusOpenProjectAction.this.addViewContentManagerListener(openedProject);
                });
            }
        });
    }

    public static @NotNull CompletableFuture<Project> openExistingDir(@NotNull Path file, @Nullable Project currentProject) {

        CompletableFuture projectFuture = ProjectUtil.openOrImportAsync(file, OpenProjectTask.build().withProjectToClose(currentProject));

        return projectFuture.thenApply((Object project) -> {
            if (!ApplicationManager.getApplication().isUnitTestMode() && project instanceof Project) {
                FileChooserUtil.setLastOpenedFile((Project) project, file);
            }

            return project;
        });
    }

    private void addViewContentManagerListener(Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            CommonUtil.log(Log.LEVEL_DEBUG, "MorpheusOpenProjectAction.addViewContentManagerListener() => application => invokeLater");
            DumbService.getInstance(project).runWhenSmart(() -> {
                addListener(project);
            });
        });
    }

    private void addListener(Project project) {
        CommonUtil.log(Log.LEVEL_DEBUG, "MorpheusOpenProjectAction.addVListener() => " + project);
        ProjectView view = ProjectView.getInstance(project);
        if (view instanceof ProjectViewImpl) {
            ProjectViewImpl viewImpl = (ProjectViewImpl) view;
            viewImpl.changeView(ProjectViewPane.ID);
            viewImpl.getContentManager().addContentManagerListener(new ContentManagerListener() {
                public void selectionChanged(@NotNull ContentManagerEvent event) {
                    CommonUtil.log(Log.LEVEL_DEBUG, "ContentManagerListener selection change event => " + event);
                    if (event.getOperation() == ContentManagerEvent.ContentOperation.add) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            DumbService.getInstance(project).runWhenSmart(() -> {
                                CommonUtil.log(Log.LEVEL_DEBUG, "ContentManagerListener content change view : " + viewImpl.getCurrentViewId() +
                                        " => " + ProjectViewPane.ID);
                                viewImpl.getContentManager().removeContentManagerListener(this);
                                viewImpl.changeView(ProjectViewPane.ID);
                            });
                        });
                    }
                }
            });
        }
    }

    protected List<Library> doConvertProject(@NotNull VirtualFile projectDir) {
        if (!projectDir.exists()) {
            CommonUtil.log(Log.LEVEL_ERROR, "Project not found : " + projectDir.getPath());
            return null;
        }

        MorpheusProjectConvertTask convertTask = new MorpheusProjectConvertTask(projectDir);
        List<Library> libraryList = convertTask.getLibraryDependencies();
        ExecCommandUtil.runProcessWithProgressSynchronously(convertTask, convertTask.getTitle(), false, null);

        return libraryList;
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
