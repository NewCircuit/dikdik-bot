package io.newcircuit.dikdik.models

import io.newcircuit.dikdik.Bot
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.user.User
import org.javacord.api.interaction.ApplicationCommandInteractionData
import org.javacord.api.interaction.ApplicationCommandOptionBuilder
import org.javacord.api.interaction.Interaction
import java.lang.String.format

abstract class Command(
    val bot: Bot,
    val name: String,
    val description: String,
) {
    fun execute(interaction: Interaction, data: ApplicationCommandInteractionData): Boolean {
        if (this.check(interaction)) {
            return this.run(interaction, data)
        }
        return false
    }

    open fun getOptions(): ArrayList<ApplicationCommandOptionBuilder> {
        return ArrayList()
    }

    protected open fun check(interaction: Interaction): Boolean {
        val serverOpt = interaction.server
        val user = interaction.user
        val server = if (serverOpt.isPresent) {
            serverOpt.get()
        } else {
            null
        }?: return false

        for (role in server.getRoles(user)) {
            if (bot.config.whitelist.contains(role.id)) {
                return true
            }
        }

        return false
    }

    protected abstract fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Boolean
}
