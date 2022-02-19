package top.wsure.guild.official.dtos.operation

import kotlinx.serialization.Serializable
import top.wsure.guild.official.enums.OPCodeEnums

@Serializable
open class Operation(
    @Serializable(with = OPCodeEnums.OperationKSerializer::class)
    val op:OPCodeEnums
)