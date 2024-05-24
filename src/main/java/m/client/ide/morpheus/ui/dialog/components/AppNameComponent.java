package m.client.ide.morpheus.ui.dialog.components;

import m.client.ide.morpheus.framework.cli.jsonParam.LicenseParam;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class AppNameComponent {
    protected final IPageCompleter pageCompleter;

    public AppNameComponent(@NotNull IPageCompleter pageCompleter) {
        this.pageCompleter = pageCompleter;
    }

    protected abstract void addModifyListener(JTextField textField);
    protected abstract void selectedLicense(LicenseParam licenseParam);

    protected void checkPrefix(JTextField text, String prefix) {
        if(prefix != null) {
            SwingUtilities.invokeLater(() -> {
                String packageName = text.getText();
                if(!packageName.startsWith(prefix)) {
                    text.setText(prefix);
                    text.setSelectionEnd(prefix.length());
                }
            });
        }
    }

    protected void setPageComplete() {
        pageCompleter.setPageComplete();
    }

    public abstract boolean validateView();
    public abstract String getAndroidAppName();
    public abstract String getAndroidPackageName();
    public abstract String getIOSAppName();
    public abstract String getIOSPackageName();
}
