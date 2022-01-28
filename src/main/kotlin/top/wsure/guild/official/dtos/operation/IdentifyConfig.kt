package top.wsure.guild.official.dtos.operation

import kotlinx.serialization.Serializable

@Serializable
data class IdentifyConfig(
    val botId: Long,
    val token: String,
    val shards: Int = 1,
    var index: Int = 0,
    val intents: Intents = Intents(),
    val properties: Properties = Properties()
) {
    fun toIdentifyOperationData(): IdentifyOperationData {
        return IdentifyOperationData(
            intents = intents.toIntentsValue(),
            properties = properties,
            shard = mutableListOf(index, shards),
            token = getBotToken()
        )
    }
    fun getBotToken():String{
        return "Bot ${botId}.${token}"
    }
}

@Serializable
data class Intents(
    val guilds: Boolean = true,
    val guildMembers: Boolean = true,
    val messageCreate : Boolean = true,
    val guildMessageReactions : Boolean = true,
    val directMessage: Boolean = true,
    val forumEvent : Boolean = false,
    val audioAction: Boolean = true,
    val atMessages: Boolean = true,
) {
    fun toIntentsValue(): Long {
        return ((if (guilds) 1.shl(0) else 0)
            .or(if (guildMembers) 1.shl(1) else 0)
            .or(if (messageCreate) 1.shl(9) else 0)
            .or(if (guildMessageReactions) 1.shl(10) else 0)
            .or(if (directMessage) 1.shl(12) else 0)
            .or(if (forumEvent) 1.shl(28) else 0)
            .or(if (audioAction) 1.shl(29) else 0)
            .or(if (atMessages) 1.shl(30) else 0))
            .toLong()
    }
}