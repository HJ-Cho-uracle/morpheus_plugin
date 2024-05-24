package m.client.ide.morpheus.framework.template;

import com.intellij.ui.table.JBTable;
import m.client.ide.morpheus.core.utils.CommonUtil;
import m.client.ide.morpheus.ui.dialog.licensemanager.LicenseTableModel;
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log;

public class LicenseTable extends JBTable {
    public LicenseTable(LicenseTableModel licenseTableModel) {
        super(licenseTableModel);
    }

    public void setSelectionRow(int i) {
        CommonUtil.log(Log.LEVEL_DEBUG, "LicenseTable.setSelectionRow(" + i + ")");
        changeSelection(i, 0, false, false);
    }

    @Override
    public int getSelectedRow() {
        return super.getSelectedRow();
    }
}
