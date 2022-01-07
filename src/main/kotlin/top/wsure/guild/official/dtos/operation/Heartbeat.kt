package top.wsure.guild.official.dtos.operation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.official.enums.OPCodeEnums

@Serializable
data class Heartbeat(
    @SerialName("d")
    val count:Long
): Operation(OPCodeEnums.HEARTBEAT)