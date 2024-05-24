package m.client.ide.morpheus.core.npm.action;

import javax.swing.*;
import java.awt.event.*;
import java.util.Map;

public class NodeCommandSelectedDialog extends JDialog {
    private final Map<String, String> nodeCommands;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList listNodeCommand;

    public NodeCommandSelectedDialog(Map<String, String> nodeCommands) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.nodeCommands = nodeCommands;
        initNodeCommands();

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

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        listNodeCommand.setSelectedIndex(-1);
        dispose();
    }

    public static void main(String[] args) {
        NodeCommandSelectedDialog dialog = new NodeCommandSelectedDialog(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void initNodeCommands() {
        // TODO: place custom component creation code here
        DefaultListModel<Map.Entry<String, String>> listModel = new DefaultListModel<>(){
            /**
             * Returns the element at the specified position in this list.
             *
             * @param index index of element to return
             * @return the element at the specified position in this list
             * @throws ArrayIndexOutOfBoundsException if the index is out of range
             *                                        ({@code index &lt; 0 || index &gt;= size()})
             */
            @Override
            public Map.Entry<String, String> get(int index) {
                return super.get(index);
            }
        };
        if(nodeCommands != null) {
            for (Map.Entry<String, String> nodeCommand : nodeCommands.entrySet()) {
                listModel.addElement(nodeCommand);
            }
        }

        listNodeCommand.setModel(listModel);
        listNodeCommand.setSelectedIndex(0);
    }

    public Map.Entry<String, String> getSelectedNodeCommand() {
        return (Map.Entry<String, String>) listNodeCommand.getSelectedValue();
    }
}
