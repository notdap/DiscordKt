package me.jakejmattson.kutils.api.arguments

import me.jakejmattson.kutils.api.dsl.arguments.*
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import me.jakejmattson.kutils.api.extensions.stdlib.trimToID
import net.dv8tion.jda.api.entities.Category

/**
 * Accepts a Discord Category entity as an ID, a mention, or by name.
 *
 * @param guildId The guild ID used to determine which guild to search in.
 * @param allowsGlobal Whether or not this entity can be retrieved from outside this guild.
 */
open class CategoryArg(override val name: String = "Category", private val guildId: String = "", private val allowsGlobal: Boolean = false) : ArgumentType<Category>() {
    /**
     * Accepts a Discord Category entity as an ID, a mention, or by name from within this guild.
     */
    companion object : CategoryArg()

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Category> {
        val resolvedGuildId = guildId.ifBlank { event.guild?.id }.takeUnless { it.isNullOrBlank() }

        if (arg.trimToID().toLongOrNull() != null) {
            val category = event.discord.jda.getCategoryById(arg.trimToID())

            if (!allowsGlobal && resolvedGuildId != category?.guild?.id)
                return Error("$name must be from this guild.")

            if (category != null)
                return Success(category)
        }

        resolvedGuildId
            ?: return Error("Cannot resolve a category by name from a DM. Please invoke in a guild or use an ID.")

        val guild = event.discord.jda.getGuildById(resolvedGuildId)
            ?: return Error("$name could not determine a guild to search in.")
        val argString = args.joinToString(" ").toLowerCase()
        val viableNames = guild.categories
            .filter { argString.startsWith(it.name.toLowerCase()) }
            .sortedBy { it.name.length }

        val longestMatch = viableNames.lastOrNull()?.takeUnless { it.name.length < arg.length }
        val result = longestMatch?.let { viableNames.filter { it.name == longestMatch.name } } ?: emptyList()

        return when (result.size) {
            0 -> Error("Could not resolve any categories by name.")
            1 -> {
                val category = result.first()
                val argList = args.take(category.name.split(" ").size)
                Success(category, argList.size)
            }
            else -> Error("Resolving category by name returned multiple matches. Please use an ID.")
        }
    }

    override fun generateExamples(event: CommandEvent<*>) = event.guild?.categories?.map { it.id }
        ?: listOf("Chat Channels")
}