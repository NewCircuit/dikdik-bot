package io.newcircuit.dikdik.models

data class Vote(
    val userId: Long,
    val isYes: Boolean,
) {
    val isNo = !isYes
}