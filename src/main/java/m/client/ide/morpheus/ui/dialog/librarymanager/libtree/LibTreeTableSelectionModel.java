package m.client.ide.morpheus.ui.dialog.librarymanager.libtree;

import m.client.ide.morpheus.core.component.checktreetable.CheckTreeTableSelectionModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LibTreeTableSelectionModel extends CheckTreeTableSelectionModel {

    public LibTreeTableSelectionModel() {
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
