package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.util.runtimeReplace
import de.md5lukas.waypoints.util.splitDescriptionStringToList
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemTranslation(
    private val translationLoader: TranslationLoader,
    private val key: String,
    private val appendItemSuffix: Boolean = false,
    private val fixedMaterial: Material? = null,
) {

    val displayName: String
        get() = translationLoader["$key.displayName"]

    val description: String
        get() = translationLoader["$key.description"]

    val material: Material
        get() = fixedMaterial ?: translationLoader.plugin.waypointsConfig.inventory.getMaterial(key + if (appendItemSuffix) ".item" else "")

    val item: ItemStack
        get() = getItem(displayName, description, false)

    private fun getItem(displayName: String, description: String, trimDescription: Boolean) = ItemStack(material).also {
        it.itemMeta = it.itemMeta!!.also { itemMeta ->
            itemMeta.setDisplayName(displayName)
            itemMeta.lore = splitDescriptionStringToList(
                if (trimDescription) {
                    description.trim()
                } else {
                    description
                }
            )
        }
    }

    fun getItem(map: Map<String, String>, trimDescription: Boolean = false): ItemStack =
        getItem(displayName.runtimeReplace(map), description.runtimeReplace(map), trimDescription)
}