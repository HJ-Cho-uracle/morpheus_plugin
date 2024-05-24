package m.client.ide.morpheus.core.component.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableNode;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModelAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CheckAction extends AnAction {

	public static final String ID = "m.client.ide.morpheus.core.component.action.CheckAction";

    public CheckAction() {
		super();
		Presentation presentation = this.getTemplatePresentation();
		presentation.setIcon(AllIcons.Actions.Checked);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
		@NotNull DataContext dataContext = anActionEvent.getDataContext();
		@Nullable Object component = dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
		if(component instanceof AbstractCheckTreeTable)
			checkTreeTableForSelectedRow((AbstractCheckTreeTable) component, true);
	}

	static void checkTreeTableForSelectedRow(AbstractCheckTreeTable treeTable, boolean checked) {
		if(treeTable == null || treeTable.getTree() == null) {
			return;
		}

		AbstractCheckTreeTableNode selectedNode = treeTable.getSelectedNode();
		if(selectedNode != null) {
			int selectedRow = treeTable.getSelectedRow();
			if(treeTable.getModel() instanceof CheckTreeTableModelAdapter) {
				CheckTreeTableModelAdapter model = (CheckTreeTableModelAdapter) treeTable.getModel();

				int checkIndex = model.getCheckColumnIndex();
				if (checkIndex >= 0) {
					treeTable.setValueAt(checked, selectedRow, checkIndex);
				}
			}
		}
	}
}
