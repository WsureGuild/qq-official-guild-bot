package top.wsure.guild.bot.official.dtos.operation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.enums.OPCodeEnums


@Serializable
data class Resume(
    val d:ResumeData
):Operation(OPCodeEnums.RESUME)

@Serializable
data class ResumeData(
    @SerialName("seq")
    val seq: Long,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("token")
    val token: String
)