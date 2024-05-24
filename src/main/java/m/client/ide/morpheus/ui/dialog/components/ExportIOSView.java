package m.client.ide.morpheus.ui.dialog.components;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.mac.MacFileSaverDialog;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.PreferenceUtil;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.launch.configuration.IOSRunConfigField;
import m.client.ide.morpheus.ui.dialog.FilterOptionDialog;
import m.client.ide.morpheus.ui.dialog.components.resourcetree.ResourceTreeTable;
import m.client.ide.morpheus.ui.dialog.components.resourcetree.ResourceTreeTableModel;
import m.client.ide.morpheus.ui.dialog.components.resourcetree.ResourceTreeTableNode;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExportIOSView {
    private static final String IPA_SUFFIX = ".ipa";
    private final Project project;
    private JPanel exportIosView;
    private JScrollPane scrollPaneFile;
    private ResourceTreeTable resourceTreeTable;
    private JButton buttonExpand;
    private JButton buttonFilter;
    private JComboBox comboDevelopCert;
    private JTextField textFieldTeamId;
    private JComboBox comboSDK;
    private TextFieldWithBrowseButton textFieldDestPath;
    private ArrayList<String> iosDeveloperList;
    private String isLaunchTest;


    public ExportIOSView(Project project) {
        this.project = project;
        buttonFilter.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterOptionDialog dialog = new FilterOptionDialog(project, "Filter Option");
                dialog.show();
                if (dialog.isOK()) {
//                    Object[] newSelectedTypes = dialog.getResult();
//                    boolean isRefineResults = dialog.isRefineResults();
//                    Date startDate = null;
//                    Date endDate = null;
//                    String searchString = dialog.getSearchString();
//                    boolean caseSensitive = dialog.isCaseSensitive();
//
//                    if (newSelectedTypes != null) {
//                        this.selectedTypes = new ArrayList<Object>(newSelectedTypes.length);
//                        for (int i = 0; i < newSelectedTypes.length; i++) {
//                            this.selectedTypes.add(newSelectedTypes[i]);
//                        }
//                    }
//
//                    if (dialog.isAfterDate()) {
//                        startDate = dialog.getStartDate();
//                        endDate = dialog.getEndDate();
//                        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
//                        // java.util.Locale.KOREA);
//                        // System.out.println(formatter.format(endDate));
//                    }
//                    setupSelectionsBasedOnSelectedTypes(startDate, endDate, searchString, isRefineResults, caseSensitive);
//                    Object[] newSelectedTypes = dialog.getResult();
//                    boolean isRefineResults = dialog.isRefineResults();
//                    Date startDate = null;
//                    Date endDate = null;
//                    String searchString = dialog.getSearchString();
//                    boolean caseSensitive = dialog.isCaseSensitive();
//
//                    if (newSelectedTypes != null) {
//                        this.selectedTypes = new ArrayList<Object>(newSelectedTypes.length);
//                        for (int i = 0; i < newSelectedTypes.length; i++) {
//                            this.selectedTypes.add(newSelectedTypes[i]);
//                        }
//                    }
//
//                    if (dialog.isAfterDate()) {
//                        startDate = dialog.getStartDate();
//                        endDate = dialog.getEndDate();
//                        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
//                        // java.util.Locale.KOREA);
//                        // System.out.println(formatter.format(endDate));
//                    }
//                    setupSelectionsBasedOnSelectedTypes(startDate, endDate, searchString, isRefineResults, caseSensitive);
                }
            }
        });
        buttonExpand.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (resourceTreeTable != null) {
                    resourceTreeTable.expandAll();
                }
            }
        });

        comboDevelopCert.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String developerName = (String) comboDevelopCert.getItemAt(comboDevelopCert.getSelectedIndex());
                    AtomicBoolean isTest = new AtomicBoolean(false);
                    textFieldTeamId.setText(LaunchUtil.getIOSDevelopmentTeam(developerName, isTest));
                    isLaunchTest = isTest.get() ? "TRUE" : "FALSE";
                }
            }
        });

        initSDK();
        initDevelopCertificateInfo();
    }

    /**
     * 확장자 필터 적용
     */
    private void setupSelectionsBasedOnSelectedTypes(final Date startDate, final Date endDate,
                                                     final String searchString, final boolean isRefineResults, final boolean caseSensitive) {

//        Runnable runnable = () -> {
//            Map<File, List<File>> selectionMap = new Hashtable<>();
//
//            List<?> resources = null;
//            if (isRefineResults) {
//                resources = List.of(resourceTreeTable.getAllWhiteCheckedItems());
//            } else {
//                resources = resourceTreeTable.getAllItems();
//            }
//            Iterator<?> resourceIterator = resources.iterator();
//            while (resourceIterator.hasNext()) {
//                IResource resource = resourceIterator.next();
//                if (resource.getType() == IResource.FILE) {
//                    if (hasExportableExtension(resource.getName())
//                            && hasExportableDate(resource, startDate, endDate)
//                            && hasExportableName(resource.getName(), searchString, caseSensitive)) {
//                        List<IResource> resourceList = new ArrayList<IResource>();
//                        IContainer parent = resource.getParent();
//                        if (selectionMap.containsKey(parent)) {
//                            resourceList = (List<IResource>) selectionMap.get(parent);
//                        }
//                        resourceList.add(resource);
//                        selectionMap.put(parent, resourceList);
//                    }
//                } else {
//                    setupSelectionsBasedOnSelectedTypes(selectionMap, (IContainer) resource, startDate, endDate,
//                            searchString, caseSensitive);
//                }
//            }
//            resourceGroup.updateSelections(selectionMap);
//        };
//        BusyIndicator.showWhile(getShell().getDisplay(), runnable);
    }


    private void createUIComponents() {
        File resFolder = LaunchUtil.getResFolder(project);
        resourceTreeTable = new ResourceTreeTable(new ResourceTreeTableModel(new ResourceTreeTableNode(resFolder.getName(), resFolder)));

        textFieldDestPath = new TextFieldWithBrowseButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileSaverDescriptor directoryDescriptor = new FileSaverDescriptor(UIMessages.get(UIMessages.ExportiOSFileFale), "", IPA_SUFFIX) {
                    public boolean isFileSelectable(@Nullable VirtualFile file) {
                        return super.isFileSelectable(file) && file != null;
                    }
                };

                @NotNull File desrination = new File(textFieldDestPath.getText());

                @Nullable VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(desrination.isFile() ? desrination.getParentFile() : desrination);
                String fileName = desrination.isFile() ? desrination.getName() : project.getName() + IPA_SUFFIX;
                @Nullable VirtualFileWrapper selectedFile = new MacFileSaverDialog(directoryDescriptor, project).save(file, fileName);
                if (selectedFile != null) {
                    String path = selectedFile.getFile().getPath();
                    if (!path.endsWith(IPA_SUFFIX)) {
                        path += IPA_SUFFIX;
                    }
                    textFieldDestPath.setText(path);
                }
            }
        });

        String destination = PreferenceUtil.getIOSExportDestination();
        if (destination != null && !destination.isEmpty()) {
            String defaultDestination = new File(destination, project.getName() + IPA_SUFFIX).getAbsolutePath();
            textFieldDestPath.setText(defaultDestination);
        }
    }

    public JComponent getComponent(Project project) {
        return exportIosView;
    }

    private void initSDK() {
        ArrayList<String> targetSDKs = LaunchUtil.getIOSTargetSDKList();

        if (targetSDKs != null && targetSDKs.size() > 0) {
            for (String target : targetSDKs) {
                comboSDK.addItem(target);
            }
            comboSDK.setSelectedIndex(0);
        }

    }

    private void initDevelopCertificateInfo() {
        comboDevelopCert.removeAll();
        try {
            iosDeveloperList = LaunchUtil.getIOSDeveloperList();
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
            return;
        }

        if (iosDeveloperList != null && iosDeveloperList.size() > 0) {
            for (String iosDeveloper : iosDeveloperList) {
                comboDevelopCert.addItem(iosDeveloper);
            }
        } else {
            comboDevelopCert.addItem("");
        }
        String deceloperCertificate = PreferenceUtil.getIOSDeveloperCertificate();
        updateSelectedCombobox(comboDevelopCert, deceloperCertificate);
    }

    private void updateSelectedCombobox(JComboBox comboBox, String selected) {
        if (comboBox == null) {
            return;
        }

        if (selected == null || selected.isEmpty()) {
            comboBox.setSelectedItem(0);
            return;
        }

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Object item = comboBox.getItemAt(i);
            if (selected.equals(item)) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    public List getWhiteCheckedResources() {
        return List.of(resourceTreeTable.getAllWhiteCheckedItems());
    }

    public String getDestinationValue() {
        return textFieldDestPath.getText();
    }

    public String getDeveloperName() {
        return (String) comboDevelopCert.getSelectedItem();
    }

    public String getTargetVersion() {
        String sdk = (String) comboSDK.getSelectedItem();
        if (sdk.startsWith(IOSRunConfigField.DEVICE_TYPE_IPHONE)) {
            return sdk.replace(IOSRunConfigField.DEVICE_TYPE_IPHONE, "");
        }
        return sdk.replace(IOSRunConfigField.DEVICE_TYPE_IPHONE_SIMULATOR, "");
    }

    public String getDevelopmentTeam() {
        return textFieldTeamId.getText();
    }

    public String getTargetType() {
        String sdk = (String) comboSDK.getSelectedItem();
        if (sdk.startsWith(IOSRunConfigField.DEVICE_TYPE_IPHONE)) {
            return IOSRunConfigField.DEVICE_TYPE_IPHONE;
        } else {
            return IOSRunConfigField.DEVICE_TYPE_IPHONE_SIMULATOR;
        }
    }
}
