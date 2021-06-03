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
        var closed = false
        val userId = interaction.user.id
        for (vote in bot.votes.values) {
            if (vote.author == userId) {
                vote.close()
                bot.votes.remove(vote.id)
                closed = true
            }
        }

        return if (closed) {
            InteractionMessageBuilder()
                .setContent("Vote closed.")
                .sendInitialResponse(interaction)
                .join()
            Pair(true, "")
        } else {
            Pair(false, "You don't have an active vote.")
        }
    }
}