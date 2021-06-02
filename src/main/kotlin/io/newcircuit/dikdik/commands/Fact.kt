package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.ApplicationCommandOptionBuilder
import org.javacord.api.interaction.ApplicationCommandOptionType
import org.javacord.api.interaction.Interaction
import kotlin.text.Typography.quote

class Fact(bot: Bot) : Command(
    bot,
    "fact",
    "Send a random fact",
) {
    private val quote = Quote(bot)

    override fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Pair<Boolean, String> {
        return quote.run(interaction, data, "fact")
    }

    override fun getOptions(): ArrayList<ApplicationCommandOptionBuilder> {
        return quote.getOptions()
    }
}
