package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.channel.ChannelType
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.ApplicationCommandOptionBuilder
import org.javacord.api.interaction.ApplicationCommandOptionType
import org.javacord.api.interaction.Interaction

class Quote(private val bot: Bot) {
    fun run(interaction: Interaction, data: ApplicationCommandInteractionData, opt: String): Pair<Boolean, String> {
        val target = Command.getChannel(interaction, data)
            ?: return Pair(false, "Please provide a text-channel.")

        if (!target.canYouWrite()) {
            return Pair(false, "I can't send messages in that channel.")
        }

        val random = if (opt == "joke")
            bot.config.jokes.random()
        else
            bot.config.facts.random()

        val builder = InteractionMessageBuilder()
        if (target == interaction.channel.get()) {
            builder.setContent(random)
        } else {
            builder.setContent("Sent.")
            target.sendMessage(random).join()
        }
        builder.sendInitialResponse(interaction)


        return Pair(true, "")
    }

    fun getOptions(): ArrayList<ApplicationCommandOptionBuilder> {
        return arrayListOf(
            ApplicationCommandOptionBuilder()
                .setName("channel")
                .setDescription("Optionally send this joke or fact to a text-channel.")
                .setType(ApplicationCommandOptionType.CHANNEL)
                .setRequired(false)
        )
    }
}