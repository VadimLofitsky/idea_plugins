package lofitsky.idea_plugins.plugin.translator

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import lofitsky.idea_plugins.plugin.util.PluginUtil.Companion.notify
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import java.util.*

object TheTranslator {
//    private val uri = "https://google-translate-v21.p.rapidapi.com/translate"
    private const val uri = "https://webhook.site/eee61dcb-1ba6-4224-95f5-d0bba966f3b9"

    private val mapper = jacksonObjectMapper().registerKotlinModule()

    val calendar = Calendar.getInstance(TimeZone.getDefault())

    private class Message(
        val text_to_translate: String,
        val dest: String = "ru"
    ) {
        fun stringify(): String = mapper.writeValueAsString(this)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private class ServiceResponse(
        val translation: String?
    )

    class TranslateResponse(
        val statusCode: Int,
        val payload: String?,
    )

    private val httpClient: HttpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
        .build()

    fun translate(text: String, callback: (TranslateResponse) -> Unit): Unit {
        if(!TranslatorState.isAvailable) {
            notify("Исчерпан лимит переводов на этот месяц!")
            return
        }

        val request = HttpRequest.newBuilder(URI.create(uri))
            .POST(BodyPublishers.ofString(Message(text).stringify()))
                .header("Content-Type", "application/json")
                .header("X-RapidAPI-Key", "420839e931mshe55228ad3d413d9p16d1acjsne251e245b257")
                .header("X-RapidAPI-Host", "google-translate-v21.p.rapidapi.com")
            .build()

        try {
            httpClient.sendAsync(request, BodyHandlers.ofString())
                .thenApply { TranslateResponse(it.statusCode(), mapper.readValue<ServiceResponse>(it.body()).translation) }
                .thenApply {
                    TranslatorState.save(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), text.length)
                    it
                }
                .thenAccept(callback)
        } catch(e: Exception) {
            callback(TranslateResponse(500, e.message.toString()))
        }
    }
}
