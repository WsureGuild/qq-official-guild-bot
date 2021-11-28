# qq-official-guild-bot

kotlin + okhttp 写的 qq频道官方bot websocket协议实现

## 项目结构
```
├─gradle
│  └─wrapper
├─logs
└─src
    ├─main
    │  ├─kotlin
    │  │  └─top
    │  │      └─wsure
    │  │          └─guild
    │  │              └─bot
    │  │                  ├─common                  //  websocket 抽象层
    │  │                  ├─component               //  用户自己开发的listener目录 
    │  │                  ├─official                //  核心实现，包括事件监听和api
    │  │                  │  ├─dtos                 //  数据结构类
    │  │                  │  │  ├─api               //  api相关request 和response
    │  │                  │  │  ├─event             //  ws事件
    │  │                  │  │  │  ├─channel        //  子频事件
    │  │                  │  │  │  ├─guild          //  
    │  │                  │  │  │  │  └─member      //  成员事件
    │  │                  │  │  │  └─guilds         //  主频事件
    │  │                  │  │  └─operation         //  opCode事件
    │  │                  │  ├─enums                //  枚举
    │  │                  │  └─intf                 //  事件Listener 和 官方api
    │  │                  └─utils                   //  工具包
    │  │                      ├─ac                  //  一个ac自动机的kotlin实现，用于字符串匹配
    │  │                      └─serializer          //  kotlinx.serialization 的 自定义serializer
    │  └─resources                                  //  配置文件目录
    └─test
        ├─kotlin
        └─resources

```
## 如何使用
1. 首先配置你的botId 和 botToken ，这个可以自由设置，我目前是放在idea的`Run/Debug Configation` 的`Program arguments`里了，格式为`id token`
```kotlin
    // 就可以直接从args里面拿到了，如果你觉得麻烦可以用自己的方法实现，能拿到就行，直接写代码里也行
fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString(",")}")

    val botId = args.first()
    val botToken = args.last()
    val token = "Bot ${botId}.${botToken}"
}
```
2. 实现你的Listener ，这里会附赠给你一个Demo ：`EditRoles`
```kotlin
    //实际上实现一个Listener十分简单，你只需要继承`OfficialBotEvent`，然后实现它里面你需要的方法就可以了
class EditRoles(private val token:String): OfficialBotEvent() {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    //这里我实现了 at事件的 监听
    override suspend fun onAtMessageCreate(data: AtMessageCreateEvent) {
        val message = data.content
        val guildRoles = OfficialBotApi.getRoles(token,data.guildId)
        //一个自己做的ac自动机，用作字符串匹配
        val acMatcher = AhoCorasickMatcher(guildRoles){it.name}
        //在 剔除了at 和 频道连接之后的消息中搜索角色名
        val roles = acMatcher.search(message.messageContent())
        if(roles.isNotEmpty())
        {
            val isDelete = message.contains("删除")
            logger.info("{} ${if(isDelete) "del" else "add"} roles :{}",data.author,roles)
            val successRoles = roles.filter { if(isDelete) OfficialBotApi.delRoles(token,data.guildId,data.author.id,it.id)
            else OfficialBotApi.addRoles(token,data.guildId,data.author.id,it.id) }
            val msg = "已经为${data.author.username}${if(isDelete) "删除" else "设置"}了身份${successRoles.joinToString { it.name }}"
            logger.info(" EditRoles success ,msg:$msg atMsg:$data")
            
            //注意：此处使用了“变量语料”，请向腾讯工作人员索要此权限，否则会报304语料不匹配
            kotlin.runCatching { data.reply(token,msg) }.onFailure {
                it.printStackTrace()
            }
        }
    }
    //  加入你需要在ws连接准备好之后做点事，那么就实现这个事件
    override suspend fun onReady(data: ReadyEvent) {
        //做点你想做的事
    }
}
```
3.配置并初始化ws连接
```kotlin
    // 实例化你的listener
    val listeners = listOf( EditRoles("yourToken") )

    // 实际上你只需要一个token参数就可以启动默认参数的不分片ws client了
    OfficialBotClient(IdentifyConfig("yourToken"),listeners)
    
    //分片 启动 请保证分片按照序号连接，否则会有部分分片连接失败，所以你看到我在这里加了延迟
    OfficialBotClient(IdentifyConfig("yourToken",4,0), listeners)
    delay(3000L)
    OfficialBotClient(IdentifyConfig("yourToken",4,1), listeners)
    delay(3000L)
    OfficialBotClient(IdentifyConfig("yourToken",4,2), listeners)
    delay(3000L)
    OfficialBotClient(IdentifyConfig("yourToken",4,3), listeners)
```
4.如此一来就大功告成了，如果你想为分片设置特定intents
```kotlin
    // IdentifyConfig 可以接受 Intents参数 ，请参阅官方文档进行配置
    val config = IdentifyConfig("yourToken",intents = Intents(
        guilds = true,
        guildMembers = true,
        directMessage = false, // 已知当前还未开放此事件，请勿设置为true ，否则鉴权会失败
        audioAction = true,
        atMessages = true
    ))
```