package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import io.newcircuit.dikdik.models.Question
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.ApplicationCommandOptionBuilder
import org.javacord.api.interaction.ApplicationCommandOptionType
import org.javacord.api.interaction.Interaction

class Ask(bot: Bot): Command(
    bot,
    "ask",
    "Ask a yes or no question!"
) {
    override fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Pair<Boolean, String> {
        val option = if (data.options.size > 0) {
            data.options[0]
        } else {
            null
        }?: return Pair(false, "Please provide a question")

        val questionStr = option.stringValue.get()
        val question = Question(
            interaction,
            questionStr,
        )

        for (vote in bot.votes.values) {
            if (vote.author == question.author) {
                return Pair(false, "You already have an active vote.")
            }
        }

        bot.votes[interaction.id] = question
        val msg = question.getIBuilder()
        msg.sendInitialResponse(interaction).join()

        return Pair(true, "")
    }

    override fun getOptions(): ArrayList<ApplicationCommandOptionBuilder> {
        return arrayListOf(
            ApplicationCommandOptionBuilder()
                .setName("question")
                .setType(ApplicationCommandOptionType.STRING)
                .setDescription("The question")
                .setRequired(true)
        )
    }
}
