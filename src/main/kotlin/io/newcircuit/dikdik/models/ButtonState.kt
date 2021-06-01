package io.newcircuit.dikdik.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.*
import java.io.File
import kotlin.collections.HashMap

@Serializable
class ButtonState(
    private val clicks: HashMap<Long, Int> = HashMap(),
) {
    private val INTERVALS = 1000L
    private var lastSaved = System.currentTimeMillis()


    suspend fun getClicks(id: Long): Int = coroutineScope {
        var num = clicks[id]

        num = if (num != null) {
            clicks[id] = (num + 1)
            num
        } else {
            clicks[id] = 1
            1
        }

        launch {
            val now = System.currentTimeMillis()
            val diff = now - lastSaved
            if (diff >= INTERVALS) {
                save()
                lastSaved = now
            }
        }

        return@coroutineScope num
    }

    fun getButton(serverId: Long): ButtonBuilder = runBlocking {
        val clicks = getClicks(serverId)
        return@runBlocking ButtonBuilder()
            .setCustomId("click_counter")
            .setStyle(ButtonStyle.PRIMARY)
            .setLabel("Clicks: $clicks")
    }

    fun getClickedButton(original: Message): ActionRowBuilder {
        val serverId = original.server.get().id
        val comp = original.components[0] as ActionRow
        val button = comp.components[0] as Button
        val bBuilder = getButton(serverId)

        when (button.style) {
            ButtonStyle.PRIMARY -> bBuilder.setStyle(ButtonStyle.DANGER)
            ButtonStyle.DANGER -> bBuilder.setStyle(ButtonStyle.SECONDARY)
            ButtonStyle.SECONDARY -> bBuilder.setStyle(ButtonStyle.SUCCESS)
            else -> bBuilder.setStyle(ButtonStyle.PRIMARY)
        }

        return ActionRowBuilder()
            .addComponent(bBuilder)
    }

    private fun save() {
        val file = File(LOCATION)
        val data = Json.encodeToString(serializer(), this)

        file.writeText(data)
    }

    companion object {
        private const val LOCATION = "./config/clicks.json"
        fun getState(): ButtonState {
            val file = File(LOCATION)

            return if (!file.exists()) {
                genState(file)
            } else {
                val data = file.readText()
                Json.decodeFromString(serializer(), data)
            }
        }

        private fun genState(file: File): ButtonState {
            val ref = ButtonState()
            val serialized = Json.encodeToString(ref)
            file.writeText(serialized)

            return ref
        }
    }
}
