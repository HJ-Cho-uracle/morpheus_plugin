package m.client.ide.morpheus.ui.provider;

import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class MorpheusProjectImportBuilder extends ProjectImportBuilder {
    public MorpheusProjectImportBuilder() {
        super();
    }

    /**
     * @return
     */
    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getName() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public Icon getIcon() {
        return null;
    }

    /**
     * @param o
     * @return
     */
    @Override
    public boolean isMarked(Object o) {
        return false;
    }

    /**
     * @param b
     */
    @Override
    public void setOpenProjectSettingsAfter(boolean b) {

    }

    /**
     * @param project
     * @param modifiableModuleModel
     * @param modulesProvider
     * @param modifiableArtifactModel
     * @return
     */
    @Override
    public @Nullable List<Module> commit(Project project, ModifiableModuleModel modifiableModuleModel, ModulesProvider modulesProvider, ModifiableArtifactModel modifiableArtifactModel) {
        return null;
    }
}
