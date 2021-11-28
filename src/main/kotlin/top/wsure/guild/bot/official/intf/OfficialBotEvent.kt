package top.wsure.guild.bot.official.intf

import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent
import top.wsure.guild.bot.official.dtos.event.ReadyEvent
import top.wsure.guild.bot.official.dtos.event.guild.member.GuildMemberEvent

abstract class OfficialBotEvent {
    open suspend fun onReady(data: ReadyEvent){

    }

    open suspend fun onGuildMemberAdd(data: GuildMemberEvent){

    }

    open suspend fun onGuildMemberUpdate(data: GuildMemberEvent){

    }

    open suspend fun onGuildMemberRemove(data: GuildMemberEvent){

    }

    open suspend fun onAtMessageCreate(data: AtMessageCreateEvent){

    }

    //TODO 官方有许多事件，后续在这里添加事件名称

}