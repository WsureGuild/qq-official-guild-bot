package top.wsure.guild.official.dtos.event.reactions
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import top.wsure.guild.official.dtos.ReactionTarget


@Serializable
data class MessageReactionEvent(
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("emoji")
    val emoji: ReactionTarget,
    @SerialName("guild_id")
    val guildId: String,
    @SerialName("target")
    val target: ReactionTarget,
    @SerialName("user_id")
    val userId: String
)