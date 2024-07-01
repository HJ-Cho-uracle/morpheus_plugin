package m.client.ide.morpheus.launch.configeditor;

import com.intellij.ui.table.JBTable;
import m.client.ide.morpheus.core.utils.CommonUtil;
import com.esotericsoftware.minlog.Log;

public class IOSDeviceTable extends JBTable {
    public IOSDeviceTable(DeviceTableModel deviceTableModel) {
        super(deviceTableModel);
    }

    public void setSelectionRow(int i) {
        CommonUtil.log(Log.LEVEL_DEBUG, "IOSDeviceTable.setSelectionRow(" + i + ")");
        changeSelection(i, 0, false, false);
    }

    @Override
    public int getSelectedRow() {
        return super.getSelectedRow();
    }
}
