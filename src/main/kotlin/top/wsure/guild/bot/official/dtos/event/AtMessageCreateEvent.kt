package top.wsure.guild.bot.official.dtos.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.dtos.Author
import top.wsure.guild.bot.official.dtos.Member
import top.wsure.guild.bot.utils.serializer.LocalDateTimeSerializer
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