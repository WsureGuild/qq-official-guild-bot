package top.wsure.guild.bot.official.dtos.operation

import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.enums.OPCodeEnums

@Serializable
open class Operation(
    @Serializable(with = OPCodeEnums.OperationKSerializer::class)
    val op:OPCodeEnums
)