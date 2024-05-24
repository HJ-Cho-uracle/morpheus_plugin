package m.client.ide.morpheus.ui.dialog.components;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AndroidOZViewerSettingView {

    private @NotNull DialogWrapper dialog;

    private JPanel ozViewerSettingComponent;

    public JComponent getComponent(@NotNull DialogWrapper dialog) {
        this.dialog = dialog;

        return ozViewerSettingComponent;
    }
}
