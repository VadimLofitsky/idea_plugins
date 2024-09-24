package lofitsky.idea_plugins.plugin.open_gnome_terminal

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import java.io.File

class OpenGnomeTerminalAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val path = event.getData(CommonDataKeys.VIRTUAL_FILE)?.path?.let { s ->
            File(s.replaceFirst("file://", "", true)).let {
                if(it.isFile) it.parent else it.path
            }
        }
//        val psiVirtualFile = event.getData(CommonDataKeys.PSI_FILE)?.virtualFile ?: return
//        val path = if(psiVirtualFile.isDirectory) psiVirtualFile.path else psiVirtualFile.parent.path
        Runtime.getRuntime().exec(arrayOf("gnome-terminal", "--tab", "--working-directory=$path"))
    }
}
