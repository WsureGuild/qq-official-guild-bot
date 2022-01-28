package top.wsure.guild.official.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

@Serializable
enum class ReactionTargetEnums(val value:Int,val desc:String) {
    /*
0	消息
1	帖子
2	评论
3	回复
     */
    MESSAGE(0,"消息"),
    FORUM(1,"帖子"),
    COMMENTS(2,"评论"),
    RESPONSE(3,"回复"),
    ;

    companion object{
        private val codeMap = values().associateBy { it.value }
        fun getOPCodeByCode(code: Int): ReactionTargetEnums {
            return Optional.ofNullable(codeMap[code]).orElseThrow()
        }
    }
    object ReactionTargetEnumsSerializer: KSerializer<ReactionTargetEnums> {
        override fun deserialize(decoder: Decoder): ReactionTargetEnums {
            return getOPCodeByCode(decoder.decodeInt())
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("reactionTargetEnums", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, value: ReactionTargetEnums) {
            encoder.encodeInt(value.value)
        }
    }
}
