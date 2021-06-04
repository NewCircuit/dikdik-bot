package io.newcircuit.dikdik.events

import io.newcircuit.dikdik.Bot
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.event.interaction.InteractionCreateEvent
import org.javacord.api.interaction.ApplicationCommandInteractionData
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
            this.onButtonClicker(event)
        } else if (data.customId.startsWith("vote")) {
            this.onVote(event, data)
        }
    }

    private fun onCommandEvent(event: InteractionCreateEvent, data: ApplicationCommandInteractionData) {
        var result: Pair<Boolean, String> = Pair(false, "Command not found.")
        for (command in bot.commands) {
            if (command.name == data.name) {
                result = command.execute(event.interaction, data)
                break
            }
        }
        val (res, reason) = result

        if (!res) {
            event.respond()
                .setFlags(MessageFlag.EPHEMERAL)
                .setContent(reason)
                .sendInitialResponse(event.interaction)
                .join()
        }
    }

    private fun onVote(event: InteractionCreateEvent, data: InteractionComponentData) {
        val interaction = event.interaction
        val isYes = data.customId == "vote_yes"
        val voteId = interaction.channel.get().id
        val vote = bot.votes[voteId]?: return

        vote.addVote(
            event.interaction,
            interaction.user.id,
            isYes,
        )
    }

    private fun onButtonClicker(event: InteractionCreateEvent) {
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