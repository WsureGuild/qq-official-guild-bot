package top.wsure.guild.official.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
class Message(
    @SerialName("id")
    val id: String,
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("guild_id")
    val guildId: String,
    @SerialName("author")
    val author: Author,
    @SerialName("content")
    val content: String? = null,
    @SerialName("mention_everyone")
    val mentionEveryone: Boolean? = false,
    @SerialName("mentions")
    val mentions: List<Author>? = emptyList(),

    @SerialName("attachments")
    val attachments: List<Attachment>? = emptyList(),
    @SerialName("direct_message")
    val directMessage: Boolean? = null,
    @SerialName("member")
    val member: Member? = null,
    @SerialName("seq")
    val seq: Int? = null,
    @SerialName("timestamp")
    @Serializable(with = ZoneDateTimeSerializer::class)
    val timestamp: ZonedDateTime,
    @SerialName("edited_timestamp")
    @Serializable(with = ZoneDateTimeSerializer::class)
    val editedTimestamp: ZonedDateTime? = null,
){
    fun messageContent():String?{
        return content?.replace(Regex("<@!\\d+>"),"")
    }
}

@Serializable
data class Attachment(
    @SerialName("content_type")
    val contentType: String? = null,
    @SerialName("filename")
    val filename: String? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("id")
    val id: String,
    @SerialName("size")
    val size: Int? = null,
    @SerialName("url")
    val url: String,
    @SerialName("width")
    val width: Int? = null
)