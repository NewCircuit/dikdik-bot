package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import io.newcircuit.dikdik.models.CommandData

class Stop(bot: Bot) : Command(
    bot,
    "stop",
    null,
    "Stop talking in a channel",
    bot.config.prefix + "stop",
) {
    override fun run(cmd: CommandData): Boolean {
        val channelMap = bot.channels[cmd.msg.author.id]

        if (channelMap == null) {
            cmd.msg.reply("Not talking in any other channels")
            return false
        }

        bot.channels.remove(cmd.msg.author.id)
        return true
    }
}