package top.wsure.guild.bot.official.dtos.operation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.enums.DispatchEnums
import top.wsure.guild.bot.official.enums.OPCodeEnums

@Serializable
open class Dispatch<T>(
    @SerialName("s")
    val seq: Long,
    @SerialName("t")
    val type: DispatchEnums,
    @SerialName("d")
    val d:T
):Operation(OPCodeEnums.DISPATCH)

@Serializable
data class DispatchType(
    @SerialName("t")
    val type: DispatchEnums,
    @SerialName("s")
    val seq: Long,
)