import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.config.Config
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val config = Config()
    val bot = Bot(config)

    bot.start()
}
