package m.client.ide.morpheus.core.component.checktreetable;

import javax.swing.tree.TreeNode;

public class TreeItemSelectionChangeEvent {

    private final AbstractCheckTreeTableNode node;
    private final int column;

    public TreeItemSelectionChangeEvent(AbstractCheckTreeTableNode node, int column) {
        this.node = node;
        this.column = column;
    }

    public AbstractCheckTreeTableNode getNode() {
        return node;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " : " + node.getName() + "] column : " + column;
    }
}
