package io.newcircuit.dikdik.store

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class StoreController {
    val votes = Votes()

    val clicks = ButtonClicks()

    val channels = ChatRelay()

    init {
        votes.init()
        clicks.init()
        channels.init()
    }

    suspend fun startLoop() = coroutineScope {
        while (true) {
            delay(10 * 1000L)
            votes.save(votes)
            clicks.save(clicks)
            channels.save(channels)
        }
    }
}
