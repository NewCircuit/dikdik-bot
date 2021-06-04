package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.Interaction

class CloseVote(bot: Bot) : Command(
    bot,
    "closevote",
    "Close your last vote.",
) {
    override fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Pair<Boolean, String> {
        val voteId = interaction.channel.get().id
        val vote = bot.votes[voteId]?: return Pair(false, "There are no active votes in this channel.")
        vote.close()
        bot.votes.remove(vote.id)

        InteractionMessageBuilder()
            .setContent("Vote closed.")
            .sendInitialResponse(interaction)
            .join()
        return Pair(true, "")
    }
}