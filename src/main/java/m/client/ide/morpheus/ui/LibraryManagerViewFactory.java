package m.client.ide.morpheus.ui;

import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentFactoryImpl;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.ui.dialog.librarymanager.LibraryManagerView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LibraryManagerViewFactory implements ToolWindowFactory {
    private static final Logger LOG = Logger.getInstance(LibraryManagerViewFactory.class);

    public static final String ID = "LibraryManager";
    private LibraryManagerView mlibraryManagerView;

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return MorpheusConfigManager.isMorpheusProject(project);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        mlibraryManagerView = new LibraryManagerView(toolWindow);
        ContentFactoryImpl contentFactory = (ContentFactoryImpl) ContentFactory.SERVICE.getInstance();
        SimpleToolWindowPanel simpleToolWindowPanelContents = new SimpleToolWindowPanel(true);
        simpleToolWindowPanelContents.add(mlibraryManagerView.getComponent(project, toolWindow));

        ActionToolbar actionToolbar = mlibraryManagerView.createActionToolBar();
        actionToolbar.setOrientation(SwingConstants.HORIZONTAL);
        simpleToolWindowPanelContents.setToolbar(actionToolbar.getComponent());

        Content content = contentFactory.createContent(simpleToolWindowPanelContents, "LibraryManager", true);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        ToolWindowFactory.super.init(toolWindow);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return ToolWindowFactory.super.shouldBeAvailable(project);
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return ToolWindowFactory.super.isDoNotActivateOnStart();
    }

    @Override
    public @Nullable ToolWindowAnchor getAnchor() {
        return ToolWindowFactory.super.getAnchor();
    }

    @Override
    public @Nullable Icon getIcon() {
        return ToolWindowFactory.super.getIcon();
    }
}
