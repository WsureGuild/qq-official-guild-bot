package top.wsure.guild.bot.official.enums

import kotlinx.serialization.Serializable

/*
CODE	名称	客户端操作	描述
0	Dispatch	Receive	服务端进行消息推送
1	Heartbeat	Send/Receive	客户端或服务端发送心跳
2	Identify	Send	客户端发送鉴权
6	Resume	Send	客户端回复会话
7	Reconnect	Receive	服务端通知客户端重新连接
9	Invalid Session	Receive	当identify或resume的时候，如果参数有错，服务端会返回该消息
11	Heartbeat ACK	Receive	当发送心跳成功之后，就会收到该消息
 */
@Serializable
enum class OPCodeEnums(
    val code:Int,
    val description:String,
){
    Dispatch(0,"服务端进行消息推送"),
    Heartbeat(1,"客户端或服务端发送心跳"),
    Identify(2,"客户端发送鉴权"),
    Resume(6,"客户端回复会话"),
    Reconnect(7,"服务端通知客户端重新连接"),
    Invalid_Session(9,"当identify或resume的时候，如果参数有错，服务端会返回该消息"),
    Heartbeat_Config(10,"心跳参数"),
    Heartbeat_ACK(11,"当发送心跳成功之后，就会收到该消息"),
    UNKNOWN(-1,"未知")
    ;

    companion object{
        private val codeMap = values().associateBy { it.code }
        fun getOPCodeByCode(code: Int): OPCodeEnums {
            return codeMap[code]?: UNKNOWN
        }
    }
}