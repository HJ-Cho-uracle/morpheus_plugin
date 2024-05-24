package m.client.ide.morpheus.ui.dialog;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.FileUtil;
import m.client.ide.morpheus.core.utils.PreferenceUtil;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.ui.action.ArchiveFileExportOperation;
import m.client.ide.morpheus.ui.dialog.components.ExportResourcesView;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ExportResourcesDialog extends DialogWrapper {
    private final Project project;
    private boolean isOKButtonOnly = false;

    private String selectedAppID;
    private ExportResourcesView view;

    public ExportResourcesDialog(@Nullable Project project, String title) {
        this(project, false, title);
    }

    public ExportResourcesDialog(@Nullable Project project, boolean canBeParent, String title) {
        super(project, canBeParent);

        this.project = project;
        this.isOKButtonOnly = project == null;
        setTitle(title);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        view = new ExportResourcesView(this.project);

        JComponent component = view.getComponent(this.getProject());
        component.setPreferredSize(new Dimension(640, 460));
        return component;
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action helpAction = this.getHelpAction();

        if (isOKButtonOnly) {
            return helpAction == this.myHelpAction && this.getHelpId() == null ?
                    new Action[]{this.getOKAction()} : new Action[]{this.getOKAction(), helpAction};
        } else {
//            setOKButtonText(UIMessages.get(UIMessages.LibView_apply));
            return helpAction == this.myHelpAction && this.getHelpId() == null ?
                    new Action[]{this.getOKAction(), this.getCancelAction()/*, this.getCancelAction()*/} :
                    new Action[]{this.getOKAction(), this.getCancelAction()/*, this.getCancelAction()*/, helpAction};
        }
    }

    public Project getProject() {
        return project;
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();

        try {
            exportResources();
        } catch (IOException e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getMessage(), e);
        }
    }

    private String exportResources() throws IOException {
        List resources = view.getWhiteCheckedResources();
        String destination = view.getDestinationValue();

        ArchiveFileExportOperation operation = new ArchiveFileExportOperation(project, LaunchUtil.getResFolder(project), resources, destination);
        return executeExportOperation(operation);
    }

    /**
     * 오퍼레이션 실행
     *
     * @param op
     * @return
     */
    protected String executeExportOperation(ArchiveFileExportOperation op) {
        final StringBuilder output = new StringBuilder();
        Project finalProject = project;
        Task.Modal modalTask = new Task.Modal(finalProject, UIMessages.get(UIMessages.ExportResourceTask), false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    op.run(progressIndicator);
                } catch (InterruptedException | InvocationTargetException e) {
                    CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
                }
            }
        };
//        modalTask.queue();
        ProgressManager.getInstance().run(modalTask);

        return output.toString();
    }

    protected List addProjectFile(Project project, List resources) {
        File folder = FileUtil.getChildFile(project, "ios");

        ArrayList<File> result = new ArrayList<>();
        result.addAll(resources);
        if (folder.exists()) {
            result.add(folder);
        }
        return result;
    }

}
