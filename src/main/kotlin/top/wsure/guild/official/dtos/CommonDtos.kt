package top.wsure.guild.official.dtos

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import top.wsure.guild.official.enums.ReactionTargetEnums
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Serializable
data class User(
    @SerialName("bot")
    val bot: Boolean,
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String
)

@Serializable
data class Author(
    @SerialName("avatar")
    val avatar: String,
    @SerialName("bot")
    val bot: Boolean,
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String
)

@Serializable
data class Member(
    @SerialName("joined_at")
    @Serializable( with = ZoneDateTimeSerializer::class )
    val joinedAt: ZonedDateTime,
    @SerialName("roles")
    val roles: List<String>
)

@Serializable
data class ReactionTarget(
    @SerialName("id")
    val id: String? = null,
    @SerialName("type")
    @Serializable(with = ReactionTargetEnums.ReactionTargetEnumsSerializer::class)
    val type: ReactionTargetEnums? = null
)

object ZoneDateTimeSerializer : KSerializer<ZonedDateTime> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString(), formatter)
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("zonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.format(formatter))
    }
}