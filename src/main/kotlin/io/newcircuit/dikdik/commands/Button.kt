package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import io.newcircuit.dikdik.models.CommandData
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRowBuilder

class Button(bot: Bot) : Command(
    bot,
    "button",
    null,
    "Spawn a counter button",
    bot.config.prefix + "help"
) {
    override fun run(cmd: CommandData): Boolean {
        val serverOpt = cmd.msg.server
        val serverId = if (serverOpt.isPresent) {
            val server = serverOpt.get()
            server.id
        } else {
            null
        } ?: return false

        val button = bot.clicks.getButton(serverId)
        MessageBuilder()
            .setContent("Click Me!")
            .addComponent(
                ActionRowBuilder()
                    .addComponent(button)
            )
            .send(cmd.msg.channel)
            .join()

        return true
    }
}
