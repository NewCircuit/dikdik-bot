package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.Interaction

class Stop(bot: Bot) : Command(
    bot,
    "stop",
    "Stop talking in a channel",
) {
    override fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Boolean {
        val msg = interaction.message.get()
        val channelMap = bot.channels[msg.author.id]

        if (channelMap == null) {
            InteractionMessageBuilder()
                .setContent("Not talking in any other channels")
                .sendFollowupMessage(interaction)
                .join()
            return false
        }

        bot.channels.remove(msg.author.id)
        InteractionMessageBuilder()
            .setContent("Done.")
            .sendInitialResponse(interaction)
            .join()

        return true
    }
}