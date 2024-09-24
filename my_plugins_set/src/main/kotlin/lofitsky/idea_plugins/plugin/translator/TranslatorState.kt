package lofitsky.idea_plugins.plugin.translator

import com.intellij.ide.util.PropertiesComponent
import lofitsky.idea_plugins.plugin.util.PluginUtil.Companion.notify

object TranslatorState {
    private val REQUESTS_LIMIT_PER_MONTH = 5

    private val STATE_PROPERTY_NAME_COUNTER = "lofitsky.plugin.translator.state.counter"
    private val STATE_PROPERTY_NAME_LAST_CALL_MONTH = "lofitsky.plugin.translator.state.lastCallMonth"
    private val STATE_PROPERTY_NAME_LAST_CALL_YEAR = "lofitsky.plugin.translator.state.lastCallYear"
    private val STATE_PROPERTY_NAME_BYTES_COUNTER = "lofitsky.plugin.translator.state.bytesCounter"

    private data class _State(
        val counter: Int,
        val lastCallMonth: Int,
        val lastCallYear: Int,
        val bytesCounter: Int,
    )

    private val propertiesComponent = PropertiesComponent.getInstance()

    val isAvailable: Boolean
        get() = _getState().counter < REQUESTS_LIMIT_PER_MONTH

    var requestsLeft: Int = REQUESTS_LIMIT_PER_MONTH
        //        get() = REQUESTS_LIMIT_PER_MONTH - _getState().counter
        get() {
            notify("state = ${_getState()}")
            return REQUESTS_LIMIT_PER_MONTH - _getState().counter
        }
        private set

    private fun _getState() = _State(
        propertiesComponent.getInt(STATE_PROPERTY_NAME_COUNTER, 0),
        propertiesComponent.getInt(STATE_PROPERTY_NAME_LAST_CALL_MONTH, 0),
        propertiesComponent.getInt(STATE_PROPERTY_NAME_LAST_CALL_YEAR, 0),
        propertiesComponent.getInt(STATE_PROPERTY_NAME_BYTES_COUNTER, 0),
    )

    private fun _setState(counter: Int, month: Int, year: Int, bytes: Int): Unit {
        propertiesComponent.setValue(STATE_PROPERTY_NAME_COUNTER, counter, -1)
        propertiesComponent.setValue(STATE_PROPERTY_NAME_LAST_CALL_MONTH, month, -2)
        propertiesComponent.setValue(STATE_PROPERTY_NAME_LAST_CALL_YEAR, year, -1)
        propertiesComponent.setValue(STATE_PROPERTY_NAME_BYTES_COUNTER, bytes, -1)
    }

    fun reset() {
        _getState().also {
            propertiesComponent.setValue(STATE_PROPERTY_NAME_COUNTER, 0, 0)
            propertiesComponent.setValue(STATE_PROPERTY_NAME_LAST_CALL_MONTH, 0, 0)
            propertiesComponent.setValue(STATE_PROPERTY_NAME_LAST_CALL_YEAR, 0, 0)
            propertiesComponent.setValue(STATE_PROPERTY_NAME_BYTES_COUNTER, 0, 0)
        }
    }

    fun save(month: Int, year: Int, bytes: Int): Unit {
        _getState().also {
            var str = ""
            str += "saved: ${it.lastCallYear} * 12 + ${it.lastCallMonth} = ${it.lastCallYear * 12 + it.lastCallMonth}\n"
            str += "curr: $year * 12 + $month = ${year * 12 + month}\n"
            val (cnt, b) = if((it.lastCallYear * 12 + it.lastCallMonth) < (year * 12 + month)) (1 to bytes) else ((it.counter + 1) to (it.bytesCounter + bytes))
            str += listOf(cnt, month, year, b).toString()
            notify(str)
            _setState(cnt, month, year, b)
        }
    }
}
