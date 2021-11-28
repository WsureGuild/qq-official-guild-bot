package top.wsure.guild.bot.official.intf

import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent
import top.wsure.guild.bot.official.dtos.event.ReadyEvent
import top.wsure.guild.bot.official.dtos.event.channel.ChannelEvent
import top.wsure.guild.bot.official.dtos.event.guild.member.GuildMemberEvent
import top.wsure.guild.bot.official.dtos.event.guilds.GuildEvent
import top.wsure.guild.bot.official.dtos.operation.IdentifyConfig

abstract class OfficialBotEvent {
    open suspend fun onReady(data: ReadyEvent) {}

    open suspend fun onGuildMemberAdd(data: GuildMemberEvent) {}

    open suspend fun onGuildMemberUpdate(data: GuildMemberEvent) {}

    open suspend fun onGuildMemberRemove(data: GuildMemberEvent) {}

    open suspend fun onAtMessageCreate(data: AtMessageCreateEvent) {}

    open suspend fun onChannelCreate(data: ChannelEvent) {}

    open suspend fun onChannelUpdate(data: ChannelEvent) {}

    open suspend fun onChannelDelete(data: ChannelEvent) {}

    open suspend fun onGuildCreate(data: GuildEvent) {}

    open suspend fun onGuildUpdate(data: GuildEvent) {}

    open suspend fun onGuildDelete(data: GuildEvent) {}

    open suspend fun onResumed(config: IdentifyConfig, sessionId: String) {}

    //TODO 官方有许多事件，后续在这里添加事件名称

}