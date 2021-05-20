package io.newcircuit.dikdik.config

import kotlinx.serialization.Serializable

@Serializable
data class General(
    var token: String,
    var prefix: String,
    var jokes: String,
    var facts: String,
) {
    constructor() : this("bot token here", "^", "default", "default")

    constructor(location: String = "config.yml") : this() {
        val config = getConfig(
            location,
            ref = General(),
            serializer = serializer(),
            deserializer = serializer(),
        )

        this.token = config.token
        this.prefix = config.prefix
        this.jokes = config.jokes
        this.facts = config.facts
    }
}
