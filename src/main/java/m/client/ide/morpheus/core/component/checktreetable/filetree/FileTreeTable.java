package m.client.ide.morpheus.core.component.checktreetable.filetree;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableTree;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableModel;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModelAdapter;
import m.client.ide.morpheus.core.utils.FileUtil;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class FileTreeTable extends AbstractCheckTreeTable {

    public FileTreeTable(AbstractCheckTreeTableModel treeTableModel) {
        super(treeTableModel);
    }

    @Override
    protected void createMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int doubleclick = 2;
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == doubleclick) {
                    FileTreeTableNode selection = getSelectedNode();
                    if (selection != null) {
                        PsiFile file = selection.getPsiFile();
                        if (file != null) {
                            if (!file.isDirectory() && file.isWritable()) {
                                VirtualFile vFile = file.getVirtualFile();
                                FileUtil.openEditor(vFile);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public FileTreeTableNode getSelectedNode() {
        TreeTableTree tree = getTree();
        int row = tree.getTreeTable().getSelectedRow();
        TableModel model = getModel();
        if (model instanceof CheckTreeTableModelAdapter) {
            CheckTreeTableModelAdapter adapter = (CheckTreeTableModelAdapter) model;
            Object node = adapter.nodeForRow(convertRowIndexToModel(row));
            return node instanceof FileTreeTableNode ? (FileTreeTableNode) node : null;
        }

        return null;
    }

    @Override
    public void setInput(String name, Object input) {
        FileTreeTableNode root = new FileTreeTableNode(name, input);
        setModel(new FileTreeTableModel(root));

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
        FileTreeTableSelectionModel selectionModel = new FileTreeTableSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        getCellRenderer().setSelectionModel(selectionModel); //For the tree
        getTree().setSelectionModel(selectionModel);
        setSelectionModel(selectionModel.getListSelectionModel()); //For the table

        setDefaultEditor(String.class, new FileTreeTableCellEditor(getCellRenderer(), this));
    }
}

