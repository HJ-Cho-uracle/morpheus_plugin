package m.client.ide.morpheus.core.component.checktreetable;

import com.intellij.ui.CheckedTreeNode;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCheckTreeTableNode extends CheckedTreeNode {

    private final String name;

    public AbstractCheckTreeTableNode(String name, Object userObject) {
        super(userObject);

        this.name = name;
        setChecked(false);
        UIUtil.invokeLaterIfNeeded(() -> {
            createChildren();
        });
    }

    public boolean isGrayed() {
        int checkedChild = 0;
        for(Object object : children) {
            if(object instanceof AbstractCheckTreeTableNode &&
                    ((AbstractCheckTreeTableNode)object).isChecked()) {
                checkedChild ++;
            }
        }

        return children.size() != checkedChild;
    }

    public String getName() {
        return name;
    }

    protected abstract void createChildren();

    public @NotNull Object[] getChildren() {
        return children == null ? new Object[0] : children.toArray();
    }
}
