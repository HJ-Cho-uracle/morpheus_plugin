package m.client.ide.morpheus.framework.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import icons.CoreIcons;
import m.client.ide.morpheus.MessageBundle;
import m.client.ide.morpheus.framework.cli.MorpheusCLIUtil;
import m.client.ide.morpheus.framework.template.MorpheusAppTemplateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class MorpheusModuleBuilder extends ModuleBuilder {
    public static final String ID = "m.client.ide.morpheus.framework.module.MorpheusModuleBuilder";
    private MorpheusModuleWizardStep myStep;

    protected Project myProject;

    @Override
    public String getName() {
        return getPresentableName();
    }

    @Nullable
    public Project getProject() {
        return myProject; // Non-null when creating a module.
    }

    @Override
    public String getPresentableName() {
        return MessageBundle.message("morpheus.module.name");
    }

    @Override
    public String getDescription() {
        return MessageBundle.message("morpheus.project.description");
    }

    @Override
    public Icon getNodeIcon() {
        return CoreIcons.icon16;
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel model) {
        doAddContentEntry(model);
        // Add a reference to Dart SDK project library, without committing.
    }

    @Nullable
    @Override
    public Module commitModule(@NotNull Project project, @Nullable ModifiableModuleModel model) {
        final String basePath = getModuleFileDirectory();
        if (basePath == null) {
            Messages.showErrorDialog("Module path not set", "Internal Error");
            return null;
        }
        final VirtualFile baseDir = LocalFileSystem.getInstance().refreshAndFindFileByPath(basePath);
        if (baseDir == null) {
            Messages.showErrorDialog("Unable to determine Flutter project directory", "Internal Error");
            return null;
        }

        // Create the Morpheus module. This indirectly calls setupRootModule, etc.
        final Module morpheus = super.commitModule(project, model);
        if (morpheus == null) {
            return null;
        }

        createProject(project, morpheus);
        return morpheus;
    }

    private void createProject(Project project, @NotNull Module model) {
        MorpheusAppTemplateData templateData = myStep.getTemplateData();

        MorpheusCLIUtil.createProject(
                new File(model.getProject().getBasePath()),
                myStep.getTemplateData()
        );
    }

    @Override
    public boolean validate(@Nullable Project current, @NotNull Project dest) {
        try {
            return myStep.validate();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see <a href="dart.dev/tools/pub/pubspec#name">https://dart.dev/tools/pub/pubspec#name</a>
     */
    @Override
    public boolean validateModuleName(@NotNull String moduleName) throws ConfigurationException {
        return super.validateModuleName(moduleName);
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
        final ModuleWizardStep wizard = super.modifySettingsStep(settingsStep);
        return wizard;
    }

    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep) {
        // Don't allow super to add an SDK selection field (#2052).
        return null;
    }

    @Override
    public ModuleWizardStep getCustomOptionsStep(final WizardContext context, final Disposable parentDisposable) {
        if (!context.isCreatingNewProject()) {
            myProject = context.getProject();
        }
        myStep = new MorpheusModuleWizardStep(context);
        Disposer.register(parentDisposable, myStep);
        return myStep;
    }

    @Override
    public String getBuilderId() {
        // The builder id is used to distinguish between different builders with the same module type, see
        // com.intellij.ide.projectWizard.ProjectTypeStep for an example.
        return ID;
    }

    @Override
    @NotNull
    public ModuleType<?> getModuleType() {
        return new MorpheusModuleType();
    }
}
