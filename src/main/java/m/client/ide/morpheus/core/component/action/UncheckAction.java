package m.client.ide.morpheus.core.component.action;


import com.intellij.openapi.actionSystem.*;
import icons.CoreIcons;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UncheckAction extends AnAction {

	public static final String ID = "m.client.ide.morpheus.core.component.action.UnCheckAction";

	public UncheckAction() {
		super();
		Presentation presentation = this.getTemplatePresentation();
		presentation.setIcon(CoreIcons.UnselectAllIcon);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
		@NotNull DataContext dataContext = anActionEvent.getDataContext();
		@Nullable Object component = dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT);
		if(component instanceof AbstractCheckTreeTable)
			CheckAction.checkTreeTableForSelectedRow((AbstractCheckTreeTable) component, false);
	}
}
