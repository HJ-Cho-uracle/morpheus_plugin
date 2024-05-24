package m.client.ide.morpheus.ui.message

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "UIResourceBundle"

object UIMessages : DynamicBundle(BUNDLE) {


    const val LicenseManager_Title = "LicenseManager_Title"
    const val LibraryManager_Title = "LibraryManager_Title"
    const val LibView_Name = "LibView_Name"
    const val LibView_Revision = "LibView_Revision"
    const val LibView_Status = "LibView_Status"
    const val LibView_apply = "LibView_Apply"
    const val LibView_Refresh = "LibView_Refresh"
    const val LibView_unApply = "LibView_UnApply"

    const val LicView_Column0 = "LicView_Column0"
    const val LicView_Column1 = "LicView_Column1"

    const val InfoView_Description = "InfoView_Description"
    const val InfoView_Detail = "InfoView_Detail"
    const val InfoView_History = "InfoView_History"
    const val InfoView_Name = "InfoView_Name"
    const val InfoView_Revision = "InfoView_Revision"
    const val InfoView_Status = "InfoView_Status"

    const val ProjectSettingView_0 = "ProjectSettingView_0";
    const val ProjectSettingView_1 = "ProjectSettingView_1";
    const val ProjectSettingView_10 = "ProjectSettingView_10";
    const val ProjectSettingView_11 = "ProjectSettingView_11";
    const val ProjectSettingView_12 = "ProjectSettingView_12";
    const val ProjectSettingView_13 = "ProjectSettingView_13";
    const val ProjectSettingView_14 = "ProjectSettingView_14";
    const val ProjectSettingView_15 = "ProjectSettingView_15";
    const val ProjectSettingView_16 = "ProjectSettingView_16";
    const val ProjectSettingView_17 = "ProjectSettingView_17";
    const val ProjectSettingView_18 = "ProjectSettingView_18";
    const val ProjectSettingView_19 = "ProjectSettingView_19";
    const val ProjectSettingView_2 = "ProjectSettingView_2";
    const val ProjectSettingView_20 = "ProjectSettingView_20";
    const val ProjectSettingView_21 = "ProjectSettingView_21";
    const val ProjectSettingView_22 = "ProjectSettingView_22";
    const val ProjectSettingView_23 = "ProjectSettingView_23";
    const val ProjectSettingView_24 = "ProjectSettingView_24";
    const val ProjectSettingView_25 = "ProjectSettingView_25";
    const val ProjectSettingView_26 = "ProjectSettingView_26";
    const val ProjectSettingView_27 = "ProjectSettingView_27";
    const val ProjectSettingView_28 = "ProjectSettingView_28";
    const val ProjectSettingView_29 = "ProjectSettingView_29";
    const val ProjectSettingView_3 = "ProjectSettingView_3";
    const val ProjectSettingView_30 = "ProjectSettingView_30";
    const val ProjectSettingView_31 = "ProjectSettingView_31";
    const val ProjectSettingView_32 = "ProjectSettingView_32";
    const val ProjectSettingView_33 = "ProjectSettingView_33";
    const val ProjectSettingView_34 = "ProjectSettingView_34";
    const val ProjectSettingView_35 = "ProjectSettingView_35";
    const val ProjectSettingView_36 = "ProjectSettingView_36";
    const val ProjectSettingView_37 = "ProjectSettingView_37";
    const val ProjectSettingView_38 = "ProjectSettingView_38";
    const val ProjectSettingView_39 = "ProjectSettingView_39";
    const val ProjectSettingView_4 = "ProjectSettingView_4";
    const val ProjectSettingView_40 = "ProjectSettingView_40";
    const val ProjectSettingView_41 = "ProjectSettingView_41";
    const val ProjectSettingView_42 = "ProjectSettingView_42";
    const val ProjectSettingView_43 = "ProjectSettingView_43";
    const val ProjectSettingView_44 = "ProjectSettingView_44";
    const val ProjectSettingView_45 = "ProjectSettingView_45";
    const val ProjectSettingView_46 = "ProjectSettingView_46";
    const val ProjectSettingView_47 = "ProjectSettingView_47";
    const val ProjectSettingView_48 = "ProjectSettingView_48";
    const val ProjectSettingView_49 = "ProjectSettingView_49";
    const val ProjectSettingView_5 = "ProjectSettingView_5";
    const val ProjectSettingView_50 = "ProjectSettingView_50";
    const val ProjectSettingView_51 = "ProjectSettingView_51";
    const val ProjectSettingView_52 = "ProjectSettingView_52";
    const val ProjectSettingView_53 = "ProjectSettingView_53";
    const val ProjectSettingView_54 = "ProjectSettingView_54";
    const val ProjectSettingView_55 = "ProjectSettingView_55";
    const val ProjectSettingView_56 = "ProjectSettingView_56";
    const val ProjectSettingView_57 = "ProjectSettingView_57";
    const val ProjectSettingView_58 = "ProjectSettingView_58";
    const val ProjectSettingView_59 = "ProjectSettingView_59";
    const val ProjectSettingView_6 = "ProjectSettingView_6";
    const val ProjectSettingView_60 = "ProjectSettingView_60";
    const val ProjectSettingView_61 = "ProjectSettingView_61";
    const val ProjectSettingView_62 = "ProjectSettingView_62";
    const val ProjectSettingView_7 = "ProjectSettingView_7";
    const val ProjectSettingView_8 = "ProjectSettingView_8";
    const val ProjectSettingView_9 = "ProjectSettingView_9";
    const val NPMBuild = "NPMBuild"
    const val IOSResourceNotExist = "IOSResourceNotExist"
    const val OpenXCode = "OpenXCode"
    const val LaunchIOS = "LaunchIOS"
    const val IOSProjectPath = "IOSProjectPath"
    const val IOSProjectFilePath = "IOSProjectFilePath"
    const val ResFolderPath = "ResFolderPath"
    const val CocoapodsNotExist = "CocoapodsNotExist";
    const val PodInstall = "PodInstall"
    const val getCocoapodsVersion = "getCocoapodsVersion"
    const val getCocoapodsPath = "getCocoapodsPath"

    const val ExportiOSTask = "ExportiOSTask"
    const val ExportiOSTasking = "ExportiOSTasking"
    const val ExportiOSFileFale = "ExportiOSFileFale"
    const val ExportiOSDeploying = "ExportiOSDeploying"
    const val ExportiOSDeleteTemp = "ExportiOSDeleteTemp"

    const val ExportResourceTask = "ExportResourceTask"
    const val ExportResourceFileFale = "ExportResourceFileFale"

    const val Device_Column0 = "Device_Column0";
    const val Device_Column1 = "Device_Column1";


    @JvmStatic
    fun get(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)

    @Suppress("unused")
    @JvmStatic
    fun getPointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)
}