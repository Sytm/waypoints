package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.util.runtimeReplace
import de.md5lukas.waypoints.util.splitDescriptionStringToList
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemTranslation(
    private val translationLoader: TranslationLoader,
    private val key: String,
) {

    val displayName: String
        get() = translationLoader["$key.displayName"]

    val description: String
        get() = translationLoader["$key.description"]

    val material: Material
        get() = translationLoader.plugin.waypointsConfig.inventory.getMaterial(key)

    val item: ItemStack
        get() = getItem(displayName, description)

    private fun getItem(displayName: String, description: String) = ItemStack(material).also {
        it.itemMeta = it.itemMeta!!.also { itemMeta ->
            itemMeta.setDisplayName(displayName)
            itemMeta.lore = splitDescriptionStringToList(description)
        }
    }

    fun getItem(map: Map<String, String>): ItemStack = getItem(displayName.runtimeReplace(map), description.runtimeReplace(map))
}