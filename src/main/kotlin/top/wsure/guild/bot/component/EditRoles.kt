package top.wsure.guild.bot.component


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.wsure.guild.bot.official.dtos.OfficialCode.messageContent
import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent
import top.wsure.guild.bot.official.intf.OfficialBotApi
import top.wsure.guild.bot.official.intf.OfficialBotApi.reply
import top.wsure.guild.bot.official.intf.OfficialBotEvent
import top.wsure.guild.bot.utils.ac.AhoCorasickMatcher

class EditRoles(private val token:String): OfficialBotEvent() {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override suspend fun onAtMessageCreate(data: AtMessageCreateEvent) {
            val message = data.content
            val guildRoles = OfficialBotApi.getRoles(token,data.guildId)
            //一个自己做的ac自动机，用作字符串匹配
            val acMatcher = AhoCorasickMatcher(guildRoles){it.name}
            //在 剔除了at 和 频道连接之后的消息中搜索角色名
            val roles = acMatcher.search(message.messageContent())
            if(roles.isNotEmpty())
            {
                val isDelete = message.contains("删除")
                logger.info("{} ${if(isDelete) "del" else "add"} roles :{}",data.author,roles)
                val successRoles = roles.filter { if(isDelete) OfficialBotApi.delRoles(token,data.guildId,data.author.id,it.id)
                else OfficialBotApi.addRoles(token,data.guildId,data.author.id,it.id) }
                val msg = "已经为${data.author.username}${if(isDelete) "删除" else "设置"}了身份${successRoles.joinToString { it.name }}"
                logger.info(" EditRoles success ,msg:$msg atMsg:$data")

                kotlin.runCatching { data.reply(token,msg) }.onFailure {
                    it.printStackTrace()
                }
            }


    }
}