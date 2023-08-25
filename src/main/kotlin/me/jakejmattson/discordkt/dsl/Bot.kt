package me.jakejmattson.discordkt.dsl

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.event.interaction.InteractionCreateEvent
import kotlinx.coroutines.runBlocking
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.extensions.*
import me.jakejmattson.discordkt.internal.annotations.ConfigurationDSL
import me.jakejmattson.discordkt.internal.services.InjectionService
import me.jakejmattson.discordkt.internal.utils.InternalLogger
import me.jakejmattson.discordkt.internal.utils.Reflection
import me.jakejmattson.discordkt.internal.utils.ReflectionUtils
import java.io.File

@PublishedApi
internal val diService: InjectionService = InjectionService()

/**
 * Create an instance of your Discord bot! You can use the following blocks to modify bot configuration:
 * [configure][Bot.configure],
 *
 * @param kord A Kord instance used to access the Discord API.
 */
@KordPreview
@ConfigurationDSL
public fun bot(kord: Kord) {
    val packageName = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass.`package`.name

    Reflection = ReflectionUtils(packageName)
    val bot = Bot(kord)

    runBlocking {
        bot.buildBot()
    }
}

/**
 * Backing class for [bot] function.
 */
public class Bot(private val kord: Kord) {

    @KordPreview
    internal suspend fun buildBot() {
        val kord = kord
        val discord = object : Discord() {
            override val kord = kord
        }

        discord.initCore()
    }

    /**
     * Inject objects into the dependency injection pool.
     */
    @ConfigurationDSL
    public fun inject(vararg injectionObjects: Any): Unit = injectionObjects.forEach { diService.inject(it) }

}