@file:Suppress("unused")

package me.jakejmattson.kutils.api.dsl.configuration

import me.jakejmattson.kutils.api.annotations.BotConfigurationDSL
import me.jakejmattson.kutils.api.dsl.command.*
import me.jakejmattson.kutils.api.dsl.embed.EmbedDSL
import net.dv8tion.jda.api.entities.*

/**
 * @property allowMentionPrefix Allow mentioning the bot to be used as a prefix '@Bot'.
 * @property commandReaction The reaction added to a message when a command is received.
 * @property deleteErrors Whether or not error messages should be deleted over time.
 * @property requiresGuild Whether or not commands are required to be executed in a guild.
 */
data class BotConfiguration(
    internal var prefix: (DiscordContext) -> String = { "+" },
    var allowMentionPrefix: Boolean = false,
    var commandReaction: String? = "\uD83D\uDC40",
    var deleteErrors: Boolean = false,
    var requiresGuild: Boolean = true,
    internal var mentionEmbed: ((DiscordContext) -> MessageEmbed)? = null,
    internal var visibilityPredicate: (command: Command, User, MessageChannel, Guild?) -> Boolean = { _, _, _, _ -> true }
) {
    /**
     * Predicate to dynamically determine the prefix in a given context.
     */
    @BotConfigurationDSL
    fun prefix(construct: (DiscordContext) -> String) {
        prefix = construct
    }

    /**
     * An embed that will be sent anytime someone (solely) mentions the bot.
     */
    @BotConfigurationDSL
    fun mentionEmbed(construct: EmbedDSL.(DiscordContext) -> Unit) {
        mentionEmbed = {
            val embed = EmbedDSL()
            embed.construct(it)
            embed.build()
        }
    }

    /**
     * Function to dynamically determine if a command is visible in a given context.
     *
     * @sample VisibilityContext
     */
    @BotConfigurationDSL
    fun visibilityPredicate(predicate: (VisibilityContext) -> Boolean = { _ -> true }) {
        visibilityPredicate = { command, user, messageChannel, guild ->
            val context = VisibilityContext(command, user, messageChannel, guild)
            predicate.invoke(context)
        }
    }

    /**
     * Block to set global color constants, specifically for embeds.
     *
     * @sample ColorConfiguration
     */
    @BotConfigurationDSL
    fun colors(construct: ColorConfiguration.() -> Unit) {
        val colors = ColorConfiguration()
        colors.construct()
        EmbedDSL.successColor = colors.successColor
        EmbedDSL.failureColor = colors.failureColor
        EmbedDSL.infoColor = colors.infoColor
    }
}