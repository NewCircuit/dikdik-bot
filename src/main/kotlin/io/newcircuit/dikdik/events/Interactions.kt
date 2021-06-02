package io.newcircuit.dikdik.events

import io.newcircuit.dikdik.Bot
import org.javacord.api.entity.message.component.*
import org.javacord.api.event.interaction.InteractionCreateEvent
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.InteractionComponentData
import org.javacord.api.listener.interaction.InteractionCreateListener

class Interactions(private val bot: Bot) : InteractionCreateListener {
    private val clickManager = bot.clicks

    override fun onInteractionCreate(eventOpt: InteractionCreateEvent?) {
        eventOpt?: return

        if (eventOpt.interaction.componentData.isPresent) {
            this.onComponentEvent(eventOpt, eventOpt.interaction.componentData.get())
        } else {
            this.onCommandEvent(eventOpt, eventOpt.interaction.commandData.get())
        }
    }
    private fun onComponentEvent(event: InteractionCreateEvent, data: InteractionComponentData) {
        if (data.customId == "click_counter") {
            this.onButtonClick(event)
        }
    }

    private fun onCommandEvent(event: InteractionCreateEvent, data: ApplicationCommandInteractionData) {
        println(event.interaction.user.name)
        println("Found: ${bot.commands.size}")
        for (command in bot.commands) {
            if (command.name == data.name) {
                command.execute(event.interaction, data)
            }
        }
    }

    private fun onButtonClick(event: InteractionCreateEvent) {
        val interaction = event.interaction
        val msg = interaction.message.get()
        val user = interaction.user
        val server = msg.server.get()
        val button = clickManager.getClickedButton(msg)

        event.updateComponentMessage()
            .copy(msg)
            .setContent("**${user.getDisplayName(server)}** clicked last!")
            .removeAllComponents()
            .addComponent(button)
            .update(interaction)
    }
}