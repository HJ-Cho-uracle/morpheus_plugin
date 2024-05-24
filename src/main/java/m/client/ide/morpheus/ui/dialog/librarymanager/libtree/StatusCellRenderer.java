package m.client.ide.morpheus.ui.dialog.librarymanager.libtree;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusCellRenderer extends DefaultTableCellRenderer {

    public StatusCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String text = getText();
        if(Status.fromString(text) == Status.APPLIED) {
            setForeground(Color.blue);
        } else if(Status.fromString(text) == Status.UPDATABLE) {
            setForeground(Color.red);
        } else {
            setForeground(table.getForeground());
        }

        return renderer;
    }
}
