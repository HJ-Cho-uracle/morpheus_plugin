package m.client.ide.morpheus.core.component.checktreetable;

import m.client.ide.morpheus.core.utils.CommonUtil;
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;
import java.awt.*;

public class CheckTreeTableCellRenderer extends JTree implements TableCellRenderer {
    /** Die letzte Zeile, die gerendert wurde. */
    protected int visibleRow;

    private AbstractCheckTreeTable treeTable;

    public CheckTreeTableCellRenderer(AbstractCheckTreeTable treeTable, TreeModel model) {
        super(model);
        this.treeTable = treeTable;

        // Setzen der Zeilenhoehe fuer die JTable
        // Muss explizit aufgerufen werden, weil treeTable noch
        // null ist, wenn super(model) setRowHeight aufruft!
        setRowHeight(getRowHeight());
    }

    /**
     * Tree und Table muessen die gleiche Hoehe haben.
     */
    public void setRowHeight(int rowHeight) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if (treeTable != null && treeTable.getRowHeight() != rowHeight) {
                treeTable.setRowHeight(getRowHeight());
            }
        }
    }

    /**
     * Tree muss die gleiche Hoehe haben wie Table.
     */
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, 0, w, treeTable.getHeight());
    }

    /**
     * Sorgt fuer die Einrueckung der Ordner.
     */
    public void paint(Graphics g) {
        g.translate(0, -visibleRow * getRowHeight());

        super.paint(g);
    }

    /**
     * Liefert den Renderer mit der passenden Hintergrundfarbe zurueck.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
//            CommonUtil.log(Log.LEVEL_DEBUG, value + "] Row:" + row + ", Col:" + column + "] HasFocus = " + hasFocus);
        } else
            setBackground(table.getBackground());

        visibleRow = row;
        return this;
    }
}
