package top.wsure.guild.bot.official.enums

enum class DispatchEnums {
    READY,
    RESUMED,
    GUILD_MEMBER_ADD,
    GUILD_MEMBER_UPDATE,
    GUILD_MEMBER_REMOVE,
    GUILD_CREATE,          // 当机器人加入新guild时
    GUILD_UPDATE,          // 当guild资料发生变更时
    GUILD_DELETE,          // 当机器人退出guild时
    CHANNEL_CREATE,
    CHANNEL_UPDATE,
    CHANNEL_DELETE,
    MESSAGE_CREATE,         //  私域bot message
    AT_MESSAGE_CREATE,
    DIRECT_MESSAGE_CREATE,
    AUDIO_START,           // 音频开始播放时
    AUDIO_FINISH,          // 音频播放结束时
    AUDIO_ON_MIC,          // 上麦时
    AUDIO_OFF_MIC,          // 下麦时
}