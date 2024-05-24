package m.client.ide.morpheus.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import m.client.ide.morpheus.ui.dialog.components.AndroidOZViewerSettingView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AndroidOZViewerSettingDialog extends DialogWrapper {
    private final Project project;

    public static boolean openDialog(@NotNull Project project, boolean canBeParent) {
        AndroidOZViewerSettingDialog dialog = new AndroidOZViewerSettingDialog(project, canBeParent);

        return dialog.showAndGet();
    }

    public AndroidOZViewerSettingDialog(@Nullable Project project, boolean canBeParent) {
        super(canBeParent);
        this.project = project;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        AndroidOZViewerSettingView view = new AndroidOZViewerSettingView();

        JComponent component = view.getComponent(this);
        component.setPreferredSize(new Dimension(760, 480));
        return component;
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action helpAction = this.getHelpAction();
        return helpAction == this.myHelpAction && this.getHelpId() == null ?
                new Action[]{this.getOKAction()/*, this.getCancelAction()*/} :
                new Action[]{this.getOKAction()/*, this.getCancelAction()*/, helpAction};
    }
}
