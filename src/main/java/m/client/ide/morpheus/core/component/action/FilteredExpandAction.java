package m.client.ide.morpheus.core.component.action;


import com.intellij.openapi.actionSystem.*;
import icons.CoreIcons;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableNode;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModelAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;

public class FilteredExpandAction extends AnAction {

	public FilteredExpandAction() {
		super();
		Presentation presentation = this.getTemplatePresentation();
		presentation.setIcon(CoreIcons.ExpandInstIcon);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
		@NotNull DataContext dataContext = anActionEvent.getDataContext();
		@Nullable Object component = dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
		if(component instanceof AbstractCheckTreeTable)
			expandTreeTableForFilteredRow((AbstractCheckTreeTable) component);
	}

	static void expandTreeTableForFilteredRow(AbstractCheckTreeTable treeTable) {
		if(treeTable == null || treeTable.getTree() == null) {
			return;
		}

		@NotNull Object[] filteredNodes = treeTable.getFilteredNodes();
		if(treeTable.getModel() instanceof CheckTreeTableModelAdapter) {
			CheckTreeTableModelAdapter model = (CheckTreeTableModelAdapter) treeTable.getModel();

			int checkIndex = model.getCheckColumnIndex();
			if (checkIndex >= 0) {
				for(int i = 0; i < filteredNodes.length; i++) {
					if(filteredNodes[i] instanceof AbstractCheckTreeTableNode) {
						AbstractCheckTreeTableNode node = (AbstractCheckTreeTableNode) filteredNodes[i];
						treeTable.getTree().expandPath(new TreePath(node.getPath()));
					}
				}
			}
		}
	}
}
