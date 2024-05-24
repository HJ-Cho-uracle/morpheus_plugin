package m.client.ide.morpheus.framework.template

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider

class MorpheusAppProjectTemplatesProvider : WizardTemplateProvider() {
    override fun getTemplates() : List<Template> {
        return listOf(javaObjcEmptyTemplate)
    }
}