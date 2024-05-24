package m.client.ide.morpheus.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import m.client.ide.morpheus.ui.dialog.components.ExportIOSView;
import m.client.ide.morpheus.ui.dialog.components.FilterOptionView;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class FilterOptionDialog extends DialogWrapper {
    private final Project project;
    private boolean isOKButtonOnly = false;

    private String selectedAppID;
    private FilterOptionView view;

    public FilterOptionDialog(@Nullable Project project, String title) {
        this(project, false, title);
    }
    public FilterOptionDialog(@Nullable Project project, boolean canBeParent, String title) {
        super(project, canBeParent);

        this.project = project;
        this.isOKButtonOnly = project == null;
        setTitle(title);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        view = new FilterOptionView();

        JComponent component = view.getComponent(this.getProject());
        component.setPreferredSize(new Dimension(640, 460));
        return component;
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action helpAction = this.getHelpAction();

        if(isOKButtonOnly) {
            return helpAction == this.myHelpAction && this.getHelpId() == null ?
                    new Action[]{this.getOKAction()} : new Action[]{this.getOKAction(), helpAction};
        } else {
            setOKButtonText(UIMessages.get(UIMessages.LibView_apply));
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
    }
}
