package m.client.ide.morpheus.ui.dialog.components;

import com.intellij.ui.DocumentAdapter;
import m.client.ide.morpheus.framework.cli.jsonParam.LicenseParam;
import m.client.ide.morpheus.ui.dialog.licensemanager.LicenseManagerView;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class AndroidNameComponent extends AppNameComponent {
    private final ProjectConfigSettingView projectConfigSettingView;
    private JTextField textFieldAndroidApp;
    private JTextField textFieldAndroidPackage;
    private JPanel androidNameComponent;

    private String prefix_androidId;

    public JTextField getTextFieldAndroidApp() {
        return textFieldAndroidApp;
    }

    public JTextField getTextFieldAndroidPackage() {
        return textFieldAndroidPackage;
    }

    public AndroidNameComponent(ProjectConfigSettingView projectConfigSettingView) {
        super(projectConfigSettingView);

        this.projectConfigSettingView = projectConfigSettingView;

        addModifyListener(textFieldAndroidApp);
        addModifyListener(textFieldAndroidPackage);
    }

    public JComponent getComponent() {
        return androidNameComponent;
    }

    /**
     * @param textField
     */
    @Override
    protected void addModifyListener(@NotNull JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                if(textField == textFieldAndroidPackage) {
                    checkPrefix(textFieldAndroidPackage, prefix_androidId);
                }

                setPageComplete();
            }
        });
    }

    public void addAndroidPackageModifyListener(DocumentAdapter listener) {
        textFieldAndroidPackage.getDocument().addDocumentListener(listener);
    }

    public void addModifyListener(@NotNull JTextField textField, DocumentAdapter listener) {
        textField.getDocument().addDocumentListener(listener);
    }

    public void removeModifyListener(@NotNull JTextField textField, DocumentAdapter listener) {
        textField.getDocument().removeDocumentListener(listener);
    }

    /**
     * @param licenseParam
     */
    @Override
    public void selectedLicense(LicenseParam licenseParam) {
        changeLicense(licenseParam.getAppId());
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
        return "";
    }

    @Override
    public String getIOSPackageName() {
        return "";
    }

    public String getPrefix() {
        return prefix_androidId;
    }

    public void changeLicense(String appId) {
        if(appId != null) {
            String androidId = appId;
            if(androidId.endsWith("*")) {
                androidId = androidId.replace("*", "");
                this.prefix_androidId = androidId;
                textFieldAndroidPackage.setEnabled(true);
            } else if(androidId.startsWith(LicenseManagerView.LICENSE_EDUCATION)) {
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
    }

    public void setAndroidPackageName(@NotNull String packageName) {
        if(!packageName.equals(textFieldAndroidPackage.getText()))
            textFieldAndroidPackage.setText(packageName);
    }
}
