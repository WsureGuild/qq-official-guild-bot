package top.wsure.guild.bot.utils

import top.wsure.guild.bot.utils.JsonUtils.jsonToObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object FileUtils {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    inline fun <reified T> String.readResourceJson(): T? {
        return kotlin.runCatching { FileUtils::class.java.classLoader.getResource(this)?.readText()?.jsonToObject<T>() }
            .onFailure {
                logger.warn("Read resource file {} by classloader failure !!", this, it)
            }.getOrNull()
    }

    inline fun <reified T> String.readFileJson(): T? {
        return kotlin.runCatching { File(this).readText().jsonToObject<T>() }
            .onFailure {
                logger.warn("Read file {} by File method failure !!", this, it)
            }.getOrNull()
    }
}
