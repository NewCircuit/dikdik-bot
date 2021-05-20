package io.newcircuit.dikdik.events

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.CommandData
import org.javacord.api.entity.message.Message
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

class Messages(private val bot: Bot) : MessageCreateListener {
    override fun onMessageCreate(event: MessageCreateEvent) {
        val msg = event.message
        var result = false

        if (!this.filter(msg)) {
            this.attemptRelay(msg)
            return
        }

        val cmd = CommandData.parse(bot.config.prefix, msg)

        for (command in bot.commands) {
            if (command.matches(cmd)) {
                result = command.execute(cmd)
                break
            }
        }

        val emoji = if (result) "✅" else "❌"
        event.addReactionToMessage(emoji)
    }

    private fun attemptRelay(msg: Message) {
        if (msg.author.isBotUser) { return }
        val channelMap = bot.channels[msg.author.id] ?: return

        if (msg.channel == channelMap.from) {
            msg.toMessageBuilder().send(channelMap.to)
            msg.addReaction("✉️")
        }
    }

    private fun filter(msg: Message): Boolean {
        return !msg.author.isBotUser
                && msg.content.startsWith(bot.config.prefix)
    }
}
