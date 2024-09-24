package lofitsky.idea_plugins.plugin.translator

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.util.ui.JBUI.Fonts
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.properties.Delegates
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class TranslatorToolWindowFactory : ToolWindowFactory {
    private val tool = TranslatorToolWindow()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit {
        val content = ContentFactory.getInstance().createContent(tool.buildPanel(), "en → ru", false)
        toolWindow.contentManager.addContent(content)
    }

    private class TranslatorToolWindow {
        private lateinit var panel: DialogPanel
        private lateinit var textAreaRu: Cell<JBTextArea>

        private var textEn: String = "Enter text..."

        private var textRu: String by Delegates.observable("") { p, o, n -> textAreaRu.text(n) }

//        private var requestsLeft: Int by PropertyDelegateProvider<TranslatorToolWindow?, Int> { thisRef, prop1 ->
//            ReadOnlyProperty<TranslatorToolWindow?, Int> { _, prop2 -> 17 }
//        }

        private val requestsLeftInfo: String
            get() = String.format("Осталось: %d запросов.", TranslatorState.requestsLeft)

        private lateinit var requestsLimitInfoLabel: Cell<JLabel>

        fun buildPanel(): JComponent {
            panel = panel {
                row {
                    textArea()
                        .bindText(::textEn)
                        .rows(10)
                        .resizableColumn()
                        .align(AlignX.FILL)
                }.layout(RowLayout.INDEPENDENT)

                row {
                    actionButton(object : AnAction("Перевести", "Перевести", AllIcons.Actions.ForceRefresh) {
                        override fun actionPerformed(e: AnActionEvent) {
                            TheTranslator.translate(textEn) {
                                textRu = it.statusCode.toString() + "\n" + (it.payload ?: "null")
                                requestsLimitInfoLabel.component.text = requestsLeftInfo
                                panel.apply()
                            }
                        }
                    }).resizableColumn()
                        .align(AlignX.CENTER)
                }.layout(RowLayout.INDEPENDENT)
                    .topGap(TopGap.SMALL)
                    .bottomGap(BottomGap.SMALL)
                    .enabledIf(ComponentPredicate.fromValue(TranslatorState.isAvailable))

                row {
                    actionButton(object : AnAction("reset", "reset", AllIcons.Actions.ForceRefresh) {
                        override fun actionPerformed(e: AnActionEvent) {
                            TranslatorState.reset()
                        }
                    }).resizableColumn()
                        .align(AlignX.CENTER)
                }.layout(RowLayout.INDEPENDENT)
                    .topGap(TopGap.SMALL)
                    .bottomGap(BottomGap.SMALL)

                row {
                    requestsLimitInfoLabel = label(requestsLeftInfo)
                        .resizableColumn()
                        .align(AlignX.CENTER)
                        .also { it.component.font = Fonts.create("JetBrains Mono", 9) }
                        .also { requestsLimitInfoLabel = it }
                }

                row {
                    textArea()
                        .text(textRu)
                        .rows(10)
                        .resizableColumn()
                        .also { textAreaRu = it }
                        .align(AlignX.FILL)
                }.layout(RowLayout.INDEPENDENT)
            }
            return panel
        }
    }
}
