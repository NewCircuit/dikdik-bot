package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.Interaction

class Fact(bot: Bot) : Command(
    bot,
    "fact",
    "Send a random fact",
) {
    private val quote = Quote(bot)

    override fun run(
        interaction: Interaction,
        data: ApplicationCommandInteractionData,
    ): Pair<Boolean, String> {
        return quote.run(interaction, "fact")
    }
}
