package m.client.ide.morpheus.framework.messages;

import com.intellij.DynamicBundle
import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import icons.FlutterIcons
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "FrameworkResourceBundle"

object FrameworkMessages : DynamicBundle(BUNDLE) {

    const val MORPHEUS_NOTIFICATION_GROUP_ID = "Morpheus Messages"
    const val MORPHEUS_LOGGING_NOTIFICATION_GROUP_ID = "Morpheus Notifications"

    const val ApplicationProjectWizard_0 = "ApplicationProjectWizard_0";
    const val ApplicationProjectWizard_1 = "ApplicationProjectWizard_1";

    const val installNpmQuestion            = "installNpmQuestion"
    const val updateNpmQuestion             = "updateNpmQuestion"
    const val getMorpheusCLIVersion         = "getMorpheusCLIVersion"
    const val getNPMVersion                 = "getNPMVersion"
    const val updateNpm                     = "updateNpm"
    const val installNodeError              = "installNodeError"
    const val installPodQuestion            = "installPodQuestion"

    const val morpheusCliCheck              = "morpheusCliCheck"
    const val morpheusCliInstallQuestion    = "morpheusCliInstallQuestion"
    const val installMorpheusCLI            = "installMorpheusCLI"
    const val iosDeployInstallQuestion      = "iosDeployInstallQuestion"

    const val createProject                 = "createProject"
    const val addLicense                    = "addLicense"
    const val getLicenseList                = "getLicenseList"
    const val getLibraryList                = "getLibraryList"
    const val getTemplateList               = "getTemplateList"
    const val manLibrary                    = "manLibrary"
    const val applyLicense                  = "applyLicense"

    const val getIosDeployVersion           = "getIosDeployVersion"
    const val installIosDeploy              = "installIosDeploy"

    const val XCWorkspaceNotExist           = "XCWorkspaceNotExist"
    const val SelectSimulator               = "SelectSimulator"
    const val SelectDevice                  = "SelectDevice"
    const val SelectDevelopment             = "SelectDevelopment"

    const val installNpm                    = "installNpm"
    const val installNpmTerminal            = "installNpmTerminal"
    const val setNpmPath                    = "setNpmPath"
    const val installNodeAndSet             = "installNodeAndSet"


    @JvmStatic
    fun get(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) = getMessage(key, *params)

    @Suppress("unused")
    @JvmStatic
    fun getPointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)

}
