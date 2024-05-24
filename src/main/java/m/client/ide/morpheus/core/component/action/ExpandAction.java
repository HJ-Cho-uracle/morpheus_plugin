package m.client.ide.morpheus.core.component.action;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableModelAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExpandAction extends AnAction {

	public ExpandAction() {
		super();
		Presentation presentation = this.getTemplatePresentation();
		presentation.setIcon(AllIcons.General.ExpandComponent);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
		@NotNull DataContext dataContext = anActionEvent.getDataContext();
		@Nullable Object component = dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
		if(component instanceof AbstractCheckTreeTable)
			expandForSelectedRow((AbstractCheckTreeTable) component);
	}

	static void expandForSelectedRow(AbstractCheckTreeTable treeTable) {
		if(treeTable == null || treeTable.getTree() == null) {
			return;
		}

		int selectedRow = treeTable.getSelectedRow();
		if(treeTable.getModel() instanceof CheckTreeTableModelAdapter) {
			CheckTreeTableModelAdapter model = (CheckTreeTableModelAdapter) treeTable.getModel();
			treeTable.getTree().expandPath(model.pathForRow(selectedRow));
		}
	}
}
