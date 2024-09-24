package lofitsky.idea_plugins.live_template.func.curr_date_time_reversed

import com.intellij.codeInsight.template.Expression
import com.intellij.codeInsight.template.ExpressionContext
import com.intellij.codeInsight.template.Result
import com.intellij.codeInsight.template.TextResult
import com.intellij.codeInsight.template.macro.MacroBase
import java.util.*

class CurrDatetimeReversed(name: String, description: String) : MacroBase(name, description) {
    private constructor() : this("dateTimeReversed", "dateTimeReversed()")

    override fun calculateResult(p0: Array<out Expression>, p1: ExpressionContext?, p2: Boolean): Result?
        = Calendar.getInstance().let {
            val year = it[1]
            val month = it[2] + 1
            val day = it[5]
            val hours = it[10]
            val mins = it[12]
            TextResult(String.format("%04d-%02d-%02d_%02d-%02d", year, month, day, hours, mins))
        }
}
