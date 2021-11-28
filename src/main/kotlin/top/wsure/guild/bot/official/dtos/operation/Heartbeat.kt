package top.wsure.guild.bot.official.dtos.operation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.enums.OPCodeEnums

@Serializable
data class Heartbeat(
    @SerialName("d")
    val count:Long
): Operation(OPCodeEnums.HEARTBEAT)