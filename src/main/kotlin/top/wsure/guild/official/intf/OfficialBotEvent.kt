package top.wsure.guild.official.intf

import top.wsure.guild.official.dtos.Message
import top.wsure.guild.official.dtos.event.ReadyEvent
import top.wsure.guild.official.dtos.event.channel.ChannelEvent
import top.wsure.guild.official.dtos.event.guild.member.GuildMemberEvent
import top.wsure.guild.official.dtos.event.guilds.GuildEvent
import top.wsure.guild.official.dtos.event.reactions.MessageReactionEvent
import top.wsure.guild.official.dtos.operation.IdentifyConfig

abstract class OfficialBotEvent {

    lateinit var officialBot :IdentifyConfig

    lateinit var sender:OfficialBotApi

    open suspend fun onReady(data: ReadyEvent) {}

    open suspend fun onGuildMemberAdd(data: GuildMemberEvent) {}

    open suspend fun onGuildMemberUpdate(data: GuildMemberEvent) {}

    open suspend fun onGuildMemberRemove(data: GuildMemberEvent) {}

    open suspend fun onAtMessageCreate(data: Message) {}

    open suspend fun onChannelCreate(data: ChannelEvent) {}

    open suspend fun onChannelUpdate(data: ChannelEvent) {}

    open suspend fun onChannelDelete(data: ChannelEvent) {}

    open suspend fun onGuildCreate(data: GuildEvent) {}

    open suspend fun onGuildUpdate(data: GuildEvent) {}

    open suspend fun onGuildDelete(data: GuildEvent) {}

    open suspend fun onResumed(config: IdentifyConfig, sessionId: String) {}

    open suspend fun onMessageCreate(data: Message) {}
    open suspend fun onMessageReactionAdd(data: MessageReactionEvent) {}
    open suspend fun onMessageReactionRemove(data: MessageReactionEvent) {}
    open suspend fun onDirectMessageCreate(data: Message) {}

    //TODO 官方有许多事件，后续在这里添加事件名称

}