package top.wsure.guild.bot.official.dtos.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.dtos.*
import top.wsure.guild.bot.official.dtos.operation.Dispatch
import top.wsure.guild.bot.official.enums.DispatchEnums

@Serializable
data class AtMessageCreateEvent(
    @SerialName("d")
    val d: AtMessageCreateData,
): Dispatch(0, DispatchEnums.AT_MESSAGE_CREATE)

@Serializable
data class AtMessageCreateData(
    @SerialName("author")
    val author: Author,
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("content")
    val content: String,
    @SerialName("guild_id")
    val guildId: String,
    @SerialName("id")
    val id: String,
    @SerialName("member")
    val member: Member,
    @SerialName("mentions")
    val mentions: List<Author>,
    @SerialName("timestamp")
    val timestamp: String
){
    fun messageContent():String{
        return content.replace(Regex("<@!\\d+>"),"")
    }
}