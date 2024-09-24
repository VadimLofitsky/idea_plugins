package lofitsky.idea_plugins.plugin.reveal_file_in_project_panel

import com.intellij.ide.impl.ProjectViewSelectInTarget
import com.intellij.ide.projectView.impl.ProjectViewPane
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class RevealInProjectTreeAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return
        ProjectViewSelectInTarget
            .select(project, psiFile, ProjectViewPane.ID, null, psiFile.virtualFile, true)
    }
}
