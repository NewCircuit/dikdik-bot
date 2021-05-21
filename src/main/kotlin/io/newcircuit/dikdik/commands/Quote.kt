package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import io.newcircuit.dikdik.models.CommandData

class Quote(bot: Bot) : Command(
    bot,
    "fact",
    "joke",
    "Send a random fact or joke",
    bot.config.prefix + "fact (optional #channel)",
) {
    override fun run(cmd: CommandData): Boolean {
        val channels = cmd.msg.mentionedChannels
        val target = if (channels.isEmpty()) {
            cmd.msg.channel
        } else {
            channels.first()
        }

        val random = if (cmd.name == "fact") {
            bot.config.facts.random()
        } else {
            bot.config.jokes.random()
        }

        target.sendMessage(random)
        return true
    }
}
