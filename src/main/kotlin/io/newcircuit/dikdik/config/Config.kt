package io.newcircuit.dikdik.config


class Config(location: String = "$CONFIG_ROOT/config.yml") {
    val token: String
    val prefix: String
    val jokes: List<String>
    val facts: List<String>
    val whitelist: List<Long>

    init {
        val general = General(location)
        this.token = general.token
        this.prefix = general.prefix
        this.whitelist = general.whitelist
        this.jokes = Quotes(general.jokes).quotes
        this.facts = Quotes(general.facts).quotes
    }
}
