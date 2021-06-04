package io.newcircuit.dikdik.store

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.component.*
import java.io.File

@Serializable
class ButtonClicks(
    private val clicks: HashMap<Long, Int> = HashMap(),
) : Store<ButtonClicks>("./config/clicks.json") {

    fun getButton(serverId: Long): ButtonBuilder {
        val clicks = getClicks(serverId)
        return ButtonBuilder()
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

    private fun getClicks(id: Long): Int {
        var num = clicks[id]

        num = if (num != null) {
            clicks[id] = (num + 1)
            num
        } else {
            clicks[id] = 1
            1
        }

        return num
    }

    override fun generate(file: File) {
        val ref = ButtonClicks()
        val serialized = Json.encodeToString(serializer(), ref)

        file.writeText(serialized)
    }

    override fun load(file: File) {
        val data = file.readText()
        val serialized = Json.decodeFromString(serializer(), data)

        for ((id, clicks) in serialized.clicks) {
            this.clicks[id] = clicks
        }
    }

    override suspend fun save(data: ButtonClicks) = coroutineScope {
        val file = File(location)
        val serialized = Json.encodeToString(serializer(), data)

        file.writeText(serialized)
    }
}
