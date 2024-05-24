package m.client.ide.morpheus.ui.dialog.components.resourcetree;

import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableModel;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModel;

import javax.swing.*;

import static m.client.ide.morpheus.ui.dialog.components.resourcetree.ResourceTreeTableModel.ResourceColumn.*;


public class ResourceTreeTableModel extends AbstractCheckTreeTableModel {

    public enum ResourceColumn {
        CHECKED(0), NAME(0), UNKNOWN(-1);

        private final int value;

        ResourceColumn(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            switch (this) {
                case CHECKED:
                    return "";
                case NAME:
                    return "이름";

                default:
                    return super.toString().toLowerCase();
            }
        }

        public String getName() {
            return toString();
        }

        public static ResourceColumn valueOf(int value) {
            switch (value) {
                case 0:
                    return CHECKED;
                case 1:
                    return NAME;
                default:
                    return UNKNOWN;
            }
        }

        public static ResourceColumn fromString(String type) {
            if (type.isEmpty())
                return CHECKED;
            if (type.equals("이름"))
                return NAME;
            else
                return UNKNOWN;
        }
    }

    // Spalten Name.
    static protected String[] columnNames = {
            CHECKED.getName(), NAME.getName()};

    // Spalten Typen.
    static protected Class<?>[] columnTypes = {
            Boolean.class, CheckTreeTableModel.class};

    public ResourceTreeTableModel(ResourceTreeTableNode rootNode) {
        super(rootNode);
        root = rootNode;
    }

    @Override
    public int getCheckColumnIndex() {
        return CHECKED.value;
    }

    public Object getChild(Object parent, int index) {
        return ((ResourceTreeTableNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        return ((ResourceTreeTableNode) parent).getChildCount();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((ResourceTreeTableNode) parent).getIndexOfChild(child);
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }


    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        switch (valueOf(column)) {
            case CHECKED:
                return ((ResourceTreeTableNode) node).isChecked();
            case NAME:
                return ((ResourceTreeTableNode) node).getName();
            default:
                break;
        }
        return null;
    }

    public boolean isCellEditable(Object node, int column) {
        ResourceColumn value = valueOf(column);
        return (value == CHECKED || value == NAME); // Important to activate TreeExpandListener
    }


    @Override
    public void setTree(JTree jTree) {

    }
}

