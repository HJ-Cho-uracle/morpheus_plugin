package m.client.ide.morpheus.ui.dialog.librarymanager;


import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionManagerListener;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.ToolWindow;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;
import m.client.ide.morpheus.ui.dialog.LibraryManagerDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LibraryManagerView {
    private ToolWindow toolWindow;
    private DialogWrapper dialog;

    private InfoView infoView;
    private LibraryView libraryView;
    private JPanel libManagerComponent;
    private Project project;

    public LibraryManagerView() {
    }

    public LibraryManagerView(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    public JComponent getComponent(@NotNull DialogWrapper dialog) {
        this.dialog = dialog;

        libraryView.setLibManagerView(this);
        infoView.setLibmanagerView(this);
        return libManagerComponent;
    }

    public JComponent getComponent(Project project, @NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.project = project;

        libraryView.setLibManagerView(this);
        infoView.setLibmanagerView(this);
        return libManagerComponent;
    }

    public @Nullable LibraryManagerDialog getLibManagerDialog() {
        return dialog instanceof LibraryManagerDialog ? (LibraryManagerDialog) dialog : null;
    }

    public void refreshLibraryInfo(Object libraryParam, boolean force) {
        if(libraryParam instanceof LibraryParam) {
            CommonUtil.log(Log.LEVEL_DEBUG, this.getClass().getName() + "] refreshLibraryInfo : " + libraryParam);
            infoView.setLibraryParam((LibraryParam) libraryParam, force);
        }
    }

    public Project getProject() {
        return project != null ? project : (dialog instanceof LibraryManagerDialog ?
                ((LibraryManagerDialog) dialog).getProject() :
                ProjectManager.getInstance().getDefaultProject());
    }

    public ActionToolbar createActionToolBar() {
        final ActionManager actionManager = ActionManager.getInstance();

        DefaultActionGroup actionGroup = new DefaultActionGroup("TOOLBAR_GROUP", false);

        ActionToolbar toolbar = new ActionToolbarImpl("TOOLBAR", actionGroup, true, false);
        toolbar.setTargetComponent(this.libManagerComponent);

        ActionManagerListener actionManagerListener = (ActionManagerListener) ApplicationManager.getApplication().getMessageBus().syncPublisher(ActionManagerListener.TOPIC);
        actionManagerListener.toolbarCreated("TOOLBAR", actionGroup, true, toolbar);
        return toolbar;
    }
}
