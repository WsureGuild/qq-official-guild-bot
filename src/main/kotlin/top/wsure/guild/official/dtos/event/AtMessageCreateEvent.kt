package top.wsure.guild.official.dtos.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.official.dtos.Author
import top.wsure.guild.official.dtos.LocalDateTimeSerializer
import top.wsure.guild.official.dtos.Member
import java.time.LocalDateTime

@Serializable
data class AtMessageCreateEvent(
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
    @Serializable(with = LocalDateTimeSerializer::class)
    val timestamp: LocalDateTime
){
    fun messageContent():String{
        return content.replace(Regex("<@!\\d+>"),"")
    }
}