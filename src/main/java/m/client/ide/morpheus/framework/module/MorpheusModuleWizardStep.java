package m.client.ide.morpheus.framework.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import m.client.ide.morpheus.framework.template.MorpheusAppTemplateData;
import m.client.ide.morpheus.framework.template.MorpheusTemplateHelper;
import m.client.ide.morpheus.ui.dialog.components.ProjectConfigSettingView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MorpheusModuleWizardStep extends ModuleWizardStep implements Disposable {
    private final ProjectConfigSettingView myview;

    public MorpheusModuleWizardStep(@NotNull WizardContext context) {
        myview = new ProjectConfigSettingView(context);
        myview.init(null);

        JComponent component = myview.getComponent(null);
    }

    /**
     * @return
     */
    @Override
    public JComponent getComponent() {
        return myview.getComponent(null);
    }

    /**
     *
     */
    @Override
    public void updateDataModel() {

    }

    /**
     *
     */
    @Override
    public void dispose() {

    }

    /**
     * @return
     * @throws ConfigurationException
     */
    @Override
    public boolean validate() throws ConfigurationException {
        return myview.validateView();
    }

    public MorpheusAppTemplateData getTemplateData() {
        MorpheusAppTemplateData templateData = new MorpheusAppTemplateData(
                myview.getSelectedLicense(),
                myview.getAndroidAppName(),
                myview.getAndroidPackageName(),
                myview.getIOSAppName(),
                myview.getIOSPackageName(),
                myview.getSelectionCpuList(),
                null, MorpheusTemplateHelper.TemplateType.JAVA_OBJC_EMPTY
        );

        return templateData;
    }
}
