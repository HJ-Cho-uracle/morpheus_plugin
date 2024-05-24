package m.client.ide.morpheus.ui.dialog.licensemanager;

import m.client.ide.morpheus.framework.cli.jsonParam.LicenseParam;
import m.client.ide.morpheus.ui.dialog.librarymanager.libtree.LibTreeTableModel;
import m.client.ide.morpheus.ui.message.UIMessages;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LicenseTableModel extends AbstractTableModel implements Serializable {
    private static final String[] columnNames = { UIMessages.get(UIMessages.LicView_Column0), UIMessages.get(UIMessages.LicView_Column1) };

    public enum LicenseColumn {
        APPID(0), EXPDATE(1);

        private final int value;

        LicenseColumn(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            switch (this) {
                case APPID:
                    return columnNames[0];
                case EXPDATE:
                    return columnNames[1];

                default:
                    return super.toString().toLowerCase();
            }
        }

        public String getName() {
            return toString();
        }

        public static LicenseColumn valueOf(int value) {
            switch (value) {
                case 1:
                    return EXPDATE;

                default:
                    return APPID;
            }
        }

        public static LicenseColumn fromString(String type) {
            String typeLower = type.toLowerCase();
            if (typeLower.equals(columnNames[1].toLowerCase()))
                return EXPDATE;
            else
                return APPID;
        }
    }

//
// Instance Variables
//

    /**
     * The <code>List</code> of <code>LicenseParam</code> values.
     */
    @SuppressWarnings("rawtypes")
    protected List<LicenseParam> licenseParams;

//
// Constructors
//

    /**
     *  Default Constructs a <code>LicenseTableModel</code> with empty <code>ArrayList</code>.
     */
    public LicenseTableModel() {
        this(new ArrayList<>());
    }

    /**
     *  Constructs a <code>LicenseTableModel</code> and initializes the table
     *  by <code>List</code> of <code>LicenseParam</code>.
     *
     * @param licenseParams     the data of the table, a <code>List</code> of <code>LicenseParam</code>
     *                          values
     * @see #getLicenseParams
     * @see #setLicenseParams
     */
    @SuppressWarnings("rawtypes")
    public LicenseTableModel(List<LicenseParam> licenseParams) {
        setLicenseParams(licenseParams);
    }

    /**
     *  Returns the <code>List</code> of <code>LicenseParam</code>
     *  that contains the table's
     *  data values.  <p>
     *
     * @return  the list containing the tables data values
     *
     * @see #setLicenseParams
     */
    @SuppressWarnings("rawtypes")
    public List<LicenseParam> getLicenseParams() {
        return licenseParams;
    }

    /**
     *  Replaces the current <code>licenseParams</code> instance variable
     *  with the new <code>List</code> of rows, <code>licenseParams</code>.
     *  Each row is represented in <code>licenseParams</code> as a
     *  <code>List</code> of <code>LicenseParam</code> values.
     *
     * @param   licenseParams         the new data vector
     * @see #getLicenseParams
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setLicenseParams(List<LicenseParam> licenseParams) {
        this.licenseParams = licenseParams;

        fireTableStructureChanged();
    }

    /**
     *  Equivalent to <code>fireTableChanged</code>.
     *
     *  @param event the change event
     *
     */
    public void rowsRemoved(TableModelEvent event) {
        fireTableChanged(event);
    }

    /**
     *  Adds a row to the end of the model.
     *
     * @param   licenseParam          data of the row being added
     */
    public void addLicense(LicenseParam licenseParam) {
        licenseParams.add(licenseParam);

        fireTableRowsInserted(licenseParams.size(), licenseParams.size());
    }

    /**
     *  Removes the row at <code>row</code> from the model.  Notification
     *  of the row being removed will be sent to all the listeners.
     *
     * @param   row      the row index of the row to be removed
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid
     */
    public void removeLicense(int row) {
        licenseParams.remove(row);

        fireTableRowsDeleted(row, row);
    }

//
// Implementing the TableModel interface
//

    /**
     * Returns the number of rows in this data table.
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return licenseParams.size();
    }

    /**
     * Returns the number of columns in this data table.
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the column name.
     *
     * @return a name for this column using the string value of the
     * appropriate member in <code>columnIdentifiers</code>.
     * If <code>columnIdentifiers</code> does not have an entry
     * for this index, returns the default
     * name provided by the superclass.
     */
    public String getColumnName(int column) {
        String id = null;
        // This test is to cover the case when
        // getColumnCount has been subclassed by mistake ...
        if (column < columnNames.length && (column >= 0)) {
            id = columnNames[column];
        }
        return (id == null) ? "" : id;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return super.getColumnClass(columnIndex);
    }

    /**
     * Returns true regardless of parameter values.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  true
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Returns an attribute value for the cell at <code>row</code>
     * and <code>column</code>.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  the value Object at the specified cell
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
     */
    public Object getValueAt(int row, int column) {
        if(row < 0 || row >= licenseParams.size() || column < 0 || column >= columnNames.length) {
            return "";
        }

        LicenseParam licenseParam = licenseParams.get(row);
        switch(column) {
            case 0:
                return licenseParam.getAppId();
            case 1:
                return licenseParam.getExpirationDate();
        }
        return "";
    }
} // End of class DefaultTableModel
