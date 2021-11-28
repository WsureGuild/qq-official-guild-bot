package top.wsure.guild.bot.official.intf

import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.wsure.guild.bot.official.dtos.api.Role
import top.wsure.guild.bot.official.dtos.api.RolesApiRes
import top.wsure.guild.bot.official.dtos.api.TextMessage
import top.wsure.guild.bot.utils.JsonUtils.jsonToObjectOrNull
import top.wsure.guild.bot.utils.JsonUtils.objectToJson
import top.wsure.guild.bot.utils.OkHttpUtils

object OfficialBotApi {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private const val roles = "https://api.sgroup.qq.com/guilds/{{guild_id}}/roles"
    private const val editRole = "https://api.sgroup.qq.com/guilds/{{guild_id}}/members/{{user_id}}/roles/{{role_id}}"
    private const val sendMessage = "https://api.sgroup.qq.com/channels/{{channel_id}}/messages"

    private fun officeApiHeader(token: String): MutableMap<String, String> {
        return mutableMapOf(
            "Authorization" to token
        )
    }

    fun AtMessageCreateEvent.reply(botName: String, msg: String): Boolean {
        val url = sendMessage.replace("{{channel_id}}", this.d.channelId)
        val json = TextMessage(msg, this.d.id).objectToJson()
        val header = officeApiHeader(botName)
        val res = OkHttpUtils.postJson(url, OkHttpUtils.addJson(json), header)
        logger.info("reply msg:$msg url:$url res: $res")
        return true
    }

    fun getRoles(token: String, guildId: String): List<Role> {
        val url = roles.replace("{{guild_id}}", guildId)
        val rolesApiRes = OkHttpUtils.getJson(url, officeApiHeader(token)).jsonToObjectOrNull<RolesApiRes>()
        logger.info("roles $url res:{}", rolesApiRes)
        return rolesApiRes?.roles ?: emptyList()
    }

    fun addRoles(token: String, guildId: String, userId: String, roleId: String): Boolean {
        val url = editRole.replace("{{guild_id}}", guildId)
            .replace("{{user_id}}", userId)
            .replace("{{role_id}}", roleId)
        val res = OkHttpUtils.put(url, emptyMap(), officeApiHeader(token))
        logger.info("addRoles $url res:${res.isSuccessful}")
        return res.isSuccessful
    }

    fun delRoles(token: String, guildId: String, userId: String, roleId: String): Boolean {
        val url = editRole.replace("{{guild_id}}", guildId)
            .replace("{{user_id}}", userId)
            .replace("{{role_id}}", roleId)
        val res = OkHttpUtils.delete(url, emptyMap(), officeApiHeader(token))
        logger.info("delRoles $url res:${res.isSuccessful}")
        return res.isSuccessful
    }
}