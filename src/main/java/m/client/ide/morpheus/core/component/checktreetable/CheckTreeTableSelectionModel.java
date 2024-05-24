package m.client.ide.morpheus.core.component.checktreetable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

public class CheckTreeTableSelectionModel extends DefaultTreeSelectionModel {

    public CheckTreeTableSelectionModel() {
        super();

        setSelectionMode(SINGLE_TREE_SELECTION);
    }

    protected ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }

    public AbstractCheckTreeTableNode getSelectionNode() {
        TreePath path = getSelectionPath();
        if(path != null && path.getLastPathComponent() instanceof AbstractCheckTreeTableNode)
            return (AbstractCheckTreeTableNode) path.getLastPathComponent();

        return null;
    }

    @Override
    public void setSelectionPaths(TreePath[] pPaths) {
        super.setSelectionPaths(pPaths);
        System.out.println("Tree Selection Model selected : " + pPaths[0]);
    }
}
