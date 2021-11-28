package top.wsure.guild.bot.utils.ac

import kotlinx.serialization.Serializable

/**
 * FileName: AcNode
 * Author:   wsure
 * Date:     2021/4/9 10:20 上午
 * Description:
 */
@Serializable
data class AcNode<T>(
    val children:MutableMap<Char, AcNode<T>> = HashMap(),
    val value:Char? = null,
    var fail: AcNode<T>? = null,
    var key :T? = null,
    var finished:Boolean = false
    )
