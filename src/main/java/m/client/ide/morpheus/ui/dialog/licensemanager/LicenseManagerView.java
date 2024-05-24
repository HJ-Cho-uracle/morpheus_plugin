package m.client.ide.morpheus.ui.dialog.licensemanager;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBScrollPane;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.cli.MorpheusCLIUtil;
import m.client.ide.morpheus.framework.cli.config.MorpheusConfigManager;
import m.client.ide.morpheus.framework.cli.jsonParam.LicenseParam;
import m.client.ide.morpheus.framework.template.LicenseTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LicenseManagerView {
    public static final String LICENSE_EDUCATION = "mcore.edu";
    private JTable tableLicense;
    private JCheckBox checkBoxArm64;
    private JCheckBox checkBoxV7a;
    private JCheckBox checkBoxX86;
    private JPanel licManagerComponent;
    private JButton btnAdd;
    private JButton btnDelete;
    private JScrollPane spanelTable;
    private JCheckBox checkBoxX64;
    private JTextField textFieldAppID;
    private JLabel labelAppId;
    private JPanel panelCPUType;
    private JLabel labelCPUType;
    private Project project;
    private String prefix_androidId;


    public LicenseManagerView() {
        this(null);
    }

    public LicenseManagerView(Project project) {
        this.project = project;

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooserDescriptor directoryDescriptor = new FileChooserDescriptor(true, false,
                        true, true, true, false) {
                    public boolean isFileSelectable(@Nullable VirtualFile file) {
                        String ext = "";
                        if(file != null) {
                            ext = file.getExtension();
                            boolean isArchive = FileElement.isArchive(file);
                            CommonUtil.log(Log.LEVEL_DEBUG, LicenseManagerView.class, project,this.getClass().getName() + ":" +
                                    file.getName() + "] ext = " + ext + ", isArchive = " + isArchive);
                        }
                        return super.isFileSelectable(file) && "zip".equalsIgnoreCase(ext);
                    }
                };

                @Nullable VirtualFile selectedFile = FileChooser.chooseFile(directoryDescriptor, project, null);
                addLicenseFile(selectedFile);
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JComponent getComponent(@Nullable Project project) {
        this.project = project;

        boolean isCreate = project == null;
        if(isCreate) {
            ((LicenseTable) tableLicense).setSelectionRow(0);
        } else {
            MorpheusConfigManager morpheusConfig = new MorpheusConfigManager(project);
            textFieldAppID.setText(morpheusConfig.getInfo().getAndroidPackageName());
            textFieldAppID.getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(@NotNull DocumentEvent documentEvent) {
                    checkPrefix(textFieldAppID, prefix_androidId);
                }
            });
        }
        setAppIDVisible(!isCreate);
        setCPUTypeVisible(isCreate);

        btnDelete.setVisible(false);

        return licManagerComponent;
    }

    protected void checkPrefix(JTextField text, String prefix) {
        if(prefix != null) {
            SwingUtilities.invokeLater(() -> {
                String packageName = text.getText();
                if(!packageName.startsWith(prefix)) {
                    text.setText(prefix);
                    text.setSelectionEnd(prefix.length());
                }
            });
        }
    }

    private void setAppIDVisible(boolean visible) {
        labelAppId.setVisible(visible);
        textFieldAppID.setVisible(visible);
    }

    private void setCPUTypeVisible(boolean visible) {
        labelCPUType.setVisible(visible);
        panelCPUType.setVisible(visible);
    }

    private @NotNull List<LicenseParam> getLicenseList() {
        @NotNull Map<String, LicenseParam> licenses = MorpheusCLIUtil.getLicenseList();
        List<LicenseParam> licenseList = new ArrayList<>();
        for(String key : licenses.keySet()) {
            LicenseParam param = licenses.get(key);
            licenseList.add(param);
        }

        return licenseList;
    }

    private void createUIComponents() {
        tableLicense = new LicenseTable(new LicenseTableModel(getLicenseList()));
        spanelTable = new JBScrollPane(tableLicense);
        spanelTable.setRowHeaderView(tableLicense.getTableHeader());

        tableLicense.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object source = e.getSource();
                if(source instanceof DefaultListSelectionModel) {
                    DefaultListSelectionModel model = (DefaultListSelectionModel) source;
                    LicenseParam param = getSelectedLicenseParam(model.getMaxSelectionIndex());
                    if(param != null) {
                        changeLicense(param.getAppId());
                    }
                }
            }
        });
    }

    public void addAppIDModifyListener(DocumentAdapter listener) {
        textFieldAppID.getDocument().addDocumentListener(listener);
    }

    public void removeAppIDModifyListener(DocumentAdapter listener) {
        textFieldAppID.getDocument().removeDocumentListener(listener);
    }

    private void addLicenseFile(@NotNull VirtualFile selectedFile) {
        MorpheusCLIUtil.addLicense(selectedFile.getCanonicalPath());
        refreshLicense();
    }

    private void refreshLicense() {
        tableLicense.setModel(new LicenseTableModel(getLicenseList()));
        ((LicenseTable)tableLicense).setSelectionRow(0);
    }

    public LicenseParam getSelectedLicenseParam() {
        return getSelectedLicenseParam(tableLicense.getSelectedRow());
    }

    private @Nullable LicenseParam getSelectedLicenseParam(int row) {
        TableModel tableModel = tableLicense.getModel();
        if(tableModel instanceof LicenseTableModel) {
            List<LicenseParam> licenseParams = ((LicenseTableModel) tableModel).licenseParams;
            if(licenseParams != null && licenseParams.size() > row) {
                return licenseParams.get(row);
            }
        }

        return null;
    }

    public String getSelectedAppID() {
        return textFieldAppID.getText();
    }

    public List<Boolean> getSelectionCpuList() {
        List<Boolean> selectionCpuList = new ArrayList<>();
        selectionCpuList.add(checkBoxArm64.isSelected());
        selectionCpuList.add(checkBoxV7a.isSelected());
        selectionCpuList.add(checkBoxX86.isSelected());
        selectionCpuList.add(checkBoxX64.isSelected());

        return selectionCpuList;
    }

    public void addLicenseSelectionListener(ListSelectionListener selectionListener) {
        tableLicense.getSelectionModel().addListSelectionListener(selectionListener);
    }

    public void changeLicense(String appId) {
        if(appId != null) {
            if(appId.endsWith("*")) {
                appId = appId.replace("*", "");
                this.prefix_androidId = appId;
                textFieldAppID.setEnabled(true);
            } else if(appId.startsWith(LicenseManagerView.LICENSE_EDUCATION)) {
                textFieldAppID.setEnabled(true);
            } else {
                this.prefix_androidId = null;
                textFieldAppID.setEnabled(false);
            }
            textFieldAppID.setText(appId);
        } else {
            this.prefix_androidId = null;
            textFieldAppID.setText("");
            textFieldAppID.setEnabled(true);
        }
    }

    public void setTextFieldAppID(String appID) {
        if(!appID.equals(textFieldAppID.getText())) {
            textFieldAppID.setText(appID);
        }
    }
}
