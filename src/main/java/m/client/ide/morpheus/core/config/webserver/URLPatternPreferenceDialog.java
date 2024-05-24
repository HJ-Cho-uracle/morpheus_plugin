package m.client.ide.morpheus.core.config.webserver;

import javax.swing.*;
import java.awt.event.*;

public class URLPatternPreferenceDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameText;
    private JTextField classNameText;
    private JTextField patternText;
    private JCheckBox singletonBtn;
    private MappingInfo sourceInfo;
    private MappingInfo currentInfo;

    public URLPatternPreferenceDialog() {
        setTitle("URLPattern Configuration");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void setData(MappingInfo mapInfo) {
        sourceInfo = mapInfo;
        nameText.setText(mapInfo.getName());
        classNameText.setText(mapInfo.getClassName());
        patternText.setText(mapInfo.getPatternStr());
        singletonBtn.setSelected(mapInfo.isSingleton());
    }

    public MappingInfo getMappingInfo() {
        return currentInfo;
    }

    public MappingInfo getSourceInfo() {
        return sourceInfo;
    }

    private void onOK() {
        currentInfo = new MappingInfo(nameText.getText(), patternText.getText(), classNameText.getText(), singletonBtn.isSelected());
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        URLPatternPreferenceDialog dialog = new URLPatternPreferenceDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
