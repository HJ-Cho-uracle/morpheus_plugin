package m.client.ide.morpheus.core.component.action;


import com.intellij.openapi.actionSystem.*;
import icons.CoreIcons;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableNode;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModelAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilteredCheckAction extends AnAction {

	public FilteredCheckAction() {
		super();
		Presentation presentation = this.getTemplatePresentation();
		presentation.setIcon(CoreIcons.SelectUDTIcon);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
		@NotNull DataContext dataContext = anActionEvent.getDataContext();
		@Nullable Object component = dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
		if(component instanceof AbstractCheckTreeTable)
			checkTreeTableForFilteredRow((AbstractCheckTreeTable) component);
	}

	static void checkTreeTableForFilteredRow(AbstractCheckTreeTable treeTable) {
		if(treeTable == null || treeTable.getTree() == null) {
			return;
		}

		AbstractCheckTreeTableNode selectedNode = treeTable.getSelectedNode();
		if(selectedNode != null) {
			@NotNull Object[] selectedRows = treeTable.getFilteredNodes();
			if(treeTable.getModel() instanceof CheckTreeTableModelAdapter) {
				CheckTreeTableModelAdapter model = (CheckTreeTableModelAdapter) treeTable.getModel();

				int checkIndex = model.getCheckColumnIndex();
				if (checkIndex >= 0) {
					for(int i = 0; i < selectedRows.length; i++) {
						model.setValueAt(true, selectedRows[i], checkIndex);
					}
					treeTable.updateUI();
				}
			}
		}
	}
}
