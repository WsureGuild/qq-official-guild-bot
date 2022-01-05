package top.wsure.guild.bot.official.dtos.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.wsure.guild.bot.official.dtos.Author
import top.wsure.guild.bot.official.dtos.Member


@Serializable
data class MessageCreate(
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
    @SerialName("timestamp")
    val timestamp: String
)