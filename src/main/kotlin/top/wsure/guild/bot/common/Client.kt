package top.wsure.guild.bot.common

import okhttp3.*
import okio.ByteString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicLong

abstract class Client(
    private val wsUrl: String,
    private val retryTime:Long = 1000,
    private val retryWait:Long = 3000
) : WebSocketListener(),BotClient{
    private val retryCount = AtomicLong(0)
    var retryTimer: Timer? = null

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val wsClient: OkHttpClient = OkHttpClient.Builder().build()

    private val wsRequest: Request = Request.Builder()
        .get()
        .url(wsUrl)
        .build()

    private var connectWebSocket = wsClient.newWebSocket(wsRequest, listener()).also {
        logger.info("url :$wsUrl ")
    }

    override fun reconnect(){
        connectWebSocket.close(1000,"reconnect")
        logger.info("reconnecting ... ")
        connectWebSocket = wsClient.newWebSocket(wsRequest,listener())
    }

    override fun sendMessage(text: String){
        logger.info("send text message $text")
        connectWebSocket.send(text)
    }

    private fun listener():WebSocketListener{
        return object :WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                this@Client.onOpen(webSocket, response)
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                this@Client.onMessage(webSocket,text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                this@Client.onMessage(webSocket,bytes)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                this@Client.onClosing(webSocket,code,reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                this@Client.onClosed(webSocket,code,reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                this@Client.onFailure(webSocket,t,response)
            }
        }
    }
}