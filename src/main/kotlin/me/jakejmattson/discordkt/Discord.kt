@file:Suppress("unused")

package me.jakejmattson.discordkt

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.dsl.diService
import me.jakejmattson.discordkt.internal.listeners.registerInteractionListener
import me.jakejmattson.discordkt.internal.utils.Reflection

/**
 * @property kord A Kord instance used to access the Discord API.
 */
public abstract class Discord {
    public abstract val kord: Kord

    @KordPreview
    internal suspend fun initCore() {
        diService.inject(this)
        Reflection.registerFunctions(this)
        registerListeners(this)
    }

    private fun registerServices() = Reflection.detectClassesWith<Service>().apply { diService.buildAllRecursively(this) }

    @KordPreview
    private suspend fun registerListeners(discord: Discord) {
        registerInteractionListener(discord)
    }

}