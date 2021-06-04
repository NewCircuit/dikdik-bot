package io.newcircuit.dikdik.commands

import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.models.Command
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.Interaction

class Stop(bot: Bot) : Command(
    bot,
    "stop",
    "Stop talking in a channel",
) {
    override fun run(
        interaction: Interaction,
        data: ApplicationCommandInteractionData,
    ): Pair<Boolean, String> {
        val user = interaction.user
        bot.store.channels.get(user.id)
            ?: return Pair(false, "Not talking in any other channels")

        bot.store.channels.remove(user.id)

        InteractionMessageBuilder()
            .setContent("Done.")
            .setFlags(MessageFlag.EPHEMERAL)
            .sendInitialResponse(interaction)
            .join()

        return Pair(true, "")
    }
}
