package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import io.newcircuit.dikdik.models.CommandData
import java.lang.String.format

class Help(bot: Bot) : Command(
    bot,
    "help",
    null,
    "Help dialogue",
    bot.config.prefix + "help"
) {
    override fun check(cmd: CommandData): Boolean = true
    override fun run(cmd: CommandData): Boolean {
        var commands = "Commands:\n"

        for (command in bot.commands) {
            commands += format("%s%s\n", bot.config.prefix, command)
        }

        cmd.msg.reply(commands)
        return true
    }
}
