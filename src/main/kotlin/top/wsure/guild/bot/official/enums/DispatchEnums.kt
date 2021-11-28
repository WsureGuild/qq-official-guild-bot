package top.wsure.guild.bot.official.enums

enum class DispatchEnums {
    READY,
    GUILD_MEMBER_ADD,
    GUILD_MEMBER_UPDATE,
    GUILD_MEMBER_REMOVE,
    GUILD_UPDATE,
    CHANNEL_CREATE,
    CHANNEL_UPDATE,
    CHANNEL_DELETE,
    DIRECT_MESSAGE_CREATE,
    AT_MESSAGE_CREATE,
    AUDIO_START,           // 音频开始播放时
    AUDIO_FINISH,          // 音频播放结束时
    AUDIO_ON_MIC,          // 上麦时
    AUDIO_OFF_MIC,          // 下麦时
}