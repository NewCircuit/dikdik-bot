package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.ApplicationCommandOptionBuilder
import org.javacord.api.interaction.ApplicationCommandOptionType
import org.javacord.api.interaction.Interaction

class Quote(private val bot: Bot) {
    fun run(interaction: Interaction, data: ApplicationCommandInteractionData, opt: String): Boolean {
        val target = if (data.options.size == 0) {
            interaction.channel.get()
        } else {
            val api = interaction.api
            val option = data.options[0]
            val id = option.stringValue.get().toLong()
            api.getTextChannelById(id).get()
        }

        if (!target.canYouWrite()) {
            return false
        }

        val random = if (opt == "joke")
            bot.config.jokes.random()
        else
            bot.config.facts.random()

        InteractionMessageBuilder()
            .setContent("Sent.")
            .sendInitialResponse(interaction)
            .join()
            .deleteInitialResponse(interaction)

        target.sendMessage(random).join()

        return true
    }

    fun getOptions(): ArrayList<ApplicationCommandOptionBuilder> {
        return arrayListOf(
            ApplicationCommandOptionBuilder()
                .setName("channel")
                .setDescription("Optionally send this joke or fact to a channel.")
                .setType(ApplicationCommandOptionType.CHANNEL)
                .setRequired(false)
        )
    }
}