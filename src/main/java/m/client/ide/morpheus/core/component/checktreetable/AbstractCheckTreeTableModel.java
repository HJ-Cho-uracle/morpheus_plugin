package m.client.ide.morpheus.core.component.checktreetable;

import com.intellij.ui.CheckedTreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public abstract class AbstractCheckTreeTableModel implements CheckTreeTableModel{
    protected Object root;
    protected EventListenerList valueChandedListeners = new EventListenerList();

    private static final int CHANGED = 0;
    private static final int INSERTED = 1;
    private static final int REMOVED = 2;
    private static final int STRUCTURE_CHANGED = 3;

    public AbstractCheckTreeTableModel(Object root) {
        this.root = root;
    }

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public void setValueAt(Object aValue, Object node, int column) {
        if(column == getCheckColumnIndex() && node instanceof AbstractCheckTreeTableNode && aValue instanceof Boolean) {
            updateChildCheck((AbstractCheckTreeTableNode) node, (Boolean) aValue);
            updateParentCheck((AbstractCheckTreeTableNode) node, (Boolean) aValue);
        }
    }

    private void updateParentCheck(@NotNull AbstractCheckTreeTableNode node, boolean checked) {
        if(!(node.getParent() instanceof AbstractCheckTreeTableNode)) { return; }

        AbstractCheckTreeTableNode parent = (AbstractCheckTreeTableNode) node.getParent();
        boolean parentCheck = true;
        for(int i=0; i<parent.getChildCount(); i++) {
            TreeNode child = parent.getChildAt(i);
            if(child instanceof CheckedTreeNode) {
                parentCheck &= ((CheckedTreeNode) child).isChecked();
            }
        }
        if(parent.isChecked() != parentCheck) {
            parent.setChecked(parentCheck);
            updateParentCheck(parent, parentCheck);
        }
    }

    private void updateChildCheck(@NotNull AbstractCheckTreeTableNode node, boolean checked) {
        node.setChecked(checked);

        for(int i=0; i<node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            if(child instanceof AbstractCheckTreeTableNode) {
                updateChildCheck((AbstractCheckTreeTableNode) child, checked);
            }
        }
    }


    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /**
     * Die Methode wird normalerweise nicht aufgerufen.
     */
    public abstract int getIndexOfChild(Object parent, Object child);

    public abstract int getCheckColumnIndex();

    public void addTreeModelListener(TreeModelListener l) {
        valueChandedListeners.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        valueChandedListeners.remove(TreeModelListener.class, l);
    }

    private void fireTreeNode(int changeType, Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = valueChandedListeners.getListenerList();
        TreeModelEvent e = new TreeModelEvent(source, path, childIndices, children);
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {

                switch (changeType) {
                    case CHANGED:
                        ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
                        break;
                    case INSERTED:
                        ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
                        break;
                    case REMOVED:
                        ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
                        break;
                    case STRUCTURE_CHANGED:
                        ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
                        break;
                    default:
                        break;
                }

            }
        }
    }

    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(CHANGED, source, path, childIndices, children);
    }

    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(INSERTED, source, path, childIndices, children);
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(REMOVED, source, path, childIndices, children);
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(STRUCTURE_CHANGED, source, path, childIndices, children);
    }
}
