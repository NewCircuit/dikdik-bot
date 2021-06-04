package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.interaction.Interaction

class Quote(private val bot: Bot) {
    fun run(interaction: Interaction, opt: String): Pair<Boolean, String> {
        val random = if (opt == "joke")
            bot.config.jokes.random()
        else
            bot.config.facts.random()

        InteractionMessageBuilder()
            .setContent(random)
            .sendInitialResponse(interaction)
            .join()

        return Pair(true, "")
    }
}