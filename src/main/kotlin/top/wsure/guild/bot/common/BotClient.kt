package top.wsure.guild.bot.common

interface BotClient {

    fun reconnect()

    fun sendMessage(text: String)
}