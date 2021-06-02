package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.component.ActionRowBuilder
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.ApplicationCommandOptionBuilder
import org.javacord.api.interaction.ApplicationCommandOptionType
import org.javacord.api.interaction.Interaction

class Button(bot: Bot) : Command(
    bot,
    "button",
    "Spawn a counter button",
) {
    override fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Pair<Boolean, String> {
        val target = getChannel(interaction, data)
            ?: return Pair(false, "Please provide a text-channel.")
        val server = interaction.server.get()
        val serverId = server.id

        val button = bot.clicks.getButton(serverId)
        val res = InteractionMessageBuilder()
            .setFlags(MessageFlag.EPHEMERAL)
            .setContent("Sent.")
            .sendInitialResponse(interaction)
            .join()
            .deleteInitialResponse(interaction)
            .join()

        MessageBuilder()
            .setContent("Click Me!")
            .addComponent(
                ActionRowBuilder()
                    .addComponent(button)
            )
            .send(target)
            .join()

        return Pair(true, "")
    }

    override fun getOptions(): ArrayList<ApplicationCommandOptionBuilder> {
        return arrayListOf(
            ApplicationCommandOptionBuilder()
                .setName("channel")
                .setDescription("Optionally send this button to a text-channel.")
                .setType(ApplicationCommandOptionType.CHANNEL)
                .setRequired(false)
        )
    }
}
