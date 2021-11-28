package top.wsure.guild.bot.official.intf

import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent

abstract class OfficialBotEvent {

    open suspend fun onAtMessageCreate(data: AtMessageCreateEvent){

    }
    //TODO 官方有许多事件，后续在这里添加事件名称

}