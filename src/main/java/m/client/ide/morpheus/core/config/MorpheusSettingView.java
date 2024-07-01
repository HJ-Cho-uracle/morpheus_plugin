package m.client.ide.morpheus.core.config;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import m.client.ide.morpheus.core.constants.SettingConstants;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.ExecCommandUtil;
import m.client.ide.morpheus.core.utils.OSUtil;
import m.client.ide.morpheus.core.utils.PreferenceUtil;
import m.client.ide.morpheus.framework.eclipse.library.UnapplyLibraryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.File;

public class MorpheusSettingView {
    private static final Logger LOG = Logger.getInstance(MorpheusSettingView.class);
    private static final String SECRET_KEY = "34794400";

    private JCheckBox addCommnetsCheckBox;
    private JRadioButton morpheusRadioButton;
    private JRadioButton devRadioButton;
    private JCheckBox showDebugMessageCheckBox;
    private JPanel secreatPanel;
    private JPanel morpheusPanel;
    private TextFieldWithBrowseButton textFieldNpm;
    private TextFieldWithBrowseButton textFieldPod;
    private JTextField textFieldNexusBaseUrl;
    private JTextField textFieldGiteaBaseUrl;
    private JTextField textFieldGiteaTemplateOrg;
    private JTextField textFieldNpmClient;
    private JTextField textFieldCliVersion;
    private JCheckBox showCLIDebugCheckBox;
    private JCheckBox checkBoxToolUpdate;
    private JButton makeUnapplyInfoButton;
    private TextFieldWithBrowseButton unapplyLocation;
    private JButton testButton;

    private StringBuffer secretBuffer = new StringBuffer();
    private KeyListener secretKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);

            LOG.debug("Key Pressed : " + e.getSource() + " : " + e.getKeyChar());
            secretBuffer.append(e.getKeyChar());
            if (secretBuffer.toString().equals(SECRET_KEY)) {
                setSecretBodyVisible(true);
                setEnableDevMode(CoreSettingsState.getInstance().isDevMode());
            }
        }
    };
    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            secretBuffer = new StringBuffer();
        }
    };

    public MorpheusSettingView() {
        super();

        createListeners();
        makeUnapplyInfoButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Task.Modal task = new Task.Modal(null, UnapplyLibraryManager.class.getSimpleName(), false) {
                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {
                        UnapplyLibraryManager unapplyManager = new UnapplyLibraryManager(progressIndicator);
                        CommonUtil.log(Log.LEVEL_DEBUG, unapplyManager.toString());
                        unapplyManager.writeUnapplyLibraryInfo(unapplyLocation.getText());
                    }
                };
                ExecCommandUtil.runProcessWithProgressSynchronously(task, task.getTitle(), false, null);
            }
        });
        testButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                UnapplyLibraryManager manager = UnapplyLibraryManager.getInstance();
                System.out.println(manager.toString());
                System.out.println(manager.getUnapplyInfo("core", "m.client.library.core.2.1.8.42"));
            }
        });
    }

    private void createListeners() {
        devRadioButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setEnableDevMode(devRadioButton.isSelected());
            }
        });

        showDebugMessageCheckBox.setSelected(PreferenceUtil.getShowDebugMessage());
        showDebugMessageCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                CoreSettingsState.getInstance().setShowDebugMessage(showDebugMessageCheckBox.isSelected());
            }
        });
        showCLIDebugCheckBox.setSelected((PreferenceUtil.getShowCLIDebug()));
        showCLIDebugCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                CoreSettingsState.getInstance().setShowCLIDebug(showCLIDebugCheckBox.isSelected());
            }
        });

        addCommnetsCheckBox.addKeyListener(secretKeyListener);
        addCommnetsCheckBox.addMouseListener(mouseListener);
    }

    private void setEnableDevMode(boolean isDevMode) {
        if (isDevMode) {
            showDebugMessageCheckBox.setEnabled(true);
            showDebugMessageCheckBox.setSelected(true);
            showCLIDebugCheckBox.setEnabled(true);
        } else {
            showDebugMessageCheckBox.setEnabled(false);
            showDebugMessageCheckBox.setSelected(false);
            showCLIDebugCheckBox.setEnabled(false);
            showCLIDebugCheckBox.setSelected(false);
        }
    }

    public JComponent getContents() {
        return morpheusPanel;
    }

    public String getNpmPath() {
        return textFieldNpm.getText();
    }

    public String getPodPath() {
        return textFieldPod.getText();
    }

    public boolean isAddComment() {
        return addCommnetsCheckBox.isSelected();
    }

    public String getMSdkMode() {
        return morpheusRadioButton.isSelected() ? SettingConstants.SDK_MODE_MORPHEUS : SettingConstants.SDK_MODE_DEV;
    }

    public boolean isShowDebugMessage() {
        return showDebugMessageCheckBox.isSelected();
    }

    public boolean isShowCLIDebug() {
        return showCLIDebugCheckBox.isSelected();
    }

    public void setNpmPath(String npmPath) {
        this.textFieldNpm.setText(npmPath);
    }

    public void setPodPath(String podPath) {
        this.textFieldPod.setText(podPath);
    }

    public void setAddComment(boolean isAddComment) {
        this.addCommnetsCheckBox.setSelected(isAddComment);
    }

    public void setMSdkMode(String mSdkMode) {
        boolean isMorpheusMode = SettingConstants.SDK_MODE_MORPHEUS.equals(mSdkMode);
        morpheusRadioButton.setSelected(isMorpheusMode);
        devRadioButton.setSelected(SettingConstants.SDK_MODE_DEV.equals(mSdkMode));

        setSecretBodyVisible(!isMorpheusMode);
    }

    public void setShowDebugMessage(boolean showDebugMessage) {
        showDebugMessageCheckBox.setSelected(showDebugMessage);
    }

    public void setShowCLIDebugCheckBox(boolean showCLIDebug) {
        showCLIDebugCheckBox.setSelected(showCLIDebug);
    }

    private void setSecretBodyVisible(boolean visible) {
        secreatPanel.setVisible(visible);
    }

    public String getNexusBaseUrl() {
        return textFieldNexusBaseUrl.getText();
    }

    public void setNexusBaseUrl(String sNexusBaseUrl) {
        this.textFieldNexusBaseUrl.setText(sNexusBaseUrl);
    }

    public String getGiteaBaseUrl() {
        return textFieldGiteaBaseUrl.getText();
    }

    public void setGiteaBaseUrl(String sGiteaBaseUrl) {
        this.textFieldGiteaBaseUrl.setText(sGiteaBaseUrl);
    }

    public String getGiteaTemplateOrg() {
        return textFieldGiteaTemplateOrg.getText();
    }

    public void setGiteaTemplateOrg(String sGiteaTemplateOrg) {
        this.textFieldGiteaTemplateOrg.setText(sGiteaTemplateOrg);
    }

    public String getNpmClient() {
        return textFieldNpmClient.getText();
    }

    public void setNpmClient(String sNpmClient) {
        this.textFieldNpmClient.setText(sNpmClient);
    }

    public String getCliVersion() {
        return textFieldCliVersion.getText();
    }

    public void setCliVersion(String cliVersion) {
        this.textFieldCliVersion.setText(cliVersion);
    }

    private void createUIComponents() {
        textFieldNpm = new TextFieldWithBrowseButton();
        registerBrowseDialog(textFieldNpm, "Select Npm Path");
        textFieldPod = new TextFieldWithBrowseButton();
        registerBrowseDialog(textFieldPod, "Select CocoaPods Path");
        if (!OSUtil.isMac()) {
            textFieldPod.setEnabled(false);
        }
        unapplyLocation = new TextFieldWithBrowseButton();
        unapplyLocation.addBrowseFolderListener("Select result folder", null, null,
                new FileChooserDescriptor(false, true, false, false, false, false) {
                    public boolean isFileSelectable(@Nullable VirtualFile file) {
                        return super.isFileSelectable(file) && file != null && file.isDirectory();
                    }
                });

        File tempFolder = new File(CommonUtil.getAppDataLocation(), "temp");
        unapplyLocation.setText(tempFolder.getAbsolutePath());
    }

    private void registerBrowseDialog(@NotNull TextFieldWithBrowseButton component, @NotNull String dialogTitle) {
        component.addBrowseFolderListener(dialogTitle, null, null,
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    public boolean isToolUpdateForce() {
        return checkBoxToolUpdate.isSelected();
    }

    public void setToolUpdateForce(boolean toolUpdateForce) {
        checkBoxToolUpdate.setSelected(toolUpdateForce);
    }
}
