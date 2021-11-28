package top.wsure.guild.bot.utils.ac

import kotlinx.serialization.Serializable

/**
 * FileName: MatchingResult
 * Author:   wsure
 * Date:     2021/4/7 4:24 下午
 * Description:
 */
@Serializable
data class MatchingResult<T>(

    var patternString: String,

    var index: Int,

    var pattern: T
)