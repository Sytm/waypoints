package de.md5lukas.waypoints.lang

import de.md5lukas.waypoints.util.runtimeReplace
import org.bukkit.inventory.ItemStack

class ItemTranslation(
    private val translationLoader: TranslationLoader,
    private val key: String,
    private val itemSupplier: () -> ItemStack
) {

    val displayName: String
        get() = translationLoader["$key.displayName"]

    val description: String
        get() = translationLoader["$key.description"]

    val item: ItemStack
        get() = getItem(displayName, description)

    private fun getItem(displayName: String, description: String) = itemSupplier().also {
        it.itemMeta = it.itemMeta?.also {
            it.setDisplayName(displayName)
            it.lore = description.lines()
        }
    }

    fun getItem(map: Map<String, String>): ItemStack = getItem(displayName.runtimeReplace(map), description.runtimeReplace(map))
}