package io.newcircuit.dikdik.models

import kotlinx.serialization.Serializable
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.component.ActionRowBuilder
import org.javacord.api.entity.message.component.ButtonBuilder
import org.javacord.api.entity.message.component.ButtonStyle
import org.javacord.api.interaction.Interaction

@Serializable
class Vote(
    val id: Long,
    private val message: String,
    private val entries: ArrayList<SubmittedVote> = ArrayList()
) {
    fun addEntry(interaction: Interaction, userId: Long, opt: Boolean) {
        val newEntry = SubmittedVote(userId, opt)

        for (entry in entries) {
            if (entry.userId == userId) {
                entries.remove(newEntry)
                // this means they're removing their vote
                if (newEntry.isYes == entry.isYes) {
                    update(interaction)
                    return
                }
                break
            }
        }

        entries.add(newEntry)
        update(interaction)
    }

    fun close(interaction: Interaction) {
        val iBuilder = getIBuilder(true, interaction)

        iBuilder.setContent("$message (closed)")
            .update(interaction)
            .join()
    }

    fun getIBuilder(
        toDisable: Boolean = false,
        interaction: Interaction? = null,
    ): InteractionMessageBuilder {
        val components = getComponents(toDisable, interaction)
        return InteractionMessageBuilder()
            .setContent(message)
            .addComponent(components)
    }

    private fun update(interaction: Interaction) {
        val iBuilder = getIBuilder()

        iBuilder.update(interaction).join()
    }

    private fun countVotes(): Pair<Int, Int> {
        var yes = 0
        var no = 0

        entries.forEach { vote ->
            if (vote.isYes) {
                yes += 1
            } else {
                no += 1
            }
        }

        return Pair(yes, no)
    }

    private fun getComponents(
        toDisable: Boolean = false,
        interaction: Interaction? = null,
    ): ActionRowBuilder {
        val (yes, no) = countVotes()
        val close = ButtonBuilder()
            .setLabel("Close")
            .setCustomId("vote_close")
            .setDisabled(toDisable)
            .setStyle(ButtonStyle.SECONDARY)
        if (toDisable && interaction != null) {
            val user = interaction.user
            close.setLabel("Closed by ${user.discriminatedName}")
        } else {
            close.setLabel("Close")
        }

        return ActionRowBuilder()
            .addComponent(
                ButtonBuilder()
                    .setLabel("Yes: $yes")
                    .setCustomId("vote_yes")
                    .setDisabled(toDisable)
                    .setStyle(ButtonStyle.SUCCESS)
            )
            .addComponent(
                ButtonBuilder()
                    .setLabel("No: $no")
                    .setCustomId("vote_no")
                    .setDisabled(toDisable)
                    .setStyle(ButtonStyle.DANGER)
            )
            .addComponent(close)
    }
}