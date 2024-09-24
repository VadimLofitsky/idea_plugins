package lofitsky.idea_plugins.live_template.context

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class MarkdownContext : TemplateContextType("Markdown") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean =
        templateActionContext.file.name.lowercase().endsWith(".md")
}
