package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.ChannelMap
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.*
import java.lang.String.format

class TalkIn(bot: Bot) : Command(
    bot,
    "talkin",
    "Talk as the bot in another channel",
) {
    override fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Pair<Boolean, String> {
        val msg = interaction.message.get()
        val channels = msg.mentionedChannels
        if (channels.isEmpty()) {
            return Pair(false, "Please provide a channel to talk in.")
        }

        val channel = channels.first()
        val channelMap = ChannelMap(
            msg.author.id,
            channel,
            msg.channel,
        )

        if (!channel.canYouWrite()) {
            return Pair(false, format("I'm not able to talk in %s", channel.mentionTag))
        }

        bot.channels[msg.author.id] = channelMap
        InteractionMessageBuilder()
            .setFlags(MessageFlag.EPHEMERAL)
            .setContent(format("Now transmitting messages to %s", channel.mentionTag))
            .sendInitialResponse(interaction)
            .join()

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
