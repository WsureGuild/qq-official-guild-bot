package top.wsure.guild.official

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.wsure.guild.official.dtos.event.ReadyEvent
import top.wsure.guild.official.dtos.event.channel.ChannelEvent
import top.wsure.guild.official.dtos.event.guild.member.GuildMemberEvent
import top.wsure.guild.official.dtos.event.guilds.GuildEvent
import top.wsure.guild.official.dtos.operation.*
import top.wsure.guild.official.enums.DispatchEnums
import top.wsure.guild.official.enums.OPCodeEnums
import top.wsure.guild.official.intf.OfficialBotApi
import top.wsure.guild.official.intf.OfficialBotEvent
import top.wsure.guild.common.client.WebsocketClient
import top.wsure.guild.common.utils.FileUtils
import top.wsure.guild.common.utils.JsonUtils.jsonToObject
import top.wsure.guild.common.utils.JsonUtils.jsonToObjectOrNull
import top.wsure.guild.common.utils.JsonUtils.objectToJson
import top.wsure.guild.common.utils.ScheduleUtils
import top.wsure.guild.official.dtos.Message
import top.wsure.guild.official.dtos.event.reactions.MessageReactionEvent
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.io.path.Path

class OfficialClient(
    private val config: IdentifyConfig,
    private val officialEvents:List<OfficialBotEvent> = emptyList(),
    private val heartbeatDelay: Long = 30000,
    private val reconnectTimeout: Long = 60000,
    wsUrl :String = "wss://api.sgroup.qq.com/websocket",
    retryWait:Long = 3000,
    seqPath: String = "seq/official/"
):WebsocketClient(wsUrl, retryWait) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val identifyOpDto = IdentifyOperation(config.toIdentifyOperationData()).objectToJson()
    var sessionId :String = ""
    private val logHeader = "${config.index} of ${config.shards}"

    private var hbTimer: Timer? = null
    private val messageSeq by lazy { AtomicLong(0) }
    private val lastReceivedHeartBeat = AtomicLong(0)
    private val seqFile = Path("$seqPath${FileUtils.md5(config.getBotToken())}_${config.shards}_${config.index}").toFile()

    init {
        logger.info("seqFile:${seqFile.absolutePath}, exists:${seqFile.exists()}")
        Path(seqPath).toFile().mkdirs()
        if(seqFile.exists()){
            logger.info(" try to load seqFile")
            try {
                val cacheResumeData = seqFile.readText().jsonToObject<ResumeData>()
                messageSeq.getAndSet(cacheResumeData.seq)
                sessionId = cacheResumeData.sessionId
            }catch (e:Exception){
                logger.warn("read seqFile fail",e)
            }
        } else {
            logger.info("create new seqFile")
            seqFile.createNewFile()
        }
        officialEvents.forEach {
            it.officialBot = config
            it.sender = OfficialBotApi(config.getBotToken())
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        logger.info("$logHeader onOpen ")
    }

    @OptIn(DelicateCoroutinesApi::class)
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
                        logger.debug("$logHeader received HELLO , content:$text")
                        initConnection(webSocket)
                    }
                    //收到事件
                    OPCodeEnums.DISPATCH -> {
                        text.jsonToObjectOrNull<DispatchType>()?.also { dispatchDto ->

                            logger.debug("$logHeader received Dispatch type:${dispatchDto.type} content:$text")
                            when (dispatchDto.type) {
                                DispatchEnums.READY -> {
                                    text.jsonToObjectOrNull<Dispatch<ReadyEvent>>()?.also { readyEvent ->
                                        sessionId = readyEvent.d.sessionId
                                        GlobalScope.launch { officialEvents.forEach {
                                            it.onReady(readyEvent.d)
                                        } }
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_ADD -> {
                                    text.jsonToObjectOrNull<Dispatch<GuildMemberEvent>>()?.also { guildMemberEvent ->
                                        GlobalScope.launch { officialEvents.forEach {
                                            it.onGuildMemberAdd(guildMemberEvent.d)
                                        } }
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_UPDATE -> {
                                    text.jsonToObjectOrNull<Dispatch<GuildMemberEvent>>()?.also { guildMemberEvent ->
                                        GlobalScope.launch { officialEvents.forEach {
                                            it.onGuildMemberUpdate(guildMemberEvent.d)
                                        } }
                                    }
                                }
                                DispatchEnums.GUILD_MEMBER_REMOVE -> {
                                    text.jsonToObjectOrNull<Dispatch<GuildMemberEvent>>()?.also { guildMemberEvent ->
                                        GlobalScope.launch { officialEvents.forEach {
                                            it.onGuildMemberRemove(guildMemberEvent.d)
                                        } }
                                    }
                                }
                                DispatchEnums.AT_MESSAGE_CREATE -> {
                                    text.jsonToObjectOrNull<Dispatch<Message>>()?.also { guildAtMessage ->
                                        GlobalScope.launch { officialEvents.forEach { it.onAtMessageCreate(guildAtMessage.d) } }
                                    }
                                }
                                DispatchEnums.MESSAGE_CREATE -> {
                                    text.jsonToObjectOrNull<Dispatch<Message>>()?.also { messageCreate ->
                                        GlobalScope.launch { officialEvents.forEach { it.onMessageCreate(messageCreate.d) } }
                                    }
                                }
                                DispatchEnums.CHANNEL_CREATE -> {
                                    text.jsonToObjectOrNull<Dispatch<ChannelEvent>>()?.also { channelEvent ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onChannelCreate(channelEvent.d) } }
                                    }
                                }
                                DispatchEnums.CHANNEL_UPDATE -> {
                                    text.jsonToObjectOrNull<Dispatch<ChannelEvent>>()?.also { channelEvent ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onChannelUpdate(channelEvent.d) } }
                                    }
                                }
                                DispatchEnums.CHANNEL_DELETE -> {
                                    text.jsonToObjectOrNull<Dispatch<ChannelEvent>>()?.also { channelEvent ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onChannelDelete(channelEvent.d) } }
                                    }
                                }
                                DispatchEnums.GUILD_CREATE ->{
                                    text.jsonToObjectOrNull<Dispatch<GuildEvent>>()?.also { guildEvent ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onGuildCreate(guildEvent.d) } }
                                    }
                                }
                                DispatchEnums.GUILD_UPDATE ->{
                                    text.jsonToObjectOrNull<Dispatch<GuildEvent>>()?.also { guildEvent ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onGuildUpdate(guildEvent.d) } }
                                    }
                                }
                                DispatchEnums.GUILD_DELETE ->{
                                    text.jsonToObjectOrNull<Dispatch<GuildEvent>>()?.also { guildEvent ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onGuildDelete(guildEvent.d) } }
                                    }
                                }
                                DispatchEnums.MESSAGE_REACTION_ADD ->{
                                    text.jsonToObjectOrNull<Dispatch<MessageReactionEvent>>()?.also { messageReactionEvent ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onMessageReactionAdd(messageReactionEvent.d) } }
                                    }
                                }
                                DispatchEnums.MESSAGE_REACTION_REMOVE ->{
                                    text.jsonToObjectOrNull<Dispatch<MessageReactionEvent>>()?.also { messageReactionEvent ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onMessageReactionRemove(messageReactionEvent.d) } }
                                    }
                                }
                                DispatchEnums.DIRECT_MESSAGE_CREATE ->{
                                    text.jsonToObjectOrNull<Dispatch<Message>>()?.also { message ->
                                        GlobalScope.launch { officialEvents.forEach{ it.onDirectMessageCreate(message.d) } }
                                    }
                                }

                                DispatchEnums.RESUMED -> {
                                    GlobalScope.launch { officialEvents.forEach { it.onResumed(config,sessionId) } }
                                }
                                else -> {
                                    logger.warn("$logHeader Unknown event ! message:$text")
                                }
                            }
                            //先处理消息再处理seq
                            onReceivedMsg(dispatchDto)
                        }
                    }
                    OPCodeEnums.RECONNECT -> {
                        logger.warn("$logHeader need reconnect !!")
                        reconnectClient()
                    }
                    OPCodeEnums.INVALID_SESSION -> {
                        logger.error("$logHeader error:", OPCodeEnums.INVALID_SESSION.description)
                        failureConnect()
                    }
                    else -> {
                        logger.warn("$logHeader Unknown opcode ! message:$text")
                    }
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun failureConnect() {
        if(sessionId.isNotBlank()){
            sessionId = ""
            reconnectClient()
        } else {
            disconnect()
            hbTimer?.cancel()
            throw RuntimeException(OPCodeEnums.INVALID_SESSION.description)
        }
    }

    private fun onReceivedMsg(dispatchDto: DispatchType) {
        if(seqFile.exists() || seqFile.createNewFile()){
            seqFile.writeText(ResumeData(dispatchDto.seq,sessionId,config.getBotToken()).objectToJson())
        } else {
            logger.error("seqFile isn't exists and can't create !! ")
        }
        messageSeq.getAndSet(dispatchDto.seq)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        logger.warn("$logHeader onClosing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        logger.warn("$logHeader onClosed")
        reconnect()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        logger.warn("$logHeader onFailure try to reconnect")
        reconnectClient()
    }

    private fun initConnection(webSocket: WebSocket){
        connected()
        // 鉴权
        if(sessionId.isNotBlank()){
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
        hbTimer = ScheduleUtils.loopEvent(processor, Date(),heartbeatDelay)
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