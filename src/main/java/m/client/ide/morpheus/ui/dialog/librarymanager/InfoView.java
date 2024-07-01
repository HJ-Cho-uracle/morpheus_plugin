package m.client.ide.morpheus.ui.dialog.librarymanager;

import m.client.ide.morpheus.framework.cli.jsonParam.LibraryParam;

import javax.swing.*;

public class InfoView {
    private LibraryManagerView libView;

    private JPanel infoPanel;
    private JTextField textFieldName;
    private JTextArea textAreaDescription;
    private JButton detailButton;
    private JTextField textFieldRevision;
    private JTextField textFieldStatus;
    private JTextArea textAreaHistory;
    private LibraryParam libraryParam;

    public void setLibmanagerView(LibraryManagerView libraryManagerView) {
        this.libView = libraryManagerView;
    }

    public void setLibraryParam(LibraryParam libraryParam, boolean force) {
        if (force == false && this.libraryParam == libraryParam) {
            return;
        }

        this.libraryParam = libraryParam;
        textFieldName.setText(libraryParam == null ? "" : libraryParam.getName());
        textFieldRevision.setText(libraryParam == null ? "" : libraryParam.getNpm().getVersion());
        textFieldStatus.setText(libraryParam == null ? "" : libraryParam.getStatus());
        textAreaDescription.setText(libraryParam == null ? "" : libraryParam.getPath());
    }
}
