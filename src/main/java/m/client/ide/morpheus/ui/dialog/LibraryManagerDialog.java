package m.client.ide.morpheus.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;
import m.client.ide.morpheus.ui.dialog.librarymanager.LibraryManagerView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class LibraryManagerDialog extends DialogWrapper {
    private final Project project;
    private LibraryManagerView view;

    public LibraryManagerDialog(@Nullable Project project, boolean canBeParent, String title) {
        super(project, canBeParent);

        this.project = project;
        setTitle(title);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        view = new LibraryManagerView();

        JComponent component = view.getComponent(this);
        component.setPreferredSize(new Dimension(920, 580));
        return component;
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action helpAction = this.getHelpAction();
        return helpAction == this.myHelpAction && this.getHelpId() == null ?
                new Action[]{this.getOKAction()/*, this.getCancelAction()*/} :
                new Action[]{this.getOKAction()/*, this.getCancelAction()*/, helpAction};
    }

    public void refreshLibraryInfo(LibraryParam libraryParam) {
        refreshLibraryInfo(libraryParam, false);
    }

    public void refreshLibraryInfo(Object libraryParam, boolean force) {
        if(view != null) {
            view.refreshLibraryInfo(libraryParam, force);
        }
    }

    public Project getProject() {
        return project;
    }
}
