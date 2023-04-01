package de.md5lukas.waypoints.lang

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender

class Translation(
    private val translationLoader: TranslationLoader,
    private val key: String
) {

    val text: Component
        get() = staticComponent()

    val rawText: String
        get() = translationLoader[key]

    private lateinit var staticMessage: Component

    private fun staticComponent(): Component {
        if (!::staticMessage.isInitialized) {
            staticMessage = MiniMessage.miniMessage().deserialize(rawText)
        }
        return staticMessage
    }

    fun withReplacements(vararg resolvers: TagResolver): Component = if (resolvers.isEmpty()) {
        staticComponent()
    } else {
        MiniMessage.miniMessage().deserialize(rawText, *resolvers)
    }

    fun send(commandSender: CommandSender) {
        commandSender.sendMessage(staticComponent())
    }

    fun send(commandSender: CommandSender, vararg resolvers: TagResolver) {
        commandSender.sendMessage(withReplacements(*resolvers))
    }
}