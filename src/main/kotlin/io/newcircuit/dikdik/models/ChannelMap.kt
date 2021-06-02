package io.newcircuit.dikdik.models

import org.javacord.api.entity.channel.TextChannel

data class ChannelMap(
    val userID: Long,
    val from: TextChannel,
    val to: TextChannel,
)
