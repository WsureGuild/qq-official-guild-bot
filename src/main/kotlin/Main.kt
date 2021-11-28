import kotlinx.coroutines.delay
import top.wsure.guild.bot.component.EditRoles
import top.wsure.guild.bot.official.OfficialBotClient
import top.wsure.guild.bot.official.dtos.event.AtMessageCreateEvent
import top.wsure.guild.bot.official.dtos.operation.IdentifyConfig
import top.wsure.guild.bot.utils.JsonUtils.jsonToObject
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

suspend fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString(",")}")

    val botId = args.first()
    val botToken = args.last()
    val token = "Bot ${botId}.${botToken}"

    val editRole = EditRoles(token)

    val listeners = listOf(
        editRole
    )

    OfficialBotClient(IdentifyConfig(token,4,0), listeners)
    delay(3000L)
    OfficialBotClient(IdentifyConfig(token,4,1), listeners)
    delay(3000L)
    OfficialBotClient(IdentifyConfig(token,4,2), listeners)
    delay(3000L)
    OfficialBotClient(IdentifyConfig(token,4,3), listeners)

}