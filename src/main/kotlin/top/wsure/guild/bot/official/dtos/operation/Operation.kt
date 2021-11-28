package top.wsure.guild.bot.official.dtos.operation

import top.wsure.guild.bot.official.enums.OPCodeEnums
import kotlinx.serialization.Serializable

@Serializable
open class Operation(
    val op:Int
){
    constructor(op: OPCodeEnums):this(op.code)
    fun type(): OPCodeEnums {
        return OPCodeEnums.getOPCodeByCode(op)
    }
}