package io.newcircuit.dikdik.models

import org.javacord.api.entity.message.Message

data class CommandData(
    val msg: Message,
    val name: String,
    val argsStr: String,
    val args: List<String>,
) {
    companion object {
        fun parse(prefix: String, msg: Message): CommandData {
            val argsStr: String
            val args: List<String>
            val noPrefix = msg.content.substring(
                prefix.length,
            )

            val name = if (noPrefix.contains(' ')) {
                argsStr = noPrefix.substring(
                    noPrefix.indexOf(' ') + 1,
                )
                args = argsStr.split(' ')
                noPrefix.substring(
                    0,
                    noPrefix.indexOf(' '),
                ).lowercase()
            } else {
                argsStr = ""
                args = ArrayList()
                noPrefix
            }

            return CommandData(
                msg = msg,
                name = name,
                argsStr = argsStr,
                args = args,
            )
        }
    }
}
