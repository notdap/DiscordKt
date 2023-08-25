@file:Suppress("unused")

package me.jakejmattson.discordkt.dsl

import dev.kord.core.event.Event
import dev.kord.core.on
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.extensions.intentsOf
import me.jakejmattson.discordkt.internal.annotations.BuilderDSL
import me.jakejmattson.discordkt.internal.annotations.InnerDSL
import me.jakejmattson.discordkt.internal.utils.BuilderRegister
import me.jakejmattson.discordkt.internal.utils.InternalLogger
import me.jakejmattson.discordkt.internal.utils.simplerName

/**
 * Create a block for registering listeners.
 *
 * @param construct The builder function.
 */
@BuilderDSL
public fun listeners(construct: ListenerBuilder.() -> Unit): Listeners = Listeners(construct)

/**
 * @suppress Used in DSL
 *
 * @param discord The discord instance.
 */
public data class ListenerBuilder(val discord: Discord) {
    /**
     * Create a new listener.
     */
    @InnerDSL
    public inline fun <reified T : Event> on(crossinline listener: suspend T.() -> Unit) {
        val requiredIntents = intentsOf<T>()
        val intentNames = requiredIntents.values.joinToString { it::class.simpleName!! }

        discord.kord.on<T> {
            try {
                listener(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

/**
 * This is not for you...
 */
public class Listeners(private val collector: ListenerBuilder.() -> Unit) : BuilderRegister {
    /** @suppress */
    override fun register(discord: Discord) {
        collector.invoke(ListenerBuilder(discord))
    }
}