package m.client.ide.morpheus.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.framework.cli.MorpheusCLIUtil;
import m.client.ide.morpheus.framework.cli.jsonParam.LicenseParam;
import m.client.ide.morpheus.ui.dialog.components.ProjectConfigSettingView;
import m.client.ide.morpheus.ui.dialog.licensemanager.LicenseManagerView;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LicenseManagerDialog extends DialogWrapper {
    private final Project project;
    private boolean isOKButtonOnly = false;

    private String selectedAppID;
    private LicenseManagerView view;

    public LicenseManagerDialog(@Nullable Project project, String title) {
        this(project, false, title);
    }
    public LicenseManagerDialog(@Nullable Project project, boolean canBeParent, String title) {
        super(project, canBeParent);

        this.project = project;
        this.isOKButtonOnly = project == null;
        setTitle(title);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        view = new LicenseManagerView(this.project);

        JComponent component = view.getComponent(this.getProject());
        component.setPreferredSize(new Dimension(640, 460));
        return component;
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action helpAction = this.getHelpAction();

        if(isOKButtonOnly) {
            return helpAction == this.myHelpAction && this.getHelpId() == null ?
                    new Action[]{this.getOKAction()} : new Action[]{this.getOKAction(), helpAction};
        } else {
            setOKButtonText(UIMessages.get(UIMessages.LibView_apply));
            return helpAction == this.myHelpAction && this.getHelpId() == null ?
                    new Action[]{this.getOKAction(), this.getCancelAction()/*, this.getCancelAction()*/} :
                    new Action[]{this.getOKAction(), this.getCancelAction()/*, this.getCancelAction()*/, helpAction};
        }
    }

    public Project getProject() {
        return project;
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    @Override
    protected void doOKAction() {
        selectedAppID = view.getSelectedAppID();

        if(validateAppID()) {
            MorpheusCLIUtil.applyLicense(this.project, selectedAppID);
            super.doOKAction();
        }
    }

    private boolean validateAppID() {
        String appID = selectedAppID;
        if(appID.isEmpty()) {
            CommonUtil.openErrorDialog(UIMessages.get(UIMessages.ProjectSettingView_51));
            return false;
        }

        if (!appID.matches(ProjectConfigSettingView.REGEX_PACKAGE_NAME)) {
            CommonUtil.openErrorDialog(UIMessages.get(UIMessages.ProjectSettingView_3));
            return false;
        }
        if (appID.startsWith(".") || appID.endsWith(".")) {
            CommonUtil.openErrorDialog(UIMessages.get(UIMessages.ProjectSettingView_3));
            return false;
        }
        if (!appID.contains(".")) {
            CommonUtil.openErrorDialog(UIMessages.get(UIMessages.ProjectSettingView_3));
            return false;
        }

        if(!isExpirationDate(view.getSelectedLicenseParam()))  {
            CommonUtil.openErrorDialog(UIMessages.get(UIMessages.ProjectSettingView_58));
            return false;
        }

        return true;
    }

    private boolean isExpirationDate(@NotNull LicenseParam licenseParam) {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date to = transFormat.parse(licenseParam.getExpirationDate(), new ParsePosition(0));
        Date currentTime = new Date ();
        return to.compareTo(currentTime) > 0;
    }

    public String getSelectedAppID() {
        return selectedAppID;
    }

    @NotNull
    public List<Boolean> getSelectionCpuList() {
        if(view != null) {
            return view.getSelectionCpuList();
        }
        ArrayList<Boolean> defaultCpu = new ArrayList<>();
        return defaultCpu;
    }
}
