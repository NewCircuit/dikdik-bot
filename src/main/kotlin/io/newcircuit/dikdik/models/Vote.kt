package io.newcircuit.dikdik.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.component.ActionRowBuilder
import org.javacord.api.entity.message.component.ButtonBuilder
import org.javacord.api.entity.message.component.ButtonStyle
import org.javacord.api.interaction.Interaction

@Serializable
class Vote(
    @Transient
    val interaction: Interaction? = null,
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

    fun close() {
        val iBuilder = getIBuilder(true)

        iBuilder.setContent("$message (closed)")
            .editOriginalResponse(interaction)
            .join()
    }

    fun getIBuilder(toDisable: Boolean = false): InteractionMessageBuilder {
        val components = getComponents(toDisable)
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

    private fun getComponents(toDisable: Boolean = false): ActionRowBuilder {
        val (yes, no) = countVotes()
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
    }
}