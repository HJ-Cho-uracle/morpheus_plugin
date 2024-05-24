package m.client.ide.morpheus.launch.configeditor;

import m.client.ide.morpheus.launch.common.LaunchUtil;
import m.client.ide.morpheus.ui.message.UIMessages;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.HashMap;

public class DeviceTableModel extends AbstractTableModel implements Serializable {
    private static final String[] columnNames = { UIMessages.get(UIMessages.Device_Column0), UIMessages.get(UIMessages.Device_Column1) };

    public enum DeviceColumn {
        DEVICETYPE(0), SERIALNUMBER(1);

        private final int value;

        DeviceColumn(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            switch (this) {
                case DEVICETYPE:
                    return columnNames[0];
                case SERIALNUMBER:
                    return columnNames[1];

                default:
                    return super.toString().toLowerCase();
            }
        }

        public String getName() {
            return toString();
        }

        public static DeviceColumn valueOf(int value) {
            switch (value) {
                case 1:
                    return SERIALNUMBER;

                default:
                    return DEVICETYPE;
            }
        }

        public static DeviceColumn fromString(@NotNull String type) {
            String typeLower = type.toLowerCase();
            if (typeLower.equals(columnNames[SERIALNUMBER.value].toLowerCase()))
                return SERIALNUMBER;
            else
                return DEVICETYPE;
        }
    }

//
// Instance Variables
//

    /**
     * The <code>List</code> of <code>LicenseParam</code> values.
     */
    @SuppressWarnings("rawtypes")
    protected HashMap<String, LaunchUtil.IOSDeviceInfo> deviceList;

//
// Constructors
//

    /**
     *  Default Constructs a <code>LicenseTableModel</code> with empty <code>ArrayList</code>.
     */
    public DeviceTableModel() {
        this(new HashMap<String, LaunchUtil.IOSDeviceInfo>());
    }

    /**
     *  Constructs a <code>LicenseTableModel</code> and initializes the table
     *  by <code>List</code> of <code>LicenseParam</code>.
     *
     * @param deviceList     the data of the table, a <code>List</code> of <code>LicenseParam</code>
     *                          values
     * @see #setDeviceList
     */
    @SuppressWarnings("rawtypes")
    public DeviceTableModel(HashMap<String, LaunchUtil.IOSDeviceInfo> deviceList) {
        setDeviceList(deviceList);
    }

    /**
     *  Returns the <code>List</code> of <code>LicenseParam</code>
     *  that contains the table's
     *  data values.  <p>
     *
     * @return  the list containing the tables data values
     *
     * @see #setDeviceList
     */
    @SuppressWarnings("rawtypes")
    public HashMap<String, LaunchUtil.IOSDeviceInfo> getDeviceList() {
        return deviceList;
    }

    /**
     *  Replaces the current <code>licenseParams</code> instance variable
     *  with the new <code>List</code> of rows, <code>licenseParams</code>.
     *  Each row is represented in <code>licenseParams</code> as a
     *  <code>List</code> of <code>LicenseParam</code> values.
     *
     * @param   deviceList         the new data vector
     * @see #getDeviceList()
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setDeviceList(HashMap<String, LaunchUtil.IOSDeviceInfo> deviceList) {
        this.deviceList = deviceList;

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
     * @param   serialNumber           data of the row being added
     * @param   iosDeviceInfo          data of the row being added
     */
    public void addDevice(String serialNumber, LaunchUtil.IOSDeviceInfo iosDeviceInfo) {
        deviceList.put(serialNumber, iosDeviceInfo);

        fireTableRowsInserted(deviceList.size(), deviceList.size());
    }

    /**
     *  Removes the row at <code>row</code> from the model.  Notification
     *  of the row being removed will be sent to all the listeners.
     *
     * @param   row      the row index of the row to be removed
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid
     */
    public void removeLicense(int row) {
        Object name = getValueAt(row, 0);
        deviceList.remove(name);

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
        return deviceList.size();
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
        if(row < 0 || row >= deviceList.size() || column < 0 || column >= columnNames.length) {
            return "";
        }

        String serial = getSerial(row);
        LaunchUtil.IOSDeviceInfo iosDeviceInfo = deviceList.get(serial);
        switch(column) {
            case 0:
                return iosDeviceInfo.getDeviceType().toString();
            case 1:
                return serial;
        }
        return "";
    }

    private String getSerial(int row) {
        if(row < 0 || row >= deviceList.size()) {
            return "";
        }
        Object[] serials = deviceList.keySet().toArray();
        return (String) serials[row];
    }
} // End of class DefaultTableModel
