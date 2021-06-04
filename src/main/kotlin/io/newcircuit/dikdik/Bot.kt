package io.newcircuit.dikdik

import io.newcircuit.dikdik.commands.*
import io.newcircuit.dikdik.config.Config
import io.newcircuit.dikdik.events.Interactions
import io.newcircuit.dikdik.events.Messages
import io.newcircuit.dikdik.models.Command
import io.newcircuit.dikdik.store.StoreController
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.interaction.ApplicationCommandBuilder

class Bot(val config: Config) {
    private val client: DiscordApi = DiscordApiBuilder()
        .setToken(config.token)
        .login().join()
    val commands = ArrayList<Command>()
    val store = StoreController()

    suspend fun start() {
        registerEventListeners()
        registerCommands()
        store.startLoop()
        println("Ready")
    }

    private fun registerEventListeners() {
        val msgEvent = Messages(this)
        val intEvent = Interactions(this)

        client.addListener(msgEvent)
        client.addListener(intEvent)
    }

    private fun registerCommands() {
        val cmds = arrayListOf(
            Joke(this),
            Fact(this),
            TalkIn(this),
            Stop(this),
            Button(this),
            Ask(this),
            CloseVote(this),
        )

        for (command in cmds) {
            val builder = ApplicationCommandBuilder()
                .setName(command.name)
                .setDescription(command.description)
            val options = command.getOptions()
            for (option in options) {
                builder.addOption(option.build())
            }
            println("Registering: ${command.name}")
            builder.createGlobal(client).join()
            this.commands.add(command)
        }
    }
}
