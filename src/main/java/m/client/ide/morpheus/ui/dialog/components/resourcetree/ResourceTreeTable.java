package m.client.ide.morpheus.ui.dialog.components.resourcetree;

import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableTree;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableModel;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableNode;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModelAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ResourceTreeTable extends AbstractCheckTreeTable {

    public ResourceTreeTable(AbstractCheckTreeTableModel treeTableModel) {
        super(treeTableModel);

        setRootVisible(true);
        setValueAt(true, 0, 0);

        setHeaderVisible(false);
    }

    private void setHeaderVisible(boolean headerVisible) {
        TreeTableTree tree = getTree();
        JTableHeader header = getTableHeader();
        header.setVisible(headerVisible);
        updateUI();
    }

    @Override
    protected void createMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int doubleclick = 2;
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == doubleclick) {
                }
            }
        });
    }

    @Override
    public ResourceTreeTableNode getSelectedNode() {
        TreeTableTree tree = getTree();
        int row = tree.getTreeTable().getSelectedRow();
        TableModel model = getModel();
        if (model instanceof CheckTreeTableModelAdapter) {
            CheckTreeTableModelAdapter adapter = (CheckTreeTableModelAdapter) model;
            Object node = adapter.nodeForRow(convertRowIndexToModel(row));
            return node instanceof ResourceTreeTableNode ? (ResourceTreeTableNode) node : null;
        }

        return null;
    }

    @Override
    public void setInput(String name, Object input) {
        ResourceTreeTableNode root = new ResourceTreeTableNode(name, input);
        setModel(new ResourceTreeTableModel(root));

        packColumns();
    }

    @Override
    public Object[] getFilteredNodes() {
        return new Object[0];
    }

    /**
     * @param treeTableModel
     */
    @Override
    public void setModel(TreeTableModel treeTableModel) {
        super.setModel(treeTableModel);

        // Gleichzeitiges Selektieren fuer Tree und Table.
        ResourceTreeTableSelectionModel selectionModel = new ResourceTreeTableSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        getCellRenderer().setSelectionModel(selectionModel); //For the tree
        getTree().setSelectionModel(selectionModel);
        setSelectionModel(selectionModel.getListSelectionModel()); //For the table

        setDefaultEditor(String.class, new ResourceTreeTableCellEditor(getCellRenderer(), this));
    }

    @Override
    protected Object @NotNull [] getAllWhiteCheckedItems(Object node) {
        ArrayList<Object> items = new ArrayList<Object>();
        if (node instanceof ResourceTreeTableNode) {
            AbstractCheckTreeTableNode treeNode = (ResourceTreeTableNode) node;
            if (!treeNode.isChecked()) {
                Object[] children = ((ResourceTreeTableNode) node).getChildren();
                if (children != null) {
                    for (Object child : children) {
                        if (child instanceof ResourceTreeTableNode) {
                            items.addAll(List.of(getAllWhiteCheckedItems(child)));
                        }
                    }
                }
            } else {
                Object userObject = ((ResourceTreeTableNode) node).getUserObject();
                if (userObject instanceof File) {
                    items.add(userObject);
                }
            }
        }
        return items.toArray();
    }

}

