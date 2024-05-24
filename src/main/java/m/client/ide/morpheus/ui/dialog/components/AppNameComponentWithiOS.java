package m.client.ide.morpheus.ui.dialog.components;

import com.intellij.ui.DocumentAdapter;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.framework.cli.jsonParam.LicenseParam;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppNameComponentWithiOS extends AppNameComponent{
    private final ProjectConfigSettingView projectConfigSettingView;
    private JRadioButton radioButtonBoth;
    private JRadioButton radioButtonDiff;
    private JTextField textFieldAndroidApp;
    private JTextField textFieldAndroidPackage;
    private JTextField textFieldIOSApp;
    private JTextField textFieldIOSPackage;
    private JPanel appNameComponentWithiOS;

    private boolean androidAppNameLock;
    private boolean iOSAppNameLock;
    private boolean packageIdLock;
    private boolean bundleIdLock;

    private String prefix_androidId;
    private String prefix_iOSId;

    public AppNameComponentWithiOS(@NotNull ProjectConfigSettingView projectConfigSettingView) {
        super(projectConfigSettingView);

        this.projectConfigSettingView = projectConfigSettingView;

        addModifyListener(textFieldAndroidApp);
        addModifyListener(textFieldIOSApp);
        addModifyListener(textFieldAndroidPackage);
        addModifyListener(textFieldIOSPackage);

        if(OSUtil.isMac()) {
            textFieldIOSApp.setEnabled(true);
            textFieldIOSPackage.setEnabled(true);
            radioButtonBoth.setEnabled(true);
            radioButtonDiff.setEnabled(true);
            radioButtonBoth.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(radioButtonBoth.isSelected()) {
                        textFieldIOSApp.setText(textFieldAndroidApp.getText());
                        textFieldIOSPackage.setText(textFieldAndroidPackage.getText());
                    }
                }
            });
        }
    }

    public JComponent getComponent() {
        return appNameComponentWithiOS;
    }

    /**
     * @param textField
     */
    @Override
    protected void addModifyListener(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                if(textField == textFieldAndroidPackage) {
                    checkPrefix(textFieldAndroidPackage, prefix_androidId);
                } else if(textField == textFieldIOSPackage) {
                    checkPrefix(textFieldIOSPackage, prefix_iOSId);
                }

                if(OSUtil.isMac() && radioButtonBoth.isSelected()) {
                    if(textField == textFieldAndroidApp && !androidAppNameLock) {
                        iOSAppNameLock = true;
                        textFieldIOSApp.setText(textFieldAndroidApp.getText());
                        iOSAppNameLock = false;
                    } else if(textField == textFieldIOSApp && !iOSAppNameLock) {
                        androidAppNameLock = true;
                        textFieldAndroidApp.setText(textFieldIOSApp.getText());
                        androidAppNameLock = false;
                    }

                    LicenseParam license = projectConfigSettingView.getSelectedLicenseParam();
                    if(license != null) {
                        String bundleId = license.getBundleId();
                        String packageId = license.getPackageName();
                        if((bundleId == null && packageId == null) || (bundleId != null && bundleId.equals(packageId))) {
                            if(textField == textFieldAndroidPackage && !packageIdLock) {
                                bundleIdLock = true;
                                textFieldIOSPackage.setText(textFieldAndroidPackage.getText());
                                bundleIdLock = false;
                            } else if(textField == textFieldIOSPackage && !bundleIdLock) {
                                packageIdLock = true;
                                textFieldAndroidPackage.setText(textFieldIOSPackage.getText());
                                packageIdLock = false;
                            }
                        }
                    }
                }

                setPageComplete();
            }
        });
    }

    /**
     * @param licenseParam
     */
    @Override
    public void selectedLicense(LicenseParam licenseParam) {
        String packageName = licenseParam.getPackageName();
        String bundleId = licenseParam.getBundleId();
        if(packageName != null) {
            String androidId = packageName;
            if(androidId.endsWith("*")) {
                androidId = androidId.replace("*", "");
                this.prefix_androidId = androidId;
                textFieldAndroidPackage.setEnabled(true);
            } else {
                this.prefix_androidId = null;
                textFieldAndroidPackage.setEnabled(false);
            }
            textFieldAndroidPackage.setText(androidId);
        } else {
            this.prefix_androidId = null;
            textFieldAndroidPackage.setText("");
            textFieldAndroidPackage.setEnabled(true);
        }

        if(OSUtil.isMac()) {
            if(bundleId != null) {
                String iosId = bundleId;
                if(iosId.endsWith("*")) {
                    iosId = iosId.replace("*", "");
                    this.prefix_iOSId = iosId;
                    textFieldIOSPackage.setEnabled(true);
                } else {
                    this.prefix_iOSId = null;
                    textFieldIOSPackage.setEnabled(false);
                }
                textFieldIOSPackage.setText(iosId);
            } else {
                this.prefix_iOSId = null;
                textFieldIOSPackage.setText("");
                textFieldIOSPackage.setEnabled(true);
            }
        }
        setPageComplete();
    }

    /**
     * @return
     */
    @Override
    public boolean validateView() {
        String androidAppName = getAndroidAppName();
        if(androidAppName.isEmpty()) {
            projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_51));
            return false;
        }

        String androidPackageName = getAndroidPackageName();
        if (!androidPackageName.matches(ProjectConfigSettingView.REGEX_PACKAGE_NAME)) {
            projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_3));
            return false;
        }
        if (androidPackageName.isEmpty()) { //$NON-NLS-1$
            projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_2));
            return false;
        }
        if (androidPackageName.startsWith(".") || androidPackageName.endsWith(".")) {
            projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_3));
            return false;
        }
        if (!androidPackageName.contains(".")) {
            projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_3));
            return false;
        }

        if (OSUtil.isMac()) {
            String iOSAppName = getIOSAppName();
            if(iOSAppName.isEmpty()) {
                projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_52));
                return false;
            }

            String iosBundleIdentifier = getIOSPackageName();
            if (iosBundleIdentifier.isEmpty()) {
                projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_19));
                return false;
            }
            if (iosBundleIdentifier.startsWith(".") || iosBundleIdentifier.endsWith(".")) {
                projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_20));
                return false;
            }
            if (!iosBundleIdentifier.contains(".")) {
                projectConfigSettingView.setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_20));
                return false;
            }
        } else {
            textFieldIOSApp.setEnabled(false);
            textFieldIOSPackage.setEnabled(false);
            radioButtonBoth.setSelected(true);
            radioButtonBoth.setEnabled(false);
            radioButtonDiff.setEnabled(false);
        }

        return true;
    }

    @Override
    public String getAndroidAppName() {
        return textFieldAndroidApp.getText();
    }

    @Override
    public String getAndroidPackageName() {
        return textFieldAndroidPackage.getText();
    }

    @Override
    public String getIOSAppName() {
        return textFieldIOSApp.getText();
    }

    @Override
    public String getIOSPackageName() {
        return textFieldIOSPackage.getText();
    }
}
