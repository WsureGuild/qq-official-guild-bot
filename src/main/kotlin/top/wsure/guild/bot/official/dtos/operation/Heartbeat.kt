package top.wsure.guild.bot.official.dtos.operation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// OP Heartbeat
@Serializable
data class HeartbeatDto(
    @SerialName("d")
    val count:Long
): Operation(op = 1)