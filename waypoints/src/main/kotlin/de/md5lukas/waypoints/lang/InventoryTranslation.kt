package de.md5lukas.waypoints.lang

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.jetbrains.annotations.VisibleForTesting

class InventoryTranslation(
    private val translationLoader: TranslationLoader,
    private val key: String,
) : AbstractTranslation {

  init {
    translationLoader.registerTranslationWrapper(this)
  }

  val text: List<Component>
    get() = staticComponent()

  val rawText: String
    get() = translationLoader[key]

  private var staticMessage: List<Component>? = null

  private fun staticComponent(): List<Component> {
    if (staticMessage === null) {
      staticMessage =
          rawText.lineSequence().map { translationLoader.itemMiniMessage.deserialize(it) }.toList()
    }
    return staticMessage!!
  }

  fun withReplacements(vararg resolvers: TagResolver): List<Component> =
      if (resolvers.isEmpty()) {
        staticComponent()
      } else {
        rawText
            .lineSequence()
            .map { translationLoader.itemMiniMessage.deserialize(it, *resolvers) }
            .toList()
      }

  override fun reset() {
    staticMessage = null
  }

  @VisibleForTesting override fun getKeys() = arrayOf(key)
}
