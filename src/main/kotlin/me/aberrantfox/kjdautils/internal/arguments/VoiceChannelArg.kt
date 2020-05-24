package me.aberrantfox.kjdautils.internal.arguments

import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.kjdautils.internal.command.*
import net.dv8tion.jda.api.entities.*

open class VoiceChannelArg(override val name: String = "VoiceChannel", private val allowsGlobal: Boolean = false) : ArgumentType<VoiceChannel>() {
    companion object : VoiceChannelArg()

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<VoiceChannel> {
        val channel = tryRetrieveSnowflake(event.discord.jda) {
            it.getVoiceChannelById(arg.trimToID())
        } as VoiceChannel? ?: return ArgumentResult.Error("Couldn't retrieve voice channel: $arg")

        if (!allowsGlobal && channel.guild.id != event.guild?.id)
            return ArgumentResult.Error("Voice channel must be from this guild.")

        return ArgumentResult.Success(channel)
    }

    override fun generateExamples(event: CommandEvent<*>): List<String> {
        val channel = event.guild?.channels?.firstOrNull { it.type == ChannelType.VOICE } as? VoiceChannel
        return listOf(channel?.id ?: "582168201979494421")
    }
}