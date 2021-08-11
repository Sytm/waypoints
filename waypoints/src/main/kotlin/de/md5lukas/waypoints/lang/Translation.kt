package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.util.runtimeReplace
import org.bukkit.command.CommandSender

class Translation(
    private val translationLoader: TranslationLoader,
    private val key: String
) {
    val text: String
        get() = translationLoader[key]

    fun withReplacements(map: Map<String, String>) = text.runtimeReplace(map)

    fun send(commandSender: CommandSender) {
        commandSender.sendMessage(text)
    }

    fun send(commandSender: CommandSender, map: Map<String, String>) {
        commandSender.sendMessage(text.runtimeReplace(map))
    }
}