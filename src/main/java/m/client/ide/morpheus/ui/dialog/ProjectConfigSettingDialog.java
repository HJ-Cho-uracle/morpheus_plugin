package m.client.ide.morpheus.ui.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.twelvemonkeys.lang.StringUtil;
import m.client.ide.morpheus.ui.dialog.components.ProjectConfigSettingView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectConfigSettingDialog extends DialogWrapper {
    private final Project project;

    private String androidAppName;
    private String androidPackageName;
    private String iOSAppName;
    private String iOSPackageName;
    private String selectLicense;
    private ProjectConfigSettingView view;

    public ProjectConfigSettingDialog(@Nullable Project project, String title) {
        this(project, false, title);
    }

    public ProjectConfigSettingDialog(@Nullable Project project, boolean canBeParent, String title) {
        super(project, canBeParent);

        this.project = project;
        setTitle(title);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        view = new ProjectConfigSettingView();

        JComponent component = view.getComponent(this);
        component.setPreferredSize(new Dimension(640, 460));
        return component;
    }

    @Override
    protected void init() {
        super.init();
        view.init(this);
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action helpAction = this.getHelpAction();

        return helpAction == this.myHelpAction && this.getHelpId() == null ?
                new Action[]{this.getOKAction()} : new Action[]{this.getOKAction(), helpAction};
    }

    public Project getProject() {
        return project;
    }

    @Override
    public boolean isOKActionEnabled() {
        updateProjectInfo();

        return super.isOKActionEnabled() && isValidInfo();
    }

    private boolean isValidInfo() {
        return !StringUtil.isEmpty(selectLicense) &&
                !StringUtil.isEmpty(androidAppName) &&
                !StringUtil.isEmpty(androidPackageName) &&
                !StringUtil.isEmpty(iOSAppName) &&
                !StringUtil.isEmpty(iOSPackageName);
    }

    private void updateProjectInfo() {
        selectLicense = view.getSelectedLicense();
        androidAppName = view.getAndroidAppName();
        androidPackageName = view.getAndroidPackageName();
        iOSAppName = view.getIOSAppName();
        iOSPackageName = view.getIOSPackageName();
    }

    @Override
    public void doCancelAction() {
//        super.doCancelAction();
    }

    @Override
    protected void doOKAction() {

        selectLicense = view.getSelectedLicense();
        androidAppName = view.getAndroidAppName();
        androidPackageName = view.getAndroidPackageName();
        iOSAppName = view.getIOSAppName();
        iOSPackageName = view.getIOSPackageName();

        super.doOKAction();
    }

    public String getSelectLicense() {
        return selectLicense;
    }

    public String getAndroidAppName() {
        return androidAppName;
    }

    public String getAndroidPackageName() {
        return androidPackageName;
    }

    public String getiOSAppName() {
        return iOSAppName;
    }

    public String getiOSPackageName() {
        return iOSPackageName;
    }

    @NotNull
    public List<Boolean> getSelectionCpuList() {
        if (view != null) {
            return view.getSelectionCpuList();
        }
        ArrayList<Boolean> defaultCpu = new ArrayList<>();
        return defaultCpu;
    }
}
