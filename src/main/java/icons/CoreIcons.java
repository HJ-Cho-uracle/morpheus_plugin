package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface CoreIcons {

    // new ImageIcon(getClass().getResource("/icons/mdeploy.png")
    Icon icon16 = IconLoader.getIcon("icons/core/icon_16_morpheus.png", CoreIcons.class);
    Icon CollapseAllIcon = IconLoader.getIcon("icons/core/collapseall.gif", CoreIcons.class);
    Icon ExpandAllSettingIcon = IconLoader.getIcon("icons/core/expandall.gif", CoreIcons.class);
    Icon ExpandInstIcon = IconLoader.getIcon("icons/core/expandinst.png", CoreIcons.class);
    Icon SelectAllIcon = IconLoader.getIcon("icons/core/selectall.png", CoreIcons.class);
    Icon SelectUDTIcon = IconLoader.getIcon("icons/core/selectudt.png", CoreIcons.class);
    Icon UnselectAllIcon = IconLoader.getIcon("icons/core/unselectall.png", CoreIcons.class);
    Icon runIOS = IconLoader.getIcon("icons/core/xcodeproject16.png", CoreIcons.class);
}
