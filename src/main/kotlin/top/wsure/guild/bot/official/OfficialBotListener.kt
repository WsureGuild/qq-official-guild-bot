package top.wsure.guild.bot.official

import kotlinx.coroutines.runBlocking
import okhttp3.Response
import okhttp3.WebSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.wsure.guild.bot.common.BaseBotListener
import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent
import top.wsure.guild.bot.official.dtos.event.ReadyEvent
import top.wsure.guild.bot.official.dtos.event.guild.member.GuildMemberEvent
import top.wsure.guild.bot.official.dtos.operation.*
import top.wsure.guild.bot.official.enums.DispatchEnums
import top.wsure.guild.bot.official.enums.OPCodeEnums
import top.wsure.guild.bot.official.intf.OfficialBotEvent
import top.wsure.guild.bot.utils.JsonUtils.jsonToObjectOrNull
import top.wsure.guild.bot.utils.JsonUtils.objectToJson
import top.wsure.guild.bot.utils.ScheduleUtils
import java.util.*
import java.util.concurrent.atomic.AtomicLong


class OfficialBotListener(
    config: IdentifyConfig,
    private val officialEvents:List<OfficialBotEvent> = emptyList(),
    private val heartbeatDelay: Long = 30000,
    private val reconnectTimeout: Long = 60000,
): BaseBotListener() {
    private var hbTimer: Timer? = null

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val identifyOpDto = IdentifyOperation(config.toIdentifyOperationData()).objectToJson()
    private val messageCount by lazy { AtomicLong(0) }

    private val lastReceivedHeartBeat = AtomicLong(0)

    private var session :String = ""


    override fun onOpen(webSocket: WebSocket, response: Response) {
        logger.info("onOpen ")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        runCatching{
            logger.debug("received message $text")
            text.jsonToObjectOrNull<Operation>()?.also { opType ->

                when(opType.type()){
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
                        text.jsonToObjectOrNull<Dispatch>()?.also { dispatchDto ->
                            messageCount.getAndSet(dispatchDto.seq)
                            logger.info("received Dispatch type:${dispatchDto.type} content:$text")
                            when(dispatchDto.type){
                                DispatchEnums.READY -> {
                                    text.jsonToObjectOrNull<ReadyEvent>()?.also { readyEvent ->
                                        session = readyEvent.d.sessionId
                                        officialEvents.forEach { runBlocking {
                                            it.onReady(readyEvent)
                                        }}
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_ADD ->{
                                    text.jsonToObjectOrNull<GuildMemberEvent>()?.also { guildMemberEvent ->
                                        officialEvents.forEach { runBlocking {
                                            it.onGuildMemberAdd(guildMemberEvent)
                                        }}
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_UPDATE -> {
                                    text.jsonToObjectOrNull<GuildMemberEvent>()?.also { guildMemberEvent ->
                                        officialEvents.forEach { runBlocking {
                                            it.onGuildMemberUpdate(guildMemberEvent)
                                        }}
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_REMOVE ->{
                                    text.jsonToObjectOrNull<GuildMemberEvent>()?.also { guildMemberEvent ->
                                        officialEvents.forEach { runBlocking {
                                            it.onGuildMemberRemove(guildMemberEvent)
                                        }}
                                    }
                                }
                                DispatchEnums.AT_MESSAGE_CREATE -> {
                                    text.jsonToObjectOrNull<AtMessageCreateEvent>()?.also { guildAtMessage ->
                                        officialEvents.forEach { runBlocking { it.onAtMessageCreate(guildAtMessage) } }
                                    }
                                }
                                else -> {
                                    logger.warn("Unknown event ! message:$text")
                                }
                            }
                        }
                    }
                    OPCodeEnums.RECONNECT -> {
                        logger.warn("need reconnect !!")
                        reconnectClient()
                    }
                    OPCodeEnums.INVALID_SESSION -> {
                        webSocket.cancel()
                        hbTimer?.cancel()
                        logger.error(OPCodeEnums.INVALID_SESSION.description)
                        throw RuntimeException(OPCodeEnums.INVALID_SESSION.description)
                    }
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        logger.warn("onClosing try to reconnect")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        logger.warn("onFailure try to reconnect")
        reconnectClient()
    }

    fun WebSocket.logInfo():String{
        return "${this.request().url} - ${this.request().body.toString()}"
    }

    private fun initConnection(webSocket: WebSocket){
        // 鉴权
        webSocket.sendAndPrintLog(identifyOpDto)
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
                logger.warn("heartbeat timeout , try to reconnect")
                reconnectClient()
            } else {
                val hb = Heartbeat(messageCount.get()).objectToJson()
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
            logger.debug("send Heartbeat $text")
        } else {
            logger.info("send text message $text")
        }
        this.send(text)
    }
}