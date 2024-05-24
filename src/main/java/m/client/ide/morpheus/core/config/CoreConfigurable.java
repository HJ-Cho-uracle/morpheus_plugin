package m.client.ide.morpheus.core.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import m.client.ide.morpheus.core.config.global.CLIConfigManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CoreConfigurable implements Configurable {
    private final Disposable mDeployDisposable = Disposer.newDisposable();

    public static final String ID = "m.client.ide.morpheus.core.config.CoreConfigurable";
    private MorpheusSettingView morpheusSettingView;
    private CLIConfigManager cliConfigManager = CLIConfigManager.getInstance();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Morpheus";
    }

    @Override
    public @Nullable JComponent createComponent() {
        if(morpheusSettingView == null) {
            morpheusSettingView = new MorpheusSettingView();
        }

        return morpheusSettingView.getContents();
    }

    @Override
    public boolean isModified() {
        CoreSettingsState settings = CoreSettingsState.getInstance();
        boolean modified = modified = morpheusSettingView.getNpmPath() != settings.getNpmPath();
        if(!modified) modified = morpheusSettingView.getPodPath() != settings.getPodPath();
        if(!modified) modified = morpheusSettingView.isAddComment() != settings.isAddComment();
        if(!modified) modified = morpheusSettingView.getMSdkMode() != settings.getMSdkMode();
        if(!modified) modified = morpheusSettingView.isShowDebugMessage() != settings.isShowDebugMessage();
        if(!modified) modified = morpheusSettingView.isShowCLIDebug() != settings.isShowCLIDebug();
        if(!modified) modified = morpheusSettingView.getCliVersion() != settings.getCliVersion();
        if(!modified) modified = !morpheusSettingView.getNexusBaseUrl().equals(cliConfigManager.getNexusBaseUrl());
        if(!modified) modified = !morpheusSettingView.getGiteaBaseUrl().equals(cliConfigManager.getGiteaBaseUrl());
        if(!modified) modified = !morpheusSettingView.getGiteaTemplateOrg().equals(cliConfigManager.getGiteaTemplateOrg());
        if(!modified) modified = !morpheusSettingView.getNpmClient().equals(cliConfigManager.getNpmClient());

        return modified;
    }

    @Override
    public void reset() {
        CoreSettingsState settings = CoreSettingsState.getInstance();
        morpheusSettingView.setNpmPath(settings.getNpmPath());
        morpheusSettingView.setPodPath(settings.getPodPath());
        morpheusSettingView.setAddComment(settings.isAddComment());
        morpheusSettingView.setMSdkMode(settings.getMSdkMode());
        morpheusSettingView.setShowDebugMessage(settings.isShowDebugMessage());
        morpheusSettingView.setShowCLIDebugCheckBox(settings.isShowCLIDebug());
        morpheusSettingView.setCliVersion(settings.getCliVersion());
        morpheusSettingView.setToolUpdateForce(settings.isToolUpdateForce());

        morpheusSettingView.setNexusBaseUrl(cliConfigManager.getNexusBaseUrl());
        morpheusSettingView.setGiteaBaseUrl(cliConfigManager.getGiteaBaseUrl());
        morpheusSettingView.setGiteaTemplateOrg(cliConfigManager.getGiteaTemplateOrg());
        morpheusSettingView.setNpmClient(cliConfigManager.getNpmClient());
    }

    @Override
    public void disposeUIResources() {
        morpheusSettingView = null;
    }

    @Override
    public void apply() throws ConfigurationException {
        CoreSettingsState settings = CoreSettingsState.getInstance();
        settings.setAddComment(morpheusSettingView.isAddComment());
        settings.setMSdkMode(morpheusSettingView.getMSdkMode());
        settings.setShowDebugMessage(morpheusSettingView.isShowDebugMessage());
        settings.setShowCLIDebug(morpheusSettingView.isShowCLIDebug());
        settings.setNpmPath(morpheusSettingView.getNpmPath());
        settings.setPodPath(morpheusSettingView.getPodPath());
        settings.setCliVersion(morpheusSettingView.getCliVersion());
        settings.setToolUpdateForce(morpheusSettingView.isToolUpdateForce());

        cliConfigManager.setNexusBaseUrl(morpheusSettingView.getNexusBaseUrl());
        cliConfigManager.setGiteaBaseUrl(morpheusSettingView.getGiteaBaseUrl());
        cliConfigManager.setGiteaTemplateOrg(morpheusSettingView.getGiteaTemplateOrg());
        cliConfigManager.setNpmClient(morpheusSettingView.getNpmClient());
        cliConfigManager.saveToFile();
    }
}
