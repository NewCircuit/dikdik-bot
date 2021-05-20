package io.newcircuit.dikdik

import io.newcircuit.dikdik.commands.Quote
import io.newcircuit.dikdik.commands.Help
import io.newcircuit.dikdik.commands.Stop
import io.newcircuit.dikdik.commands.TalkIn
import io.newcircuit.dikdik.config.Config
import io.newcircuit.dikdik.events.Messages
import io.newcircuit.dikdik.models.ChannelMap
import io.newcircuit.dikdik.models.Command
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import java.util.concurrent.CompletionException


class Bot(val config: Config) {
    val commands = ArrayList<Command>()
    val channels = HashMap<Long, ChannelMap>()

    init {
        this.registerCommands()
    }

    fun start() {
        val client: DiscordApi

        try {
            client = DiscordApiBuilder()
                .setToken(config.token)
                .login().join()
        } catch (_: CompletionException) {
            println("Failed to login, is the token correct?")
            return
        }

        val msgEvent = Messages(this)

        client.addListener(msgEvent)
    }

    private fun registerCommands() {
        val help = Help(this)
        val quotes = Quote(this)
        val talkin = TalkIn(this)
        val stop = Stop(this)

        commands.add(help)
        commands.add(quotes)
        commands.add(talkin)
        commands.add(stop)
    }
}
