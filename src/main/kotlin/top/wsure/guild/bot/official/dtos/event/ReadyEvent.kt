package top.wsure.guild.bot.official.dtos.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.dtos.User


@Serializable
data class ReadyEvent(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("shard")
    val shard: List<Int>,
    @SerialName("user")
    val user: User,
    @SerialName("version")
    val version: Int
)