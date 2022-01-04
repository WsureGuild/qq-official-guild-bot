package top.wsure.guild.bot.official.dtos.event.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ChannelEvent(
    @SerialName("guild_id")
    val guildId: String,
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("op_user_id")
    val opUserId: String,
    @SerialName("owner_id")
    val ownerId: String,
    @SerialName("sub_type")
    val subType: Int,
    @SerialName("type")
    val type: Int
)