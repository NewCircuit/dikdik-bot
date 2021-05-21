package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.ChannelMap
import io.newcircuit.dikdik.models.Command
import io.newcircuit.dikdik.models.CommandData
import java.lang.String.format

class TalkIn(bot: Bot) : Command(
    bot,
    "talkin",
    null,
    "Talk as the bot in another channel",
    bot.config.prefix + "talkin #channel",
) {
    override fun run(cmd: CommandData): Boolean {
        val channels = cmd.msg.mentionedChannels
        if (channels.isEmpty()) {
            cmd.msg.reply("Please provide a channel to talk in.")
            return false
        }
        val channel = channels.first()
        val channelMap = ChannelMap(
            cmd.msg.author.id,
            channel,
            cmd.msg.channel,
        )

        if (!channel.canYouWrite()) {
            cmd.msg.reply(format("I'm not able to talk in %s", channel.mentionTag))
            return false
        }

        bot.channels[cmd.msg.author.id] = channelMap
        cmd.msg.reply(format("Now transmitting messages to %s", channel.mentionTag))

        return true
    }
}