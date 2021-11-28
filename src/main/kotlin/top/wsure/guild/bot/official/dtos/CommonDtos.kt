package top.wsure.guild.bot.official.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.utils.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime


@Serializable
data class User(
    @SerialName("bot")
    val bot: Boolean,
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String
)

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
    @Serializable( with = LocalDateTimeSerializer::class )
    val joinedAt: LocalDateTime,
    @SerialName("roles")
    val roles: List<String>
)
