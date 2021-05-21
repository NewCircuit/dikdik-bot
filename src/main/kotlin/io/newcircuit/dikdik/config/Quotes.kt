package io.newcircuit.dikdik.config

import kotlinx.serialization.Serializable

@Serializable
internal data class Quotes(var quotes: List<String>) {
    constructor() : this(ArrayList<String>())

    constructor(location: String) : this() {
        val config = getConfig(
            location = location,
            ref = Quotes(),
            serializer = serializer(),
            deserializer = serializer(),
        )

        this.quotes = config.quotes
    }
}
