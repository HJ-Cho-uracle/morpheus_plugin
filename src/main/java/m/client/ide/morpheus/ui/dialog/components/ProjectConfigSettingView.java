package m.client.ide.morpheus.ui.dialog.components;

import com.intellij.ide.util.projectWizard.WizardContext;
import m.client.ide.morpheus.framework.cli.jsonParam.LicenseParam;
import m.client.ide.morpheus.ui.dialog.ProjectConfigSettingDialog;
import m.client.ide.morpheus.ui.dialog.licensemanager.LicenseManagerView;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ProjectConfigSettingView implements IPageCompleter {

    public static final String REGEX_PACKAGE_NAME = "^[a-z]+(\\.[a-z0-9]+)*$";
    private WizardContext context;
    private JPanel projectSettingComponent;
    private LicenseManagerView licenseManagerView;
    private JLabel lblErrorMessage;
    private AppNameComponentWithiOS appNameComponent;
    private ProjectConfigSettingDialog projectConfigSettingDialog;

    public ProjectConfigSettingView() {
        projectSettingComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                setPageComplete();
            }
        });
    }

    public ProjectConfigSettingView(WizardContext context) {
        this();

        this.context = context;
    }

    private void createUIComponents() {
        licenseManagerView = new LicenseManagerView();
        JComponent component = licenseManagerView.getComponent(null);
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
        licenseManagerView.addLicenseSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object source = e.getSource();
                if (source instanceof DefaultListSelectionModel) {
                    LicenseParam licenseParam = licenseManagerView.getSelectedLicenseParam();
                    if (licenseParam != null) {
                        selectedLicense(licenseParam);
                    }
                }
            }
        });

//        appNameComponent = new AndroidNameComponent(this);
        appNameComponent = new AppNameComponentWithiOS(this);
    }

    @Override
    public void setPageComplete() {
        if(projectConfigSettingDialog != null) {
            projectConfigSettingDialog.setOKActionEnabled(validateView());
        }
    }

    public void init(ProjectConfigSettingDialog projectConfigSettingDialog) {
        this.projectConfigSettingDialog = projectConfigSettingDialog;
        LicenseParam licenseParam = licenseManagerView.getSelectedLicenseParam();
        if(licenseParam != null) {
            selectedLicense(licenseParam);
        }
        setPageComplete();
    }

    public JComponent getComponent(ProjectConfigSettingDialog projectConfigSettingDialog) {
        this.projectConfigSettingDialog = projectConfigSettingDialog;

        if(context != null) {
            projectSettingComponent.setBorder(BorderFactory.createEtchedBorder());
        }

        return projectSettingComponent;
    }

    public String getSelectedLicense() {
        return appNameComponent.getAndroidPackageName();
    }

    public List<Boolean> getSelectionCpuList() {
        return licenseManagerView.getSelectionCpuList();
    }

    public void selectedLicense(LicenseParam licenseParam) {
        if(!isExpirationDate(licenseParam))  {
            setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_58));
            return;
        }

        appNameComponent.selectedLicense(licenseParam);
        licenseManagerView.setTextFieldAppID(appNameComponent.getAndroidPackageName());
    }

    private boolean isExpirationDate(@NotNull LicenseParam licenseParam) {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date to = transFormat.parse(licenseParam.getExpirationDate(), new ParsePosition(0));
        Date currentTime = new Date ();
        return to.compareTo(currentTime) > 0;
    }

    public boolean validateView() {
        if(licenseManagerView.getSelectedLicenseParam() == null) {
            setErrorMessage(UIMessages.get(UIMessages.ProjectSettingView_59));
            return false;
        }

        if(appNameComponent.validateView() == false) {
            return false;
        }

        setErrorMessage(null);
        return true;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.lblErrorMessage.setText(Objects.requireNonNullElse(errorMessage, ""));
    }

    public String getErrorMessage() {
        return lblErrorMessage.getText();
    }

    public LicenseParam getSelectedLicenseParam() {
        return licenseManagerView.getSelectedLicenseParam();
    }

    public String getAndroidAppName() {
        return appNameComponent.getAndroidAppName();
    }

    public String getAndroidPackageName() {
        return appNameComponent.getAndroidPackageName();
    }

    public String getIOSAppName() {
        return appNameComponent.getIOSAppName();
    }

    public String getIOSPackageName() {
        return appNameComponent.getIOSPackageName();
    }
}
