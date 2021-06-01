package io.newcircuit.dikdik.events

import io.newcircuit.dikdik.Bot
import kotlinx.coroutines.runBlocking
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.component.*
import org.javacord.api.event.interaction.InteractionCreateEvent
import org.javacord.api.interaction.Interaction
import org.javacord.api.listener.interaction.InteractionCreateListener

class Interactions(bot: Bot) : InteractionCreateListener {
    private val clickManager = bot.clicks

    override fun onInteractionCreate(eventOpt: InteractionCreateEvent?) = runBlocking {
        val (event, interaction) = filter(eventOpt) ?: return@runBlocking
        val msg = interaction.message.get()
        val user = interaction.user
        val server = msg.server.get()

        event.updateMessage()
            .copy(msg)
            .setContent("**${user.getDisplayName(server)}** clicked last!")
            .removeAllComponents()
            .addComponent(clickManager.getClickedButton(msg))
            .update(interaction)
    }

    private fun filter(event: InteractionCreateEvent?): Pair<InteractionCreateEvent, Interaction>? {
        if (event == null) {
            return null
        }

        val data = if (event.interaction.componentData.isPresent) {
            event.interaction.componentData.get()
        } else {
            null
        } ?: return null

        return if (data.customId == "click_counter") {
            Pair(event, event.interaction)
        } else {
            null
        }
    }
}