import io.newcircuit.dikdik.Bot
import io.newcircuit.dikdik.config.Config

fun main() {
    val config = Config()
    val bot = Bot(config)

    bot.start()
}
