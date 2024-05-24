package m.client.ide.morpheus.ui.action;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.ui.dialog.LibraryManagerDialog;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenLibraryManagerDialogAction extends AbstractMorpheusAction {
    public OpenLibraryManagerDialogAction() {
        this(MessageBundle.message("action.ui.OpenLibraryManagerDialogAction.text"));
    }

    public OpenLibraryManagerDialogAction(String text) {
        super(text);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        @Nullable Project project = anActionEvent.getProject();
        if (project == null) {
            CommonUtil.log(Log.LEVEL_ERROR, OpenLibraryManagerDialogAction.class, project, "Project is null");
            return;
        }

        StringBuilder message =
                new StringBuilder(anActionEvent.getPresentation().getText() + " Selected!");
        // If an element is selected in the editor, add info about it.
        Navigatable selectedElement = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        if (selectedElement != null) {
            message.append("\nSelected Element: ").append(selectedElement);
        }
        String title = anActionEvent.getPresentation().getDescription();
        title = UIMessages.get(UIMessages.LibraryManager_Title);
        LibraryManagerDialog dialog = new LibraryManagerDialog(project, true, title);
//        dialog.setModal(true);
        dialog.show();
    }
}
