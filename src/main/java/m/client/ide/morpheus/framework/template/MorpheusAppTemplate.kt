package m.client.ide.morpheus.framework.template

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import m.client.ide.morpheus.core.utils.FileUtil
import m.client.ide.morpheus.framework.messages.FrameworkMessages
import java.io.File

enum class CPUs {
    ARM64_V8A, ARMEABI_V7A, X86, X86_64;

    companion object {
        fun toList(): List<String> {
            val list = listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")

            return list;
        }
    }
}

var dryRun: Boolean = true
private const val MIN_SDK = 16

val javaObjcEmptyTemplate
    get() = template {
        name = FrameworkMessages.get(FrameworkMessages.ApplicationProjectWizard_0)

        description = FrameworkMessages.get(FrameworkMessages.ApplicationProjectWizard_1)
        minApi = MIN_SDK
//        constraints = listOf(
//            TemplateConstraint.AndroidX,
//            TemplateConstraint.Kotlin
//        )
        category = Category.Other
        formFactor = FormFactor.Generic
        screens = listOf(/*WizardUiContext.FragmentGallery, WizardUiContext.MenuEntry,*/
            WizardUiContext.NewProject/*, WizardUiContext.NewModule, WizardUiContext.NewProjectExtraDetail*/)

        val packageName = defaultPackageNameParameter
        val pathName = packageName

        widgets(
            PackageNameWidget(pathName)
        )

        // I am reusing the thumbnail provided by Android Studio, but
        // replace it with your own
        thumb { File("compose-activity-material3").resolve("template_compose_empty_activity_material3.png") }
//        thumb { FileUtil.getResourceFile("/images/TemplateEmpty.png").absoluteFile }

        val license = "mcore.edu.*"
        val cpus = listOf(true, true, false, false, false)
        val libraries = null;
        val templateData = MorpheusAppTemplateData(
            license, "myApp", "", "myApp", "",
            cpus,
            libraries,
            MorpheusTemplateHelper.TemplateType.JAVA_OBJC_EMPTY
        )

        val isDryRun = isDryRun();
        recipe = { data: TemplateData ->
            morpheusProjectRecipe(
                moduleData = data as ModuleTemplateData,
                templateData = templateData,
                isDryRun = dryRun
            )
        }
    }

val otherTemplate
    get() = template {
        name = FrameworkMessages.get(FrameworkMessages.ApplicationProjectWizard_0)

        description = FrameworkMessages.get(FrameworkMessages.ApplicationProjectWizard_1)
        minApi = MIN_SDK
        category = Category.Other
        formFactor = FormFactor.Generic
        screens = listOf(/*WizardUiContext.FragmentGallery, WizardUiContext.MenuEntry,*/
            WizardUiContext.NewProject/*, WizardUiContext.NewModule, WizardUiContext.NewProjectExtraDetail*/)

        val packageName = defaultPackageNameParameter
        val pathName = packageName

        widgets(
            PackageNameWidget(pathName)
        )

        // I am reusing the thumbnail provided by Android Studio, but
        // replace it with your own
        thumb { File("compose-activity-material3").resolve("template_compose_empty_activity_material3.png") }

        val license = "mcore.edu.*"
        val cpus = listOf(true, true, false, false, false)
        val libraries = null;
        val templateData = MorpheusAppTemplateData(
            license, "myApp", "", "myApp", "",
            cpus,
            libraries,
            MorpheusTemplateHelper.TemplateType.other
        )

        val isDryRun = isDryRun();
        recipe = { data: TemplateData ->
            morpheusProjectRecipe(
                moduleData = data as ModuleTemplateData,
                templateData = templateData,
                isDryRun = dryRun
            )
        }
    }

fun isDryRun(): Boolean {
    return dryRun;
}

fun trueVisible(): Boolean {
    return true;
}

fun falseEnabled(): Boolean {
    return false;
}
