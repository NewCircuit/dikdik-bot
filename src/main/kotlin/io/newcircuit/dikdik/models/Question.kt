package io.newcircuit.dikdik.models

import org.javacord.api.entity.message.InteractionMessageBuilder
import org.javacord.api.entity.message.component.ActionRowBuilder
import org.javacord.api.entity.message.component.ButtonBuilder
import org.javacord.api.entity.message.component.ButtonStyle
import org.javacord.api.interaction.Interaction

class Question(
    val interaction: Interaction,
    val message: String,
) {
    val id = interaction.id
    val author = interaction.user.id
    private val votes: ArrayList<Vote> = ArrayList()

    fun addVote(userId: Long, isYes: Boolean) {
        val newVote = Vote(userId, isYes)

        for (vote in votes) {
            if (vote.userId == userId) {
                votes.remove(vote)
                // this means they're removing their vote
                if (vote.isYes == vote.isYes || vote.isNo == vote.isNo) {
                    update()
                    return
                }
                break
            }
        }

        votes.add(newVote)
        update()
    }

    fun close() {
        val iBuilder = getIBuilder(true)

        iBuilder.editOriginalResponse(interaction)
            .join()
    }

    fun getIBuilder(toDisable: Boolean = false): InteractionMessageBuilder {
        val components = getComponents(toDisable)
        return InteractionMessageBuilder()
            .setContent(message)
            .addComponent(components)
    }

    fun update() {
        val iBuilder = getIBuilder()

        iBuilder.editOriginalResponse(interaction).join()
    }

    private fun countVotes(): Pair<Int, Int> {
        var yes = 0
        var no = 0

        votes.forEach { vote ->
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
                    .setCustomId("vote_yes")
                    .setDisabled(toDisable)
                    .setStyle(ButtonStyle.DANGER)
            )
    }
}