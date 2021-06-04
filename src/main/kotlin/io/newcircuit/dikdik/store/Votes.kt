package io.newcircuit.dikdik.store

import io.newcircuit.dikdik.models.Vote
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.javacord.api.interaction.Interaction
import java.io.File

@Serializable
class Votes(
    private val votes: HashMap<Long, Vote> = HashMap()
) : Store<Votes>("./config/votes.json") {

    fun addVote(vote: Vote): Boolean {
        return if (votes.containsKey(vote.id)) {
            false
        } else {
            votes[vote.id] = vote
            true
        }
    }

    fun get(channelId: Long): Vote? = votes[channelId]

    fun close(channelId: Long): Boolean {
        val vote = votes[channelId] ?: return false
        vote.close()
        votes.remove(channelId)
        return true
    }

    override fun generate(file: File) {
        val ref = Votes()
        val serialized = Json.encodeToString(serializer(), ref)

        file.writeText(serialized)
    }

    override fun load(file: File) {
        val data = file.readText()
        val deserialized = Json.decodeFromString(serializer(), data)

        for ((id, vote) in deserialized.votes) {
            this.votes[id] = vote
        }
    }

    override suspend fun save(data: Votes) = coroutineScope {
        val file = File(location)
        val serialized = Json.encodeToString(serializer(), data)

        file.writeText(serialized)
    }
}
