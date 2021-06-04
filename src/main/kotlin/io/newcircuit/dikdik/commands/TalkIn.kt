package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.ApplicationCommandOptionBuilder
import org.javacord.api.interaction.ApplicationCommandOptionType
import org.javacord.api.interaction.Interaction
import java.lang.String.format

class TalkIn(bot: Bot) : Command(
    bot,
    "talkin",
    "Talk as the bot in another channel",
) {
    override fun run(
        interaction: Interaction,
        data: ApplicationCommandInteractionData,
    ): Pair<Boolean, String> {
        val user = interaction.user
        val channel = interaction.channel.get()
        val target = getChannel(interaction, data)
            ?: return Pair(false, "You're not able to talk in that channel.")

        bot.store.channels.create(
            user.id,
            channel.id,
            target.id,
        )

        if (!channel.canYouWrite()) {
            return Pair(
                false,
                format("I'm not able to talk in %s", target.mentionTag),
            )
        }

        InteractionMessageBuilder()
            .setFlags(MessageFlag.EPHEMERAL)
            .setContent(
                format("Now transmitting messages to %s", target.mentionTag),
            ).sendInitialResponse(interaction).join()

        return Pair(true, "")
    }

    override fun getOptions(): ArrayList<ApplicationCommandOptionBuilder> {
        return arrayListOf(
            ApplicationCommandOptionBuilder()
                .setName("channel")
                .setDescription("The channel to talk in.")
                .setType(ApplicationCommandOptionType.CHANNEL)
                .setRequired(true)
        )
    }
}
