package top.wsure.guild.bot.official.dtos.operation

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class Resume(
    val d:ResumeData
):Operation(6)

@Serializable
data class ResumeData(
    @SerialName("seq")
    val seq: Long,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("token")
    val token: String
)