package m.client.ide.morpheus.launch.configeditor;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBScrollPane;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.core.utils.PreferenceUtil;
import m.client.ide.morpheus.launch.IOSLaunchTargetType;
import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.launch.configeditor.DeviceTableModel.DeviceColumn;
import m.client.ide.morpheus.launch.configuration.IOSRunConfigField;
import m.client.ide.morpheus.launch.configuration.IOSRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class IOSConfigurationEditorForm extends SettingsEditor<IOSRunConfiguration> {

    private TextFieldWithBrowseButton textfieldProjectPath;
    private JTextField textfieldAppName;
    private JRadioButton runSimulatorRadioButton;
    private JComboBox comboSimType;
    private JCheckBox runWithRetinaDisplayCheckBox;
    private JRadioButton runDeviceRadioButton;
    private JTable tableIOSDevice;
    private JButton refreshListButton;
    private JPanel configEditor;
    private JScrollPane tableScrollPane;
    private JComboBox comboDevelopCert;
    private JTextField textFieldTeamId;

    private IOSRunConfigField iosRunConfigField;

    private final Project project;
    private ArrayList<String> iosDeveloperList;
    private IOSLaunchTargetType iosLaunchType;
    private HashMap<String, LaunchUtil.IOSDeviceInfo> devices;
    private boolean retina;
    private String deviceSerialNumber;
    private String deviceVersion;
    private HashMap<String, LaunchUtil.SimulatorInfo> simulators = new HashMap<String, LaunchUtil.SimulatorInfo>();
    private String simDisplayName;

    ChangeListener runModeChangeListener = new ChangeListener() {
        /**
         * Invoked when the target of the listener has changed its state.
         *
         * @param e a ChangeEvent object
         */
        @Override
        public void stateChanged(ChangeEvent e) {
            setDeviceInfoEnabled();
        }
    };
    private String iosTargetType;

    public IOSConfigurationEditorForm(Project project) {
        this.project = project;

        runSimulatorRadioButton.addChangeListener(runModeChangeListener);
        runDeviceRadioButton.addChangeListener(runModeChangeListener);

        refreshListButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });

        comboSimType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    simDisplayName = (String) comboSimType.getItemAt(comboSimType.getSelectedIndex());
                }
            }
        });

        comboDevelopCert.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String developerName = (String) comboDevelopCert.getItemAt(comboDevelopCert.getSelectedIndex());
                    AtomicBoolean isTest = new AtomicBoolean(false);
                    textFieldTeamId.setText(LaunchUtil.getIOSDevelopmentTeam(developerName, isTest));
                }
            }
        });

        initDevelopCertificateInfo();

        initSimulatorInfo();
        initDeviceTable();
    }

    private void updateSelectedCombobox(JComboBox comboBox, String selected) {
        if (comboBox == null) {
            return;
        }

        if (selected == null || selected.isEmpty()) {
            comboBox.setSelectedItem(0);
        } else {
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                Object item = comboBox.getItemAt(i);
                if (selected.equals(item)) {
                    comboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void initSimulatorInfo() {
        comboSimType.removeAll();
        simulators.clear();

        ArrayList<LaunchUtil.SimulatorInfo> list = LaunchUtil.getIOSSimulators();
        if (list != null) {
            for (LaunchUtil.SimulatorInfo info : list) {
                comboSimType.addItem(info.getDisplayName());
                simulators.put(info.getDisplayName(), info);
            }
        }

        updateSelectedCombobox(comboSimType, "");
    }

    private void initDevelopCertificateInfo() {
        comboDevelopCert.removeAll();
        try {
            iosDeveloperList = LaunchUtil.getIOSDeveloperList();
        } catch (Exception e) {
            CommonUtil.log(Log.LEVEL_ERROR, e.getLocalizedMessage(), e);
            return;
        }

        comboDevelopCert.addItem("");
        if (iosDeveloperList != null && iosDeveloperList.size() > 0) {
            for (String iosDeveloper : iosDeveloperList) {
                comboDevelopCert.addItem(iosDeveloper);
            }
        }
    }

    private void initDeviceTable() {
        devices = LaunchUtil.getIOSDevices(project);
        tableIOSDevice.setModel(new DeviceTableModel(devices));
    }

    /**
     * @param iosRunConfigState
     */
    @Override
    protected void resetEditorFrom(@NotNull IOSRunConfiguration iosRunConfigState) {
        textfieldProjectPath.setText(iosRunConfigState.getIosProjectPath());
        textfieldAppName.setText(iosRunConfigState.getApplicationName());

        iosRunConfigField = iosRunConfigState.getRunConfigField();
        iosLaunchType = iosRunConfigField.getIosLaunchType();
        if (iosLaunchType == null) {
            iosLaunchType = IOSLaunchTargetType.SIMULATOR;
        }

        simDisplayName = iosRunConfigField.getSimDisplayName();
        if (simDisplayName == null || simDisplayName.isEmpty()) {
            simDisplayName = (String) comboSimType.getSelectedItem();
        }

        retina = iosRunConfigField.isRetina();

        deviceSerialNumber = iosRunConfigField.getIosDeviceSerialNumber(); //$NON-NLS-1$
        deviceVersion = iosRunConfigField.getIosDeviceVersion();

        updateSelectedCombobox(comboDevelopCert, iosRunConfigField.getIosCertificateName());
        textFieldTeamId.setText(iosRunConfigField.getIosDevelopmentTeam());

        updateDeviceInfo();
    }

    private void refresh() {
        initSimulatorInfo();
        initDeviceTable();

        updateDeviceInfo();
    }

    private void updateDeviceInfo() {
        runSimulatorRadioButton.setSelected(false);
        runDeviceRadioButton.setSelected(false);

        if (iosLaunchType != null) {
            if (iosLaunchType.equals(IOSLaunchTargetType.DEVICE)) {
                runDeviceRadioButton.setSelected(true);
            } else if (iosLaunchType.equals(IOSLaunchTargetType.SIMULATOR)) {
                runSimulatorRadioButton.setSelected(true);
            }
        }

        for (int i = 0; i < tableIOSDevice.getRowCount(); i++) {
            Object serial = tableIOSDevice.getValueAt(i, DeviceColumn.SERIALNUMBER.getValue());
            if (serial.equals(deviceSerialNumber)) {
                tableIOSDevice.getSelectionModel().setLeadSelectionIndex(i);
                break;
            }
        }
        updateSelectedCombobox(comboSimType, simDisplayName);
        if (simDisplayName == null || simDisplayName.isEmpty()) {
            simDisplayName = (String) comboSimType.getItemAt(comboSimType.getSelectedIndex());
        }
        runWithRetinaDisplayCheckBox.setSelected(retina);

        setDeviceInfoEnabled();
    }

    private void setDeviceInfoEnabled() {
        if (runSimulatorRadioButton.isSelected()) {
            iosLaunchType = IOSLaunchTargetType.SIMULATOR;
            iosTargetType = IOSRunConfigField.DEVICE_TYPE_IPHONE_SIMULATOR;

            comboSimType.setEnabled(true);
            runWithRetinaDisplayCheckBox.setEnabled(true);
        } else {
            comboSimType.setEnabled(false);
            runWithRetinaDisplayCheckBox.setEnabled(false);
        }
        if (runDeviceRadioButton.isSelected()) {
            iosLaunchType = IOSLaunchTargetType.DEVICE;
            iosTargetType = IOSRunConfigField.DEVICE_TYPE_IPHONE;

            comboDevelopCert.setEnabled(true);
            tableIOSDevice.setEnabled(true);
            textFieldTeamId.setEnabled(true);
        } else {
            comboDevelopCert.setEnabled(false);
            tableIOSDevice.setEnabled(false);
            textFieldTeamId.setEnabled(false);
        }
    }


    /**
     * @param iosRunConfigState
     * @throws ConfigurationException
     */
    @Override
    protected void applyEditorTo(@NotNull IOSRunConfiguration iosRunConfigState) throws ConfigurationException {
        IOSRunConfigField configField = iosRunConfigState.getRunConfigField();
        if (configField == null) {
            configField = new IOSRunConfigField(iosRunConfigState.getProject());
        }

        IOSLaunchTargetType iosLaunchType = runDeviceRadioButton.isSelected() ? IOSLaunchTargetType.DEVICE : IOSLaunchTargetType.SIMULATOR;
        configField.setIosLaunchType(iosLaunchType);

        /** Simulator 정보 저장 */
        LaunchUtil.SimulatorInfo simulatorInfo = simulators.get(simDisplayName);
        if (simulatorInfo != null) {
            String id = simulatorInfo.getDeviceTypeId().trim();
            String deviceTypeId = id.substring(id.lastIndexOf('.') + 1) + ", " + simulatorInfo.getOs();

            configField.setIosTargetSdkVersion(simulatorInfo.getOs());
            configField.setSimDisplayName(simDisplayName);

            configField.setIosDeviceTypeId(deviceTypeId);
            configField.setIosTargetType(iosTargetType);
            configField.setIosDestination(simulatorInfo.toString());

            configField.setSimulatorUUid(simulatorInfo.getUuid());
        } else {
            configField.setIosTargetSdkVersion("");
            configField.setSimDisplayName(simDisplayName);

            configField.setIosDeviceTypeId("");
            configField.setIosTargetType(iosTargetType);
            configField.setIosDestination("");

            configField.setSimulatorUUid("");
        }

        /** Device 정보 저장 */
        String certificateName = (String) comboDevelopCert.getSelectedItem();
        configField.setIosCertificateName(certificateName);
        PreferenceUtil.setIOSDeveloperCertificate(certificateName);

        configField.setIosDeviceSerialNumber(deviceSerialNumber);
        configField.setIosDeviceVersion(deviceVersion);

        iosRunConfigState.setRunConfigField(configField);
    }

    /**
     * @return
     */
    @Override
    protected @NotNull JComponent createEditor() {
        return configEditor;
    }

    private void createUIComponents() {
        tableIOSDevice = new IOSDeviceTable(null);
        tableScrollPane = new JBScrollPane(tableIOSDevice);
        tableScrollPane.setPreferredSize(new Dimension(-1, 100));
        tableScrollPane.setRowHeaderView(tableIOSDevice.getTableHeader());

        tableIOSDevice.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object source = e.getSource();
                if (source instanceof DefaultListSelectionModel) {
                    DefaultListSelectionModel model = (DefaultListSelectionModel) source;
                    String serial = (String) tableIOSDevice.getValueAt(model.getMaxSelectionIndex(), DeviceColumn.SERIALNUMBER.getValue());
                    LaunchUtil.IOSDeviceInfo deviceInfo = devices.get(serial);
                    if (deviceInfo != null) {
                        deviceSerialNumber = deviceInfo.getSerial();
                        deviceVersion = deviceInfo.getVersion();
                    }
                }
            }
        });
    }
}
