package top.wsure.guild.official.dtos.event.guilds

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GuildEvent(
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("id")
    val id: String,
    @SerialName("joined_at")
    val joinedAt: String,
    @SerialName("max_members")
    val maxMembers: Int,
    @SerialName("member_count")
    val memberCount: Int,
    @SerialName("name")
    val name: String,
    @SerialName("op_user_id")
    val opUserId: String,
    @SerialName("owner_id")
    val ownerId: String
)