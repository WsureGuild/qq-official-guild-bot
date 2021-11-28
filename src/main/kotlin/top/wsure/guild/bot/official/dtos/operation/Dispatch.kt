package top.wsure.guild.bot.official.dtos.operation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.enums.DispatchEnums

@Serializable
open class Dispatch(
    @SerialName("s")
    val seq: Long,
    @SerialName("t")
    val type: DispatchEnums
): Operation(0)
