package m.client.ide.morpheus.core.component.checktreetable;

import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableModelAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;


public class CheckTreeTableModelAdapter extends TreeTableModelAdapter {

    JTree tree;
    AbstractCheckTreeTableModel treeTableModel;

    public CheckTreeTableModelAdapter(@NotNull AbstractCheckTreeTableModel treeTableModel, JTree tree, JTable table) {
        super(treeTableModel, tree, table);

        this.tree = tree;
        this.treeTableModel = treeTableModel;

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }

            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });
    }


    public int getColumnCount() {
        return treeTableModel == null ? 0 : treeTableModel.getColumnCount();
    }

    public String getColumnName(int column) {
        return treeTableModel == null ? "" : treeTableModel.getColumnName(column);
    }

    public Class<?> getColumnClass(int column) {
        return treeTableModel == null ? String.class : treeTableModel.getColumnClass(column);
    }

    public int getRowCount() {
        return tree.getRowCount();
    }

    public TreePath pathForRow(int row) {
        return tree.getPathForRow(row);
    }

    public Object nodeForRow(int row) {
        TreePath treePath = pathForRow(row);
        return treePath == null ? null : treePath.getLastPathComponent();
    }

    public Object getValueAt(int row, int column) {
        return treeTableModel == null ? "" : treeTableModel.getValueAt(nodeForRow(row), column);
    }

    public boolean isCellEditable(int row, int column) {
        return treeTableModel == null ? false : treeTableModel.isCellEditable(nodeForRow(row), column);
    }

    public void setValueAt(Object value, int row, int column) {
        treeTableModel.setValueAt(value, nodeForRow(row), column);

        if(column == treeTableModel.getCheckColumnIndex()) {
            tree.updateUI();
        }
    }

    public void setValueAt(boolean value, Object selectedRow, int column) {
        treeTableModel.setValueAt(value, selectedRow, column);

        if(column == treeTableModel.getCheckColumnIndex()) {
            tree.updateUI();
        }
    }

    public AbstractCheckTreeTableModel getModel() {
        return treeTableModel;
    }

    public int getCheckColumnIndex() {
        return treeTableModel.getCheckColumnIndex();
    }
}

