package io.newcircuit.dikdik.store

import io.newcircuit.dikdik.models.ChannelMap
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
class ChatRelay(
    private val channelMap: HashMap<Long, ChannelMap> = HashMap(),
) : Store<ChatRelay>("./config/channel_map.json") {

    fun create(userId: Long, from: Long, to: Long): ChannelMap {
        val newMap = ChannelMap(userId, from, to)
        channelMap[userId] = newMap

        return newMap
    }

    fun remove(userId: Long) {
        channelMap.remove(userId)
    }

    fun get(userId: Long): ChannelMap? {
        return channelMap[userId]
    }

    override fun generate(file: File) {
        val ref = ChatRelay()
        val serialized = Json.encodeToString(serializer(), ref)

        file.writeText(serialized)
    }

    override fun load(file: File) {
        val data = file.readText()
        val deserialized = Json.decodeFromString(serializer(), data)

        for ((id, map) in deserialized.channelMap) {
            channelMap[id] = map
        }
    }

    override suspend fun save(data: ChatRelay) = coroutineScope {
        val file = File(location)
        val serialized = Json.encodeToString(serializer(), data)

        file.writeText(serialized)
    }
}
