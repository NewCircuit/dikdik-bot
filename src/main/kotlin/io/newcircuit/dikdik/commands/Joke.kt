package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.Interaction

class Joke(bot: Bot) : Command(
    bot,
    "joke",
    "Send a random joke",
) {
    private val quote = Quote(bot)

    override fun run(
        interaction: Interaction,
        data: ApplicationCommandInteractionData,
    ): Pair<Boolean, String> {
        return quote.run(interaction, "joke")
    }
}
