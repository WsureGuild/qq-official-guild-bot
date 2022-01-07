package top.wsure.guild.official.dtos

object OfficialCode {
    private val removeAt = Regex("<@!\\d+>")
    private val removeChannel = Regex("<@!\\d+>")
    private val findAt = Regex("(?<=<#!)\\d+(?=>)")
    private val findChannel = Regex("(?<=<#!)\\d+(?=>)")
    fun String.messageContent():String{
        return this.replace(removeAt,"").replace(removeChannel,"")
    }
    fun String.toAtOC():String{
        return "<@!$this+>"
    }

    fun String.getOCAt():List<String>{
        return findAt.findAll(this).map { it.value }.toList()
    }
    fun String.toChannelOC():String{
        return "<#!$this+>"
    }
    fun String.getOCChannel():List<String>{
        return findChannel.findAll(this).map { it.value }.toList()
    }

}