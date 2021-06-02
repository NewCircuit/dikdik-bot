package io.newcircuit.dikdik.models

import io.newcircuit.dikdik.Bot
import org.javacord.api.entity.channel.ChannelType
import org.javacord.api.entity.channel.TextChannel
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
    fun execute(interaction: Interaction, data: ApplicationCommandInteractionData): Pair<Boolean, String> {
        val (check, reason) = this.check(interaction)
        if (check) {
            return this.run(interaction, data)
        }
        return Pair(check, reason)
    }

    companion object {
        fun getChannel(interaction: Interaction, data: ApplicationCommandInteractionData): TextChannel? {
            if (data.options.size == 0) {
                return interaction.channel.get()
            }

            for (option in data.options) {
                if (option.name != "channel") {
                    continue
                }
                val api = interaction.api
                val id = option.stringValue.get().toLong()
                val channel = api.getChannelById(id).get()

                if (channel.type != ChannelType.SERVER_TEXT_CHANNEL) {
                    return null
                }

                return channel.asServerTextChannel().get()
            }

            return null
        }
    }

    open fun getOptions(): ArrayList<ApplicationCommandOptionBuilder> {
        return ArrayList()
    }

    protected open fun check(interaction: Interaction): Pair<Boolean, String> {
        val serverOpt = interaction.server
        val user = interaction.user
        val server = if (serverOpt.isPresent) {
            serverOpt.get()
        } else {
            null
        }?: return Pair(false, "This is a guild-only command.")

        for (role in server.getRoles(user)) {
            if (bot.config.whitelist.contains(role.id)) {
                return Pair(true, "")
            }
        }

        return Pair(false, "You don't have permission to run this command.")
    }

    protected abstract fun run(interaction: Interaction, data: ApplicationCommandInteractionData): Pair<Boolean, String>
}
