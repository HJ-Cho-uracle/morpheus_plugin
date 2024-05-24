package m.client.ide.morpheus.core.component.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.util.ui.tree.TreeUtil;
import icons.CoreIcons;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExpandAllAction extends AnAction {

    public ExpandAllAction() {
        super();
        Presentation presentation = this.getTemplatePresentation();
        presentation.setIcon(CoreIcons.SelectAllIcon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        @NotNull DataContext dataContext = anActionEvent.getDataContext();
        @Nullable Object component = dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
        if(component instanceof AbstractCheckTreeTable)
            expandAllRows((AbstractCheckTreeTable) component);
    }

    public static void expandAllRows(AbstractCheckTreeTable treeTable) {
        if(treeTable == null || treeTable.getTree() == null) {
            return;
        }

        TreeUtil.expandAll(treeTable.getTree());
        treeTable.updateUI();
    }
}
