package de.md5lukas.waypoints.lang

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class InventoryTranslation(
    private val translationLoader: TranslationLoader,
    private val key: String,
) {

    val text: List<Component>
        get() = staticComponent()

    val rawText: String
        get() = translationLoader[key]

    private lateinit var staticMessage: List<Component>

    private fun staticComponent(): List<Component> {
        if (!::staticMessage.isInitialized) {
            staticMessage = rawText.lineSequence().map {
                translationLoader.itemMiniMessage.deserialize(it)
            }.toList()
        }
        return staticMessage
    }

    fun withReplacements(vararg resolvers: TagResolver): List<Component> = if (resolvers.isEmpty()) {
        staticComponent()
    } else {
        rawText.lineSequence().map {
            translationLoader.itemMiniMessage.deserialize(it, *resolvers)
        }.toList()
    }
}