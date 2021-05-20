package io.newcircuit.dikdik.config


class Config(location: String = "./config.yml") {
    val token: String
    val prefix: String
    val jokes: List<String>
    val facts: List<String>

    init {
        val general = General(location)
        this.token = general.token
        this.prefix = general.prefix

        val jokesPath: String = if (general.jokes == "default") {
            val jokesURL = javaClass.getResource("/jokes.yml")
            if (jokesURL === null) {
                throw Exception("Missing default jokes yml file.")
            }
            jokesURL.path
        } else {
            general.jokes
        }

        val factsPath: String = if (general.facts == "default") {
            val factsURL = javaClass.getResource("/facts.yml")
            if (factsURL === null) {
                throw Exception("Missing default facts yml file.")
            }
            factsURL.path
        } else {
            general.facts
        }

        this.jokes = Quotes(jokesPath).quotes
        this.facts = Quotes(factsPath).quotes
    }
}
