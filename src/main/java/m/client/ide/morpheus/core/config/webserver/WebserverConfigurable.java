package m.client.ide.morpheus.core.config.webserver;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import m.client.ide.morpheus.core.config.MorpheusSettingView;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class WebserverConfigurable implements Configurable {
    private final Disposable mDeployDisposable = Disposer.newDisposable();

    public static final String ID = "m.client.ide.morpheus.core.config.CoreConfigurable";
    private MorpheusSettingView morpheusSetting;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Morpheus";
    }

    @Override
    public @Nullable JComponent createComponent() {
        if(morpheusSetting == null) {
            morpheusSetting = new MorpheusSettingView();
        }

        return morpheusSetting.getContents();
    }

    @Override
    public boolean isModified() {
        WebserverSettingsState settings = WebserverSettingsState.getInstance();
        boolean modified = morpheusSetting.isAddComment() != settings.isAddComment();
        if(!modified) modified = morpheusSetting.getMSdkMode() != settings.getMSdkMode();
        if(!modified) modified = morpheusSetting.isShowDebugMessage() != settings.isShowDebugMessage();

        return modified;
    }

    @Override
    public void reset() {
        WebserverSettingsState settings = WebserverSettingsState.getInstance();
        morpheusSetting.setAddComment(settings.isAddComment());
        morpheusSetting.setMSdkMode(settings.getMSdkMode());
        morpheusSetting.setShowDebugMessage(settings.isShowDebugMessage());
    }

    @Override
    public void disposeUIResources() {
        morpheusSetting = null;
    }

    @Override
    public void apply() throws ConfigurationException {
        WebserverSettingsState settings = WebserverSettingsState.getInstance();
        settings.setAddComment(morpheusSetting.isAddComment());
        settings.setMSdkMode(morpheusSetting.getMSdkMode());
        settings.setShowDebugMessage(morpheusSetting.isShowDebugMessage());
    }
}
