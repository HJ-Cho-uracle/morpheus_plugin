package m.client.ide.morpheus.core.component.checktreetable.filetree;

import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableModel;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModel;

import javax.swing.*;

import static m.client.ide.morpheus.core.component.checktreetable.filetree.FileTreeTableModel.FileColumn.*;


public class FileTreeTableModel extends AbstractCheckTreeTableModel {

    public enum FileColumn {
        NAME(0), DATE(1), SIZE(2), UNKNOWN(-1);

        private final int value;

        FileColumn(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            switch (this) {
                case NAME:
                    return "이름";
                case DATE:
                    return "수정일";
                case SIZE:
                    return "크기";

                default:
                    return super.toString().toLowerCase();
            }
        }

        public String getName() {
            return toString();
        }

        public static FileColumn valueOf(int value) {
            switch (value) {
                case 0:
                    return NAME;
                case 1:
                    return DATE;
                case 2:
                    return SIZE;
                default:
                    return UNKNOWN;
            }
        }

        public static FileColumn fromString(String type) {
            if (type.equals("이름"))
                return NAME;
            else if (type.equals("수정일"))
                return DATE;
            else if (type.equals("크기"))
                return SIZE;
            else
                return UNKNOWN;
        }
    }

    // Spalten Name.
    static protected String[] columnNames = {
            NAME.getName(),
            DATE.getName(),
            SIZE.getName() };

    // Spalten Typen.
    static protected Class<?>[] columnTypes = {
            CheckTreeTableModel.class,
            String.class,
            String.class };

    public FileTreeTableModel(FileTreeTableNode rootNode) {
        super(rootNode);
        root = rootNode;
    }

    @Override
    public int getCheckColumnIndex() {
        return UNKNOWN.value;
    }

    public Object getChild(Object parent, int index) {
        return ((FileTreeTableNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        return ((FileTreeTableNode) parent).getChildCount();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((FileTreeTableNode) parent).getIndexOfChild(child);
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }


    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        switch (valueOf(column)) {
            case NAME:
                return ((FileTreeTableNode) node).getName();
            case DATE:
                return ((FileTreeTableNode) node).getDate();
            case SIZE:
                return ((FileTreeTableNode) node).getSize();
            default:
                break;
        }
        return null;
    }

    public boolean isCellEditable(Object node, int column) {
        return true;
    }

    @Override
    public void setTree(JTree jTree) {

    }
}

