package top.wsure.guild.bot.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.wsure.guild.bot.utils.JsonUtils.jsonToObject
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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
    fun createFileAndDirectory(file:File){
        if(file.isDirectory) file.mkdirs()
        else if(!file.parentFile.exists()){
            file.parentFile.mkdirs()
        }
    }

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
