package top.wsure.guild.bot.official.dtos.event
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import top.wsure.guild.bot.official.dtos.User
import top.wsure.guild.bot.official.dtos.operation.Dispatch
import top.wsure.guild.bot.official.enums.DispatchEnums


@Serializable
data class ReadyEvent(
    @SerialName("d")
    val d:Ready
): Dispatch(0, DispatchEnums.READY)

@Serializable
data class Ready(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("shard")
    val shard: List<Int>,
    @SerialName("user")
    val user: User,
    @SerialName("version")
    val version: Int
)