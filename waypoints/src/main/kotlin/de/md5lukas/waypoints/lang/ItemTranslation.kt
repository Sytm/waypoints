package de.md5lukas.waypoints.lang

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemTranslation(
    private val translationLoader: TranslationLoader,
    private val key: String,
    private val appendItemSuffix: Boolean = false,
    private val fixedMaterial: Material? = null,
) {

  private val rawDisplayName: String
    get() = translationLoader["$key.displayName"]

  private val rawDescription: String
    get() = translationLoader["$key.description"]

  val material: Material
    get() =
        fixedMaterial
            ?: translationLoader.plugin.waypointsConfig.inventory.getMaterial(
                key + if (appendItemSuffix) ".item" else "")

  val item: ItemStack
    get() = getItem()

  fun getItem(vararg resolvers: TagResolver): ItemStack =
      ItemStack(material).also {
        it.itemMeta =
            it.itemMeta!!.also { itemMeta ->
              itemMeta.displayName(
                  translationLoader.itemMiniMessage.deserialize(rawDisplayName, *resolvers))
              var discard = true // Remove leading blank lines
              itemMeta.lore(
                  rawDescription.lineSequence().mapNotNullTo(mutableListOf()) { line ->
                    if (line.isNotBlank()) {
                      discard = false
                    }
                    if (discard) {
                      null
                    } else {
                      translationLoader.itemMiniMessage.deserialize(line, *resolvers)
                    }
                  })
            }
      }
}
