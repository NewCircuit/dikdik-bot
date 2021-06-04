package io.newcircuit.dikdik.models

import kotlinx.serialization.Serializable

@Serializable
data class ChannelMap(
    val userID: Long,
    val from: Long,
    val to: Long,
)
