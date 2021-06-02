package io.newcircuit.dikdik.config

import kotlinx.serialization.Serializable

@Serializable
internal data class General(
    var token: String,
    var prefix: String,
    var jokes: String,
    var facts: String,
    var whitelist: List<Long>,
) {
    constructor() : this("", "^", "$CONFIG_ROOT/jokes.yml", "$CONFIG_ROOT/facts.yml", listOf())

    constructor(location: String = "$CONFIG_ROOT/config.yml") : this() {
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
        this.whitelist = config.whitelist
    }
}
