package m.client.ide.morpheus.core.component.checktreetable;

import java.util.EventListener;

public interface TreeItemValueChangeListenre extends EventListener {

    void treeItemValueChanged(TreeItemValueChangeEvent e);
}
