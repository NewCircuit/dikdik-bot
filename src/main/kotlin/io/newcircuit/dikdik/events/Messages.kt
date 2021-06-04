package io.newcircuit.dikdik.events

import io.newcircuit.dikdik.Bot
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

class Messages(private val bot: Bot) : MessageCreateListener {
    override fun onMessageCreate(event: MessageCreateEvent) {
        val msg = event.message

        if (msg.author.isBotUser) {
            return
        }
        val channelMap = bot.store.channels.get(msg.author.id) ?: return

        if (msg.channel.id != channelMap.from) {
            return
        }

        event.api.getTextChannelById(channelMap.to)
            .ifPresentOrElse({ channel ->
                msg.toMessageBuilder().send(channel)
                    .thenRun { msg.addReaction("✉️").join() }
            }, { bot.store.channels.remove(msg.author.id) })
    }
}
