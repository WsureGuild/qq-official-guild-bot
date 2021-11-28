import top.wsure.guild.bot.official.OfficialBotClient
import top.wsure.guild.bot.official.dtos.operation.IdentifyConfig

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    val botId = 0L
    val botToken =  ""
    val token = "Bot ${botId}.${botToken}"
    OfficialBotClient(IdentifyConfig(token))

}