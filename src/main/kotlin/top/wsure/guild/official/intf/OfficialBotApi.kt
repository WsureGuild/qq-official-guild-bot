package top.wsure.guild.official.intf

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.wsure.guild.common.sender.Sender
import top.wsure.guild.official.dtos.api.Role
import top.wsure.guild.official.dtos.api.RolesApiRes
import top.wsure.guild.official.dtos.api.TextMessage
import top.wsure.guild.common.utils.JsonUtils.jsonToObjectOrNull
import top.wsure.guild.common.utils.JsonUtils.objectToJson
import top.wsure.guild.common.utils.OkHttpUtils

class OfficialBotApi(private val token: String): Sender {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val roles = "https://api.sgroup.qq.com/guilds/{{guild_id}}/roles"
    private val editRole = "https://api.sgroup.qq.com/guilds/{{guild_id}}/members/{{user_id}}/roles/{{role_id}}"
    private val sendMessage = "https://api.sgroup.qq.com/channels/{{channel_id}}/messages"

    private fun officeApiHeader(): MutableMap<String, String> {
        return mutableMapOf(
            "Authorization" to token
        )
    }

    fun reply(channelId:String,id:String, msg: String): Boolean {
        val url = sendMessage.replace("{{channel_id}}", channelId)
        val json = TextMessage(msg, id).objectToJson()
        val header = officeApiHeader()
        val res = OkHttpUtils.postJson(url, OkHttpUtils.addJson(json), header)
        logger.info("reply msg:$msg url:$url res: $res")
        return true
    }

    fun getRoles(guildId: String): List<Role> {
        val url = roles.replace("{{guild_id}}", guildId)
        val rolesApiRes = OkHttpUtils.getJson(url, officeApiHeader()).jsonToObjectOrNull<RolesApiRes>()
        logger.info("roles $url res:{}", rolesApiRes)
        return rolesApiRes?.roles ?: emptyList()
    }

    fun addRoles(guildId: String, userId: String, roleId: String): Boolean {
        val url = editRole.replace("{{guild_id}}", guildId)
            .replace("{{user_id}}", userId)
            .replace("{{role_id}}", roleId)
        val res = OkHttpUtils.put(url, emptyMap(), officeApiHeader())
        logger.info("addRoles $url res:${res.isSuccessful}")
        return res.isSuccessful
    }

    fun delRoles(guildId: String, userId: String, roleId: String): Boolean {
        val url = editRole.replace("{{guild_id}}", guildId)
            .replace("{{user_id}}", userId)
            .replace("{{role_id}}", roleId)
        val res = OkHttpUtils.delete(url, emptyMap(), officeApiHeader())
        logger.info("delRoles $url res:${res.isSuccessful}")
        return res.isSuccessful
    }
}