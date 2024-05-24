package m.client.ide.morpheus.ui.dialog.librarymanager.libtree;

import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableModel;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModel;
import m.client.ide.morpheus.ui.message.UIMessages;

import javax.swing.*;

import static m.client.ide.morpheus.ui.dialog.librarymanager.libtree.LibTreeTableModel.LibColumn.*;


public class LibTreeTableModel extends AbstractCheckTreeTableModel {

    public enum LibColumn {
        CHECKED(0), NAME(1), REVISION(2), STATUS(3), UNKNOWN(-1);

        private final int value;

        LibColumn(int value) {
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
                    return UIMessages.get(UIMessages.LibView_Name);
                case REVISION:
                    return UIMessages.get(UIMessages.LibView_Revision);
                case STATUS:
                    return UIMessages.get(UIMessages.LibView_Status);

                default:
                    return super.toString().toLowerCase();
            }
        }

        public String getName() {
            return toString();
        }

        public static LibColumn valueOf(int value) {
            switch (value) {
                case 0:
                    return CHECKED;
                case 1:
                    return NAME;
                case 2:
                    return REVISION;
                case 3:
                    return STATUS;
                default:
                    return UNKNOWN;
            }
        }

        public static LibColumn fromString(String type) {
            String typeLower = type.toLowerCase();
            if (typeLower.isEmpty())
                return CHECKED;
            if (typeLower.equals(UIMessages.get(UIMessages.LibView_Name)))
                return NAME;
            else if (typeLower.equals(UIMessages.get(UIMessages.LibView_Revision)))
                return REVISION;
            else if (typeLower.equals(UIMessages.get(UIMessages.LibView_Status)))
                return STATUS;
            else
                return UNKNOWN;
        }
    }

    // Spalten Name.
    static protected String[] columnNames = {
            CHECKED.getName(),
            NAME.getName(),
            REVISION.getName(),
            STATUS.getName() };

    // Spalten Typen.
    static protected Class<?>[] columnTypes = {
            Boolean.class,
            CheckTreeTableModel.class,
            String.class,
            Status.class };

    public LibTreeTableModel(LibTreeTableNode rootNode) {
        super(rootNode);
        root = rootNode;
    }

    @Override
    public int getCheckColumnIndex() {
        return CHECKED.getValue();
    }

    public Object getChild(Object parent, int index) {
        return ((LibTreeTableNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        return ((LibTreeTableNode) parent).getChildCount();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((LibTreeTableNode) parent).getIndexOfChild(child);
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
                return ((LibTreeTableNode) node).isChecked();
            case NAME:
                return ((LibTreeTableNode) node).getName();
            case REVISION:
                return ((LibTreeTableNode) node).getRevision();
            case STATUS:
                return ((LibTreeTableNode) node).getStatus();
            default:
                break;
        }
        return null;
    }

    public void setValueAt(Object aValue, Object node, int column) {
        if(LibColumn.valueOf(column) != STATUS) {
            super.setValueAt(aValue, node, column);
        }
    }

    public boolean isCellEditable(Object node, int column) {
        LibColumn value = valueOf(column);
        return (value == CHECKED || value == NAME); // Important to activate TreeExpandListener
    }

    @Override
    public void setTree(JTree jTree) {

    }
}

