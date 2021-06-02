package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.ChannelMap
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.interaction.*
import java.lang.String.format

class TalkIn(bot: Bot) : Command(
    bot,
    "talkin",
    "Talk as the bot in another channel",
) {
    override fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Boolean {
        val msg = interaction.message.get()
        val channels = msg.mentionedChannels
        if (channels.isEmpty()) {
            InteractionMessageBuilder()
                .setContent("Please provide a channel to talk in.")
                .sendFollowupMessage(interaction)
            return false
        }

        val channel = channels.first()
        val channelMap = ChannelMap(
            msg.author.id,
            channel,
            msg.channel,
        )

        if (!channel.canYouWrite()) {
            InteractionMessageBuilder()
                .setContent(format("I'm not able to talk in %s", channel.mentionTag))
                .sendFollowupMessage(interaction)
            return false
        }

        bot.channels[msg.author.id] = channelMap
        InteractionMessageBuilder()
            .setContent(format("Now transmitting messages to %s", channel.mentionTag))
            .sendInitialResponse(interaction)
            .join()

        return true
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
