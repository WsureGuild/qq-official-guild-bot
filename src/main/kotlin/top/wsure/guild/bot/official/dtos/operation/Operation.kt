package top.wsure.guild.bot.official.dtos.operation

import top.wsure.guild.bot.official.enums.OPCodeEnums
import kotlinx.serialization.Serializable

@Serializable
open class Operation(
    @Serializable(with = OPCodeEnums.OperationKSerializer::class)
    val op:OPCodeEnums
)