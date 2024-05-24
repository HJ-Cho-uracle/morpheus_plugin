package m.client.ide.morpheus.ui.libraryconfig;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MSDKLibraryConfigurable implements Configurable {

    private MSDKLibrarySetting librarySetting;
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return null;
    }

    @Override
    public @Nullable JComponent createComponent() {
        if(librarySetting == null) {
            librarySetting = new MSDKLibrarySetting();
        }

        return librarySetting.getContents();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
