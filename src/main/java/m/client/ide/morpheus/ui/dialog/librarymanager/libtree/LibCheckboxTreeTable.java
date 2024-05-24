package m.client.ide.morpheus.ui.dialog.librarymanager.libtree;

import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTable;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableModel;
import m.client.ide.morpheus.core.component.checktreetable.AbstractCheckTreeTableNode;
import m.client.ide.morpheus.core.component.checktreetable.TreeItemSelectionChangeEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class LibCheckboxTreeTable extends AbstractCheckTreeTable {

    public LibCheckboxTreeTable(AbstractCheckTreeTableModel treeTableModel) {
        super(treeTableModel);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
                    Point p = e.getPoint();
                    int pressedRow = rowAtPoint(p);
                    int column = columnAtPoint(p);
                    AbstractCheckTreeTableNode selection = getNodeAtRow(pressedRow);
                    if(selection instanceof LibTreeTableNode) {
                        fireTreeItemSelectionChanged(new TreeItemSelectionChangeEvent(selection, column));
                    }
                }
            }
        });
    }

    @Override
    public void setInput(@NotNull String name, Object input) {
        LibTreeTableNode node = new LibTreeTableNode(name, input);
        LibTreeTableModel root = new LibTreeTableModel(node);
        setModel(root);

        packColumns();
        packColumns(0);
    }

    /**
     * @param treeTableModel
     */
    @Override
    public void setModel(TreeTableModel treeTableModel) {
        super.setModel(treeTableModel);

        StatusCellRenderer statusCellRenderer = new StatusCellRenderer();
        setDefaultRenderer(Status.class, statusCellRenderer);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
    }

    @Override
    public Object[] getFilteredNodes() {
        return new Object[0];
    }

    public Object[] getAllWhiteCheckedItems() {
        return getAllWhiteCheckedItems(getCheckTreeTableModel().getRoot());
    }

    protected Object[] getAllWhiteCheckedItems(Object object) {
        ArrayList<Object> items = new ArrayList<Object>();
        if(object instanceof LibTreeTableNode) {
            LibTreeTableNode node = (LibTreeTableNode) object;
            if(node.isChecked() && node.isLeaf()) {
                items.add(object);
            }

            Object[] children = node.getChildren();
            if(children != null) {
                for (Object child : children) {
                    if (child instanceof LibTreeTableNode) {
                        items.addAll(List.of(getAllWhiteCheckedItems(child)));
                    }
                }
            }
        }
        return items.toArray();
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if(column == LibTreeTableModel.LibColumn.STATUS.getValue()) {
            TableCellRenderer renderer = getDefaultRenderer(Status.class);
            return renderer;
        }
        return super.getCellRenderer(row, column);
    }
}

