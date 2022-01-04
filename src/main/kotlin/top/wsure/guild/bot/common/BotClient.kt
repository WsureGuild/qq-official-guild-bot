package top.wsure.guild.bot.common

interface BotClient {

    fun reconnect()

    fun connected()

    fun disconnect()

    fun sendMessage(text: String)
}