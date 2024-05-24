package m.client.ide.morpheus.framework.template

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import com.android.tools.idea.wizard.template.impl.activities.common.addMaterial3Dependency
import m.client.ide.morpheus.core.utils.CommonUtil
import m.client.ide.morpheus.framework.cli.MorpheusCLIUtil
import m.client.ide.morpheus.framework.messages.FrameworkMessages
import m.client.ide.morpheus.ui.dialog.ProjectConfigSettingDialog


private const val COMPOSE_BOM_VERSION = "2022.10.00"
private const val COMPOSE_KOTLIN_COMPILER_VERSION = "1.3.2"

fun RecipeExecutor.morpheusProjectRecipe(
    moduleData: ModuleTemplateData,
    templateData: MorpheusAppTemplateData,
    isDryRun : Boolean
) {
    addAllKotlinDependencies(moduleData)
    addMaterial3Dependency()

    if(!isDryRun) {
        val dialog = ProjectConfigSettingDialog(null, FrameworkMessages.get(FrameworkMessages.ApplicationProjectWizard_0))
        dialog.show()
        if (dialog.isOK) {
            templateData.license = dialog.selectLicense
            templateData.androidAppName = dialog.androidAppName
            templateData.androidPackage = dialog.androidPackageName
            templateData.setiOSAppName(dialog.getiOSAppName())
            templateData.setiOSBundleId(dialog.getiOSPackageName())
            templateData.cpus = dialog.selectionCpuList
        }

        MorpheusCLIUtil.createProject(
            moduleData,
            templateData
        )

        var appRoot = moduleData.rootDir
        var projectPath = appRoot.parent
        CommonUtil.refreshProject(projectPath)

        dryRun = true;
    } else {
        dryRun = false;
    }
}

