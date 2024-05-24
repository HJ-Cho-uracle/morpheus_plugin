package m.client.ide.morpheus.core.component.checktreetable.filetree;

import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableCellEditor;

import javax.swing.*;
import java.util.EventObject;

public class FileTreeTableCellEditor extends CheckTreeTableCellEditor {
    public FileTreeTableCellEditor(JTree tree, JTable table) {
        super(tree, table);
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return false;
    }
}
