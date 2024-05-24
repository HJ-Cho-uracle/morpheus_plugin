package m.client.ide.morpheus.core.component.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.util.ui.tree.TreeUtil;
import icons.CoreIcons;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CollapseAllAction extends AnAction {

    public CollapseAllAction() {
        super();
        Presentation presentation = this.getTemplatePresentation();
        presentation.setIcon(CoreIcons.CollapseAllIcon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        @NotNull DataContext dataContext = anActionEvent.getDataContext();
        @Nullable Object component = dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
        if(component instanceof AbstractCheckTreeTable)
            collapseAllRows((AbstractCheckTreeTable) component);
    }

    static void collapseAllRows(AbstractCheckTreeTable treeTable) {
        if(treeTable == null || treeTable.getTree() == null) {
            return;
        }

        TreeUtil.collapseAll(treeTable.getTree(), -1);
    }
}
