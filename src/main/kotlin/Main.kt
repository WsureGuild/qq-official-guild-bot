import kotlinx.coroutines.delay
import top.wsure.guild.bot.component.EditRoles
import top.wsure.guild.bot.official.OfficialClient
import top.wsure.guild.bot.official.dtos.operation.IdentifyConfig

suspend fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString(",")}")

    val botId = args.first()
    val botToken = args.last()
    val token = "Bot ${botId}.${botToken}"

    val editRole = EditRoles(token)

    val listeners = listOf(
        editRole
    )

    OfficialClient(IdentifyConfig(token,4,0), listeners)
    delay(3000L)
    OfficialClient(IdentifyConfig(token,4,1), listeners)
    delay(3000L)
    OfficialClient(IdentifyConfig(token,4,2), listeners)
    delay(3000L)
    OfficialClient(IdentifyConfig(token,4,3), listeners)

//    OfficialClient(IdentifyConfig(token,1,0), listeners)
}