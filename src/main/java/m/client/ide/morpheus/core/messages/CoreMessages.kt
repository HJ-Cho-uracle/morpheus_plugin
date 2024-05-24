package m.client.ide.morpheus.core.messages;


import com.intellij.DynamicBundle
import m.client.ide.morpheus.core.utils.ZipUtils
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "CoreResourceBundle"

object CoreMessages : DynamicBundle(BUNDLE) {

    const val BUTTON_ADD                  = "BUTTON_ADD"
    const val BUTTON_BROWSE               = "BUTTON_BROWSE"
    const val BUTTON_DELETE               = "BUTTON_DELETE"
    const val BUTTON_MODIFY               = "BUTTON_MODIFY"
    const val BUTTON_REFRESH_LIST         = "BUTTON_REFRESH_LIST"
    const val BUTTON_RUN_AVD              = "BUTTON_RUN_AVD"
    const val BUTTON_USE                  = "BUTTON_USE"
    const val BUTTON_NOT_USE              = "BUTTON_NOT_USE"
    const val CommonUtil_0                = "CommonUtil_0"
    const val CommonUtil_1                = "CommonUtil_1"
    const val ForgeryDetectionDialog_0    = "ForgeryDetectionDialog_0"
    const val ForgeryDetectionDialog_1    = "ForgeryDetectionDialog_1"
    const val ForgeryDetectionDialog_2    = "ForgeryDetectionDialog_2"
    const val ForgeryDetectionDialog_5    = "ForgeryDetectionDialog_5"
    const val GROUP_DIRECT_VIEW           = "GROUP_DIRECT_VIEW"
    const val GROUP_PROJECT_DESCRIPTION   = "GROUP_PROJECT_DESCRIPTION"
    const val GROUP_TARGET                = "GROUP_TARGET"
    const val IDEUpdater_0                = "IDEUpdater_0"
    const val InitializationManager_0     = "InitializationManager_0"
    const val JSTemplateUpdateNotificationPopup_0 = ""

    const val LABEL_APPLICATION_NAME          = "LABEL_APPLICATION_NAME"
    const val LABEL_ANDROID_PACKAGE_NAME      = "LABEL_ANDROID_PACKAGE_NAME"
    const val LABEL_ANDROID_PROJECT_NAME      = "LABEL_ANDROID_PROJECT_NAME"
    const val LABEL_DIRECT_VIEW               = "LABEL_DIRECT_VIEW"
    const val LABEL_INSTALLED                 = "LABEL_INSTALLED"
    const val LABEL_IOS_BUNDLE_IDENTIFIER     = "LABEL_IOS_BUNDLE_IDENTIFIER"
    const val LABEL_IOS_PROJECT_NAME          = "LABEL_IOS_PROJECT_NAME"
    const val LABEL_LIBRARIES                 = "LABEL_LIBRARIES"
    const val LABEL_LICENSE_STATUS            = "LABEL_LICENSE_STATUS"
    const val LABEL_NOT_INSTALLED             = "LABEL_NOT_INSTALLED"
    const val LABEL_NOT_USING                 = "LABEL_NOT_USING"
    const val LABEL_PROJECT                   = "LABEL_PROJECT"
    const val LABEL_PROJECT_NAME              = "LABEL_PROJECT_NAME"
    const val LABEL_RESOURCE_PATH             = "LABEL_RESOURCE_PATH"
    const val LABEL_TARGET_FILE               = "LABEL_TARGET_FILE"
    const val LABEL_UPDATE_AVAILABLE          = "LABEL_UPDATE_AVAILABLE"
    const val LABEL_USING                     = "LABEL_USING"
    const val LogUtil_1                       = "LogUtil_1"
    const val LogUtil_2                       = "LogUtil_2"
    const val OpenResourceManagerHandler_1    = "OpenResourceManagerHandler_1"

    const val TITLE_LIBRARY           =""

    const val IphoneUtils_0               = "IphoneUtils_0"
    const val IphoneUtils_1               = "IphoneUtils_1"


    const val ProjectProperties_0             = "ProjectProperties_0"
    const val ProjectProperties_1             = "ProjectProperties_1"
    const val AndroidSDKInstalls_17           = "AndroidSDKInstalls_17"
    const val XmlUtilMkDirError               = "XmlUtilMkDirError"
    const val DeviceView_ADB_Error            = "DeviceView_ADB_Error"
    const val DeviceView_ADB_Failed_Restart   = "DeviceView_ADB_Failed_Restart"

    const val GradleLocationError               = "GradleLocationError"
    const val PackageApplicationLocationError   = "PackageApplicationLocationError"
    const val GradleLibsDebugLocationError      = "GradleLibsDebugLocationError"
    const val GradleLibsReleaseLocationError    = "GradleLibsReleaseLocationError"
    const val IOSDeployLocationError            = "IOSDeployLocationError"
    const val IOSSimLocationError               = "IOSSimLocationError"
    const val ModPbxprojLocationError           = "ModPbxprojLocationError"

    const val DownloadFileWithURL               = "DownloadFileWithURL"

    const val npmInstall                        = "npmInstall"
    const val npmAuditFix                       = "npmAuditFix"

    const val ToolsNotExist                     = "ToolsNotExist"
    const val ToolsInitQuestion                 = "ToolsInitQuestion"
    const val InitToolsData                     = "InitToolsData"
    const val DownloadToolsData                 = "DownloadToolsData"
    const val ExtractToolsData                  = "ExtractToolsData"
    const val CopyToolsDataToAppData            = "CopyToolsDataToAppData"

    const val ZipUtils                          = "ZipUtils"

    @JvmStatic
    fun get(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)

    @Suppress("unused")
    @JvmStatic
    fun getPointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)
}