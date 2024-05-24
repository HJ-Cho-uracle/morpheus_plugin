package m.client.ide.morpheus.ui.dialog.components;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class FilterOptionView {
    private JTextField textField1;
    private JTextField textField2;
    private JCheckBox checkBoxPeriod;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JPanel filterOptionView;
    private JSpinner spinnerStart;
    private JSpinner spinnerEnd;
    private Project project;

    public JComponent getComponent(Project project) {
        this.project = project;
        return filterOptionView;
    }

    private void createUIComponents() {
        spinnerStart = new JSpinner();
        spinnerStart.setModel(new SpinnerDateModel());
        spinnerEnd = new JSpinner();
        spinnerEnd.setModel(new SpinnerDateModel());
    }
}
