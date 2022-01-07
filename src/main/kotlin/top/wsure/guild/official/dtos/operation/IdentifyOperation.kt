package top.wsure.guild.official.dtos.operation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.official.enums.OPCodeEnums

@Serializable
class IdentifyOperation(
    @SerialName("d")
    val d: IdentifyOperationData,
) : Operation(OPCodeEnums.IDENTIFY) {
    constructor(token: String) : this(IdentifyOperationData(token = token))
}

@Serializable
data class IdentifyOperationData(
    @SerialName("intents")
    val intents: Long = 1610612739L,
    @SerialName("properties")
    val properties: Properties = Properties(),
    @SerialName("shard")
    val shard: List<Int> = listOf(0, 1),
    @SerialName("token")
    val token: String
)

@Serializable
data class Properties(
    @SerialName("\$browser")
    val browser: String = "okhttp",
    @SerialName("\$device")
    val device: String = "qq-official-guild-bot by WsureDev",
    @SerialName("\$os")
    val os: String = System.getProperty("os.name").split(" ").firstOrNull() ?: "unknown"
)