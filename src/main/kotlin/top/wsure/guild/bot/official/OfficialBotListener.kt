package top.wsure.guild.bot.official

import kotlinx.coroutines.runBlocking
import okhttp3.Response
import okhttp3.WebSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.wsure.guild.bot.common.BaseBotListener
import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent
import top.wsure.guild.bot.official.dtos.event.ReadyEvent
import top.wsure.guild.bot.official.dtos.event.channel.ChannelEvent
import top.wsure.guild.bot.official.dtos.event.guild.member.GuildMemberEvent
import top.wsure.guild.bot.official.dtos.event.guilds.GuildEvent
import top.wsure.guild.bot.official.dtos.operation.*
import top.wsure.guild.bot.official.enums.DispatchEnums
import top.wsure.guild.bot.official.enums.OPCodeEnums
import top.wsure.guild.bot.official.intf.OfficialBotEvent
import top.wsure.guild.bot.utils.JsonUtils.jsonToObjectOrNull
import top.wsure.guild.bot.utils.JsonUtils.objectToJson
import top.wsure.guild.bot.utils.ScheduleUtils
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong


class OfficialBotListener(
    private val config: IdentifyConfig,
    private val officialEvents:List<OfficialBotEvent> = emptyList(),
    private val heartbeatDelay: Long = 30000,
    private val reconnectTimeout: Long = 60000,
): BaseBotListener() {
    private val identifyOpDto = IdentifyOperation(config.toIdentifyOperationData()).objectToJson()
    var sessionId :String = ""

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val logHeader = "${config.index} of ${config.shards}"

    private var hbTimer: Timer? = null
    private val messageSeq by lazy { AtomicLong(0) }
    private val lastReceivedHeartBeat = AtomicLong(0)
    private val isResume = AtomicBoolean(false)


    override fun onOpen(webSocket: WebSocket, response: Response) {
        logger.info("$logHeader onOpen ")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        runCatching{
            logger.trace("$logHeader received message $text")
            text.jsonToObjectOrNull<Operation>()?.also { opType ->

                when(opType.op){
                    OPCodeEnums.HEARTBEAT_ACK -> {
                        lastReceivedHeartBeat.getAndSet(System.currentTimeMillis())
                    }
                    //首次连接 发送Identify信息鉴权
                    OPCodeEnums.HELLO -> {
                        // 初始化操作
                        initConnection(webSocket)
                    }
                    //收到事件
                    OPCodeEnums.DISPATCH -> {
                        text.jsonToObjectOrNull<DispatchType>()?.also { dispatchDto ->
                            successConnect(dispatchDto)
                            logger.debug("$logHeader received Dispatch type:${dispatchDto.type} content:$text")
                            when(dispatchDto.type){
                                DispatchEnums.READY -> {
                                    text.jsonToObjectOrNull<Dispatch<ReadyEvent>>()?.also { readyEvent ->
                                        sessionId = readyEvent.d.sessionId
                                        officialEvents.forEach { runBlocking {
                                            it.onReady(readyEvent.d)
                                        }}
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_ADD ->{
                                    text.jsonToObjectOrNull<Dispatch<GuildMemberEvent>>()?.also { guildMemberEvent ->
                                        officialEvents.forEach { runBlocking {
                                            it.onGuildMemberAdd(guildMemberEvent.d)
                                        }}
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_UPDATE -> {
                                    text.jsonToObjectOrNull<Dispatch<GuildMemberEvent>>()?.also { guildMemberEvent ->
                                        officialEvents.forEach { runBlocking {
                                            it.onGuildMemberUpdate(guildMemberEvent.d)
                                        }}
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_REMOVE ->{
                                    text.jsonToObjectOrNull<Dispatch<GuildMemberEvent>>()?.also { guildMemberEvent ->
                                        officialEvents.forEach { runBlocking {
                                            it.onGuildMemberRemove(guildMemberEvent.d)
                                        }}
                                    }
                                }
                                DispatchEnums.AT_MESSAGE_CREATE -> {
                                    text.jsonToObjectOrNull<Dispatch<AtMessageCreateEvent>>()?.also { guildAtMessage ->
                                        officialEvents.forEach { runBlocking { it.onAtMessageCreate(guildAtMessage.d) } }
                                    }
                                }
                                DispatchEnums.CHANNEL_CREATE -> {
                                    text.jsonToObjectOrNull<Dispatch<ChannelEvent>>()?.also { channelEvent ->
                                        officialEvents.forEach{ runBlocking { it.onChannelCreate(channelEvent.d) }}
                                    }
                                }
                                DispatchEnums.CHANNEL_UPDATE -> {
                                    text.jsonToObjectOrNull<Dispatch<ChannelEvent>>()?.also { channelEvent ->
                                        officialEvents.forEach{ runBlocking { it.onChannelUpdate(channelEvent.d) }}
                                    }
                                }
                                DispatchEnums.CHANNEL_DELETE -> {
                                    text.jsonToObjectOrNull<Dispatch<ChannelEvent>>()?.also { channelEvent ->
                                        officialEvents.forEach{ runBlocking { it.onChannelDelete(channelEvent.d) }}
                                    }
                                }
                                DispatchEnums.GUILD_CREATE ->{
                                    text.jsonToObjectOrNull<Dispatch<GuildEvent>>()?.also { guildEvent ->
                                        officialEvents.forEach{ runBlocking { it.onGuildCreate(guildEvent.d) }}
                                    }
                                }
                                DispatchEnums.GUILD_UPDATE ->{
                                    text.jsonToObjectOrNull<Dispatch<GuildEvent>>()?.also { guildEvent ->
                                        officialEvents.forEach{ runBlocking { it.onGuildUpdate(guildEvent.d) }}
                                    }
                                }
                                DispatchEnums.GUILD_DELETE ->{
                                    text.jsonToObjectOrNull<Dispatch<GuildEvent>>()?.also { guildEvent ->
                                        officialEvents.forEach{ runBlocking { it.onGuildDelete(guildEvent.d) }}
                                    }
                                }
                                DispatchEnums.RESUMED -> {
                                    officialEvents.forEach { runBlocking { it.onResumed(config,sessionId) } }
                                }
                                else -> {
                                    logger.warn("$logHeader Unknown event ! message:$text")
                                }
                            }
                        }
                    }
                    OPCodeEnums.RECONNECT -> {
                        logger.warn("$logHeader need reconnect !!")
                        isResume.getAndSet(true)
                        reconnectClient()
                    }
                    OPCodeEnums.INVALID_SESSION -> {
                        logger.error("$logHeader error:",OPCodeEnums.INVALID_SESSION.description)
                        failureConnect(webSocket)

                    }
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun failureConnect(webSocket: WebSocket) {
        if(isResume.get()){
            isResume.set(false)
            reconnectClient()
        } else {
            webSocket.cancel()
            hbTimer?.cancel()
            throw RuntimeException(OPCodeEnums.INVALID_SESSION.description)
        }
    }

    private fun successConnect(dispatchDto: DispatchType) {
        messageSeq.getAndSet(dispatchDto.seq)
        isResume.set(false)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        logger.warn("$logHeader onClosing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        logger.warn("$logHeader onClosed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        logger.warn("$logHeader onFailure try to reconnect")
        reconnectClient()
    }

    private fun initConnection(webSocket: WebSocket){
        // 鉴权
        if(isResume.get()){
            val resume = Resume(ResumeData(messageSeq.get(),sessionId,config.getBotToken()))
            webSocket.sendAndPrintLog(resume.objectToJson())
        } else {
            webSocket.sendAndPrintLog(identifyOpDto)
        }
        // 启动心跳发送
        lastReceivedHeartBeat.getAndSet(System.currentTimeMillis())
        val processor = createHeartBeatProcessor(webSocket)
        //  先取消以前的定时器
        hbTimer?.cancel()
        // 启动新的心跳
        hbTimer = ScheduleUtils.loopEvent(processor,Date(),heartbeatDelay)
    }

    private fun createHeartBeatProcessor(webSocket: WebSocket):suspend () ->Unit {
        return suspend {
            val last = lastReceivedHeartBeat.get()
            val now = System.currentTimeMillis()
            if( now - last > reconnectTimeout){
                logger.warn("$logHeader heartbeat timeout , try to reconnect")
                reconnectClient()
            } else {
                val hb = Heartbeat(messageSeq.get()).objectToJson()
                webSocket.sendAndPrintLog(hb,true)
            }
        }
    }

    private fun reconnectClient(){
        hbTimer?.cancel()
        reconnect()
    }

    private fun WebSocket.sendAndPrintLog(text: String, isHeartbeat:Boolean = false){
        if(isHeartbeat){
            logger.debug("$logHeader send Heartbeat $text")
        } else {
            logger.info("$logHeader send text message $text")
        }
        this.send(text)
    }
}