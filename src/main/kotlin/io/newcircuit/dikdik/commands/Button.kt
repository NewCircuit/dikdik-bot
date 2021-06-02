package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRowBuilder
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.Interaction

class Button(bot: Bot) : Command(
    bot,
    "button",
    "Spawn a counter button",
) {
    override fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Boolean {
        val server = interaction.server.get()
        val serverId = server.id

        val button = bot.clicks.getButton(serverId)
        InteractionMessageBuilder()
            .setContent("Click Me!")
            .addComponent(
                ActionRowBuilder()
                    .addComponent(button)
            )
            .sendInitialResponse(interaction)
            .join()

        return true
    }
}
