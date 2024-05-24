package m.client.ide.morpheus.core.component.checktreetable;

import java.util.EventListener;

public interface TreeItemSelectionChangeListenre extends EventListener {

    void treeItemSelectionChanged(TreeItemSelectionChangeEvent e);

}
