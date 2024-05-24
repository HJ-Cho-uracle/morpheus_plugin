package m.client.ide.morpheus.ui.libraryconfig;

import com.intellij.openapi.externalSystem.ExternalSystemUiAware;
import com.intellij.openapi.externalSystem.importing.ExternalProjectStructureCustomizer;
import com.intellij.openapi.externalSystem.model.DataNode;
import com.intellij.openapi.externalSystem.model.Key;
import com.intellij.openapi.roots.ui.configuration.classpath.ProjectStructureChooseLibrariesDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Set;

public class MSDKLibrayStructures extends ExternalProjectStructureCustomizer {
    @Override
    public @NotNull Set<? extends Key<?>> getIgnorableDataKeys() {
        ProjectStructureChooseLibrariesDialog dialog;

        return null;
    }

    @Override
    public @Nullable Icon suggestIcon(@NotNull DataNode<?> dataNode, @NotNull ExternalSystemUiAware externalSystemUiAware) {
        return null;
    }
}
