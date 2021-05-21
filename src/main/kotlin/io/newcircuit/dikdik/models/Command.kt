package io.newcircuit.dikdik.models

import io.newcircuit.dikdik.Bot
import org.javacord.api.entity.permission.PermissionType
import java.lang.String.format

abstract class Command(
    val bot: Bot,
    val name: String,
    val alias: String? = null,
    val description: String,
    val example: String,
) {
    fun execute(cmd: CommandData): Boolean {
        if (this.check(cmd)) {
            return this.run(cmd)
        }
        return false
    }

    protected open fun check(cmd: CommandData): Boolean {
        if (cmd.msg.server.isEmpty) {
            return false
        }
        val server = cmd.msg.server.get()
        val perms = server.getAllowedPermissions(cmd.msg.author.asUser().get())

        return perms.contains(PermissionType.VIEW_AUDIT_LOG)
    }

    protected abstract fun run(cmd: CommandData): Boolean

    override fun toString(): String {
        return if (alias == null)
            format("%s: %s\n * `%s`", name, description, example)
        else
            format("%s or %s: %s\n * `%s`", name, alias, description, example)
    }

    fun matches(cmd: CommandData): Boolean {
        return this.name == cmd.name
                || this.alias == cmd.name
    }
}
