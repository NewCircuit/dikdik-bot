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
        val newVote = Question(
            interaction,
            questionStr,
        )

        for (vote in bot.votes.values) {
            if (vote.id == newVote.id) {
                return Pair(false, "There's already an active vote in this channel.")
            }
        }

        bot.votes[newVote.id] = newVote
        val msg = newVote.getIBuilder()
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
