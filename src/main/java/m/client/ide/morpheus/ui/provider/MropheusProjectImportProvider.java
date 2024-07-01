package m.client.ide.morpheus.ui.provider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.projectImport.ProjectImportProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MropheusProjectImportProvider extends ProjectImportProvider {
    /**
     * @return
     */
    @Override
    protected ProjectImportBuilder doGetBuilder() {
        return new MorpheusProjectImportBuilder();
    }

    /**
     * @param fileOrDirectory
     * @param project
     * @return
     */
    @Override
    public boolean canImport(@NotNull VirtualFile fileOrDirectory, @Nullable Project project) {
        return super.canImport(fileOrDirectory, project);
    }
}
