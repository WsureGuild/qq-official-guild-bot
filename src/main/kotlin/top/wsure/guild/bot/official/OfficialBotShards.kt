package top.wsure.guild.bot.official

import top.wsure.guild.bot.official.dtos.operation.IdentifyConfig
import top.wsure.guild.bot.official.intf.OfficialBotEvent

class OfficialBotShards(
    config: IdentifyConfig,
    officialEvents:List<OfficialBotEvent> = emptyList(),
    wsUrl :String = "wss://api.sgroup.qq.com/websocket",
) {

    init {

    }

}