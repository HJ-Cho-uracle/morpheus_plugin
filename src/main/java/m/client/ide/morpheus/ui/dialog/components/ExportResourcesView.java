package m.client.ide.morpheus.ui.dialog.components;

import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.mac.MacFileSaverDialog;
import m.client.ide.morpheus.core.utils.PreferenceUtil;
import m.client.ide.morpheus.core.utils.ZipUtils;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.ui.dialog.components.resourcetree.ResourceTreeTable;
import m.client.ide.morpheus.ui.dialog.components.resourcetree.ResourceTreeTableModel;
import m.client.ide.morpheus.ui.dialog.components.resourcetree.ResourceTreeTableNode;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class ExportResourcesView {
    private static final String RESOURCE = "resource";
    private final Project project;

    private JPanel exportResourceView;
    private JScrollPane scrollPaneFile;
    private ResourceTreeTable resourceTreeTable;
    private JButton buttonExpand;
    private JButton buttonFilter;
    private TextFieldWithBrowseButton textFieldDestPath;

    public ExportResourcesView(Project project) {
        this.project = project;
    }

    private void createUIComponents() {
        File resFolder = LaunchUtil.getResFolder(project);
        resourceTreeTable = new ResourceTreeTable(new ResourceTreeTableModel(new ResourceTreeTableNode(resFolder.getName(), resFolder)));

        textFieldDestPath = new TextFieldWithBrowseButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileSaverDescriptor directoryDescriptor = new FileSaverDescriptor(UIMessages.get(UIMessages.ExportiOSFileFale), "", ZipUtils.extension) {
                    public boolean isFileSelectable(@Nullable VirtualFile file) {
                        return super.isFileSelectable(file) && file != null;
                    }
                };

                @NotNull File desrination = new File(textFieldDestPath.getText());

                @Nullable VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(desrination.isFile() ? desrination.getParentFile() : desrination);
                String fileName = desrination.isFile() ? desrination.getName() : RESOURCE + ZipUtils.extension;
                @Nullable VirtualFileWrapper selectedFile = new MacFileSaverDialog(directoryDescriptor, project).save(file, fileName);
                if (selectedFile != null) {
                    String path = selectedFile.getFile().getPath();
                    if (!path.endsWith(ZipUtils.extension)) {
                        path += ZipUtils.extension;
                    }
                    textFieldDestPath.setText(path);
                }
            }
        });

        String destination = PreferenceUtil.getIOSExportDestination();
        if (destination != null && !destination.isEmpty()) {
            String defaultDestination = new File(destination, RESOURCE + ZipUtils.extension).getAbsolutePath();
            textFieldDestPath.setText(defaultDestination);
        }
    }

    public JComponent getComponent(Project project) {
        return exportResourceView;
    }

    public List getWhiteCheckedResources() {
        return List.of(resourceTreeTable.getAllWhiteCheckedItems());
    }

    public String getDestinationValue() {
        return textFieldDestPath.getText();
    }
}
