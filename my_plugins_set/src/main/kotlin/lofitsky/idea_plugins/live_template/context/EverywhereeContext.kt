package lofitsky.idea_plugins.live_template.context

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class EverywhereeContext : TemplateContextType("Everywheree") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean = true
}
