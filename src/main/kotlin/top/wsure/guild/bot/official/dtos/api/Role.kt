package top.wsure.guild.bot.official.dtos.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RolesApiRes(
    @SerialName("guild_id")
    val guildId: String,
    @SerialName("role_num_limit")
    val roleNumLimit: String,
    @SerialName("roles")
    val roles: List<Role>
)

@Serializable
data class Role(
    @SerialName("color")
    val color: Long,
    @SerialName("hoist")
    val hoist: Int,
    @SerialName("id")
    val id: String,
    @SerialName("member_limit")
    val memberLimit: Int,
    @SerialName("name")
    val name: String,
    @SerialName("number")
    val number: Int
)