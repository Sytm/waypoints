package de.md5lukas.waypoints.lang

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender

class Translation(
    private val translationLoader: TranslationLoader,
    private val key: String,
    private val prefix: Translation? = null,
    private val miniMessage: MiniMessage = MiniMessage.miniMessage()
) {

    val text: Component
        get() = staticComponent()

    val rawText: String
        get() = translationLoader[key]

    private lateinit var staticMessage: Component

    private fun staticComponent(): Component {
        if (!::staticMessage.isInitialized) {
            staticMessage = prependPrefix(miniMessage.deserialize(rawText))
        }
        return staticMessage
    }

    fun withReplacements(vararg resolvers: TagResolver): Component = if (resolvers.isEmpty()) {
        staticComponent()
    } else {
        prependPrefix(miniMessage.deserialize(rawText, *resolvers))
    }

    fun send(commandSender: CommandSender) {
        commandSender.sendMessage(staticComponent())
    }

    fun send(commandSender: CommandSender, vararg resolvers: TagResolver) {
        commandSender.sendMessage(withReplacements(*resolvers))
    }

    private fun prependPrefix(component: Component) = if (prefix === null) {
        component
    } else {
        prefix.text.append(component)
    }
}