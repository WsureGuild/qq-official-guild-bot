import top.wsure.guild.bot.official.OfficialBotClient
import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent
import top.wsure.guild.bot.official.dtos.operation.IdentifyConfig
import top.wsure.guild.bot.utils.JsonUtils.jsonToObject
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString(",")}")

    val botId = args.first()
    val botToken = args.last()
    val token = "Bot ${botId}.${botToken}"
    OfficialBotClient(IdentifyConfig(token))

}