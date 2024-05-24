package m.client.ide.morpheus.ui.dialog.components.resourcetree;

import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableCellEditor;

import javax.swing.*;
import java.util.EventObject;

public class ResourceTreeTableCellEditor extends CheckTreeTableCellEditor {
    public ResourceTreeTableCellEditor(JTree tree, JTable table) {
        super(tree, table);
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return false;
    }
}
