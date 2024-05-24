package m.client.ide.morpheus.core.component.checktreetable;

import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableModelAdapter;
import com.intellij.ui.treeStructure.treetable.TreeTableTree;
import com.intellij.util.ui.UIUtil;
import m.client.ide.morpheus.core.component.action.CheckAction;
import m.client.ide.morpheus.core.component.action.UncheckAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.TreeUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCheckTreeTable extends TreeTable {

    private static final String CHECKTREETABLE_ACTIONGROUP_ID = "m.client.ide.morpheus.core.component.checktreetable.ActionGroup";
    private final DefaultTreeExpander treeExpander;
    private CheckTreeTableCellRenderer cellRenderer;
    protected EventListenerList treeitemListeners = new EventListenerList();
    private ActionPopupMenu popupMenu;
    private final ActionToolbar toolbar;

    public AbstractCheckTreeTable(AbstractCheckTreeTableModel treeTableModel) {
        super(treeTableModel);

        treeExpander = new DefaultTreeExpander(this.getTree());
        CommonActionsManager.getInstance().createExpandAllAction(treeExpander, this);
        CommonActionsManager.getInstance().createCollapseAllAction(treeExpander, this);

        createMouseListener();
        setComponentPopupMenu();

        toolbar = createActionToolBar();

        packColumns();
    }

    public void expandAll() {
        getTree().expandPath(getPathRow(0));
    }

    public void collapseAll() {
        getTree().collapsePath(getPathRow(0));
    }

    @Override
    protected void configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane();
        Container parent = getParent();
        if (parent instanceof JViewport) {
            JViewport port = (JViewport) parent;
            Container gp = port.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.

                if (toolbar != null) {
                    scrollPane.add(toolbar.getComponent(), 0);
                }
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null ||
                        SwingUtilities.getUnwrappedView(viewport) != this) {
                    return;
                }
            }
        }
    }

    protected void createMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int doubleclick = 2;
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == doubleclick) {
                    TreeTableTree tree = getTree();
                    TreeTable treeTable = tree.getTreeTable();
                    int selectedRow = treeTable.rowAtPoint(e.getPoint());
                    if (cellRenderer.getModel() instanceof AbstractCheckTreeTableModel) {
                        AbstractCheckTreeTableModel model = (AbstractCheckTreeTableModel) cellRenderer.getModel();

                        int checkIndex = model.getCheckColumnIndex();
                        if (checkIndex >= 0) {
                            boolean checked = (boolean) getValueAt(selectedRow, checkIndex);
                            setValueAt(!checked, selectedRow, checkIndex);
                        }
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
                    TreeTableTree tree = getTree();
                    TreeTable treeTable = tree.getTreeTable();
                    JPopupMenu popupMenu = tree.getComponentPopupMenu();
                    if (popupMenu != null) {
                        popupMenu.show(treeTable, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    protected void setComponentPopupMenu() {
        ActionManager manager = ActionManager.getInstance();
        @Nullable ActionGroup actionGroup = ActionUtil.getActionGroup(CHECKTREETABLE_ACTIONGROUP_ID);
        popupMenu = manager.createActionPopupMenu("Check Tree Table", actionGroup);
        getTree().setComponentPopupMenu(popupMenu.getComponent());
    }

    public TreePath getPathRow(int row) {
        TreeTableTree tree = getTree();
        if (tree != null) {
            TreeUI ui = tree.getUI();

            if (ui != null) {
                return ui.getPathForRow(tree, row);
            }
        }
        return null;
    }

    /**
     * @param treeTableModel
     */
    @Override
    public void setModel(TreeTableModel treeTableModel) {
        super.setModel(treeTableModel);

        cellRenderer.setSelectionModel(getTree().getSelectionModel());
        ; //For the tree
        // Renderer fuer den Tree.
        setDefaultRenderer(CheckTreeTableModel.class, cellRenderer);
        // Editor fuer die TreeTable
        setDefaultEditor(CheckTreeTableModel.class, new CheckTreeTableCellEditor(cellRenderer, this));

        // Kein Grid anzeigen.
        setShowGrid(true);

        // Keine Abstaende.
        setIntercellSpacing(new Dimension(3, 1));

        UIUtil.invokeLaterIfNeeded(() -> {
            setRootVisible(false);
            setShowColumns(true);
            setShowHorizontalLines(true);
            setShowVerticalLines(true);
        });
    }

    /**
     * @param treeTableModel
     * @return
     */
    @Override
    protected TreeTableModelAdapter adapt(TreeTableModel treeTableModel) {
        cellRenderer = new CheckTreeTableCellRenderer(this, treeTableModel);
        return new CheckTreeTableModelAdapter((AbstractCheckTreeTableModel) treeTableModel, cellRenderer, this);
    }

    public ActionToolbar createActionToolBar() {
        final ActionManager actionManager = ActionManager.getInstance();

        DefaultActionGroup actionGroup = new DefaultActionGroup("TOOLBAR_GROUP", false);
        AnAction checkAction = actionManager.getAction(CheckAction.ID);
        actionGroup.add(checkAction);
        AnAction openAction = actionManager.getAction(UncheckAction.ID);
        actionGroup.add(openAction);

//        public @NotNull ActionToolbar createActionToolbar(@NotNull String place, @NotNull ActionGroup group, boolean horizontal, boolean decorateButtons) {
//        return actionManager.createActionToolbar("TOOLBAR", actionGroup, true);
        ActionToolbar toolbar = new ActionToolbarImpl("TOOLBAR", actionGroup, true, false);
        toolbar.setTargetComponent(this);

        ActionManagerListener actionManagerListener = (ActionManagerListener) ApplicationManager.getApplication().getMessageBus().syncPublisher(ActionManagerListener.TOPIC);
        actionManagerListener.toolbarCreated("TOOLBAR", actionGroup, true, toolbar);
        return toolbar;
    }

    protected void packColumns() {
        UIUtil.invokeLaterIfNeeded(() -> {
            JTableHeader header = getTableHeader();
            if (header instanceof CheckTableHeader) {
                for (int index = 0; index < getColumnCount(); index++) {
                    ((CheckTableHeader) header).packColumn(index);
                }
            }
        });
    }

    protected void packColumns(int index) {
        UIUtil.invokeLaterIfNeeded(() -> {
            JTableHeader header = getTableHeader();
            if (header instanceof CheckTableHeader) {
                ((CheckTableHeader) header).packColumn(index);
            }
        });
    }

    @Override
    public void setRootVisible(boolean visible) {
        this.cellRenderer.setRootVisible(visible);
        super.setRootVisible(visible);
    }

    protected AbstractCheckTreeTableModel getCheckTreeTableModel() {
        return (AbstractCheckTreeTableModel) getTableModel();
    }

    protected CheckTreeTableCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    @Override
    protected @NotNull JTableHeader createDefaultTableHeader() {
        return new CheckTableHeader();
    }

    public AbstractCheckTreeTableNode getSelectedNode() {
        TreeTableTree tree = getTree();
        int row = tree.getTreeTable().getSelectedRow();
        TableModel model = getModel();
        if (model instanceof CheckTreeTableModelAdapter) {
            CheckTreeTableModelAdapter adapter = (CheckTreeTableModelAdapter) model;
            Object node = adapter.nodeForRow(convertRowIndexToModel(row));
            return node instanceof AbstractCheckTreeTableNode ? (AbstractCheckTreeTableNode) node : null;
        }

        return null;
    }

    public AbstractCheckTreeTableNode getNodeAtRow(int row) {
        TreeTableTree tree = getTree();
        TableModel model = getModel();
        if (model instanceof CheckTreeTableModelAdapter) {
            CheckTreeTableModelAdapter adapter = (CheckTreeTableModelAdapter) model;
            Object node = adapter.nodeForRow(convertRowIndexToModel(row));
            return node instanceof AbstractCheckTreeTableNode ? (AbstractCheckTreeTableNode) node : null;
        }

        return null;
    }

    public abstract void setInput(String name, Object input);

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Object oldValue = getValueAt(row, column);
        super.setValueAt(aValue, row, column);

        if (oldValue != aValue) {
            Object node = getCellRenderer().getPathForRow(row).getLastPathComponent();
            if (node instanceof AbstractCheckTreeTableNode) {
                fireTreeItemValueChanged(new TreeItemValueChangeEvent((AbstractCheckTreeTableNode) node, oldValue, aValue,
                        convertColumnIndexToModel(column)));
            }
        }
    }

    public void addTreeItemValueChangeListener(TreeItemValueChangeListenre listener) {
        treeitemListeners.add(TreeItemValueChangeListenre.class, listener);
    }

    public void removeTreeItemValueChangeListener(TreeItemValueChangeListenre listener) {
        treeitemListeners.remove(TreeItemValueChangeListenre.class, listener);
    }

    protected void fireTreeItemValueChanged(TreeItemValueChangeEvent event) {
        Object[] listeners = treeitemListeners.getListeners(TreeItemValueChangeListenre.class);
        for (int i = 0; i < listeners.length; i++) {
            ((TreeItemValueChangeListenre) listeners[i]).treeItemValueChanged(event);
        }
        UIUtil.invokeLaterIfNeeded(() -> {
            this.updateUI();
        });
    }

    public void addTreeItemSelectionChangeListener(TreeItemSelectionChangeListenre listener) {
        treeitemListeners.add(TreeItemSelectionChangeListenre.class, listener);
    }

    public void removeTreeItemSelectionChangeListener(TreeItemSelectionChangeListenre listener) {
        treeitemListeners.remove(TreeItemSelectionChangeListenre.class, listener);
    }

    protected void fireTreeItemSelectionChanged(TreeItemSelectionChangeEvent event) {
        Object[] listeners = treeitemListeners.getListeners(TreeItemSelectionChangeListenre.class);
        for (int i = 0; i < listeners.length; i++) {
            ((TreeItemSelectionChangeListenre) listeners[i]).treeItemSelectionChanged(event);
        }
    }

    public List<?> getAllItems() {
        Object node = getCheckTreeTableModel().getRoot();
        List result = new ArrayList<AbstractCheckTreeTableNode>();
        if(node instanceof AbstractCheckTreeTableNode) {
            Object[] children = ((AbstractCheckTreeTableNode) node).getChildren();
            for (int i = 0; i < children.length; ++i) {
                Object child = children[i];
                if(child instanceof AbstractCheckTreeTableNode) {
                    getAllItems((AbstractCheckTreeTableNode) child, result);
                }
            }
        }
        return result;
    }

    private void getAllItems(AbstractCheckTreeTableNode node, List result) {
        result.add(node);
        Object[] children = node.getChildren();
        if (children.length > 0) {
            for (Object child : children) {
                if(child instanceof AbstractCheckTreeTableNode) {
                    getAllItems((AbstractCheckTreeTableNode) child, result);
                }
            }
        }
    }

    public Object[] getAllWhiteCheckedItems() {
        return getAllWhiteCheckedItems(getCheckTreeTableModel().getRoot());
    }

    protected Object @NotNull [] getAllWhiteCheckedItems(Object node) {
        ArrayList<Object> items = new ArrayList<Object>();
        if (node instanceof AbstractCheckTreeTableNode) {
            AbstractCheckTreeTableNode treeNode = (AbstractCheckTreeTableNode) node;
            if (!treeNode.isChecked()) {
                Object[] children = ((AbstractCheckTreeTableNode) node).getChildren();
                if (children != null) {
                    for (Object child : children) {
                        if (child instanceof AbstractCheckTreeTableNode) {
                            items.addAll(List.of(getAllWhiteCheckedItems(child)));
                        }
                    }
                }
            } else {
                items.add(node);
            }
        }
        return items.toArray();
    }

    /**
     * Invoked when editing is finished. The changes are saved and the
     * editor is discarded.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param e the event received
     * @see CellEditorListener
     */
    @Override
    public void editingStopped(ChangeEvent e) {
        // Take in the new value
        TableCellEditor editor = getCellEditor();
        if (editor != null && editingColumn == getCheckTreeTableModel().getCheckColumnIndex()) {
            AbstractCheckTreeTableNode node = getNodeAtRow(editingRow);
            setValueAt(!node.isChecked(), editingRow, editingColumn);
            removeEditor();
        }
    }

    public abstract @NotNull Object[] getFilteredNodes();

    protected class CheckTableHeader extends JBTableHeader {
        @Override
        protected void packColumn(int columnToPack) {
            super.packColumn(columnToPack);
        }
    }

}
