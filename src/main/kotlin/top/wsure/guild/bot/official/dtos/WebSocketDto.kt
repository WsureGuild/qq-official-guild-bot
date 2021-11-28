package top.wsure.guild.bot.official.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Author(
    @SerialName("avatar")
    val avatar: String,
    @SerialName("bot")
    val bot: Boolean,
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String
)

@Serializable
data class Member(
    @SerialName("joined_at")
    val joinedAt: String? = null,
    @SerialName("roles")
    val roles: List<String>? = null
)
