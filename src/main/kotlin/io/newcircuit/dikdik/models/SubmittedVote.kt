package io.newcircuit.dikdik.models

import kotlinx.serialization.Serializable

@Serializable
data class SubmittedVote(
    val userId: Long,
    val option: Boolean,
) {
    val isYes = option
}
