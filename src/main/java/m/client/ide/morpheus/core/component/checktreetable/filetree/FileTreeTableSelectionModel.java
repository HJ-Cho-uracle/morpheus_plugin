package m.client.ide.morpheus.core.component.checktreetable.filetree;

import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableSelectionModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FileTreeTableSelectionModel extends CheckTreeTableSelectionModel {

    public FileTreeTableSelectionModel() {
        super();

        getListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            }
        });
    }

    @Override
    public ListSelectionModel getListSelectionModel() {
        return super.getListSelectionModel();
    }
}
