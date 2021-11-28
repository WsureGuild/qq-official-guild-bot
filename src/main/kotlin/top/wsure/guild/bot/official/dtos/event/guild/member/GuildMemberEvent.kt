package top.wsure.guild.bot.official.dtos.event.guild.member
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import top.wsure.guild.bot.official.dtos.User
import top.wsure.guild.bot.official.dtos.operation.Dispatch
import top.wsure.guild.bot.official.enums.DispatchEnums

@Serializable
data class GuildMemberEvent(
    @SerialName("guild_id")
    val guildId: String,
    @SerialName("joined_at")
    val joinedAt: String,
    @SerialName("nick")
    val nick: String,
    @SerialName("op_user_id")
    val opUserId: String,
    @SerialName("roles")
    val roles: List<String>,
    @SerialName("user")
    val user: User
)

