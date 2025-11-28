package de.md5lukas.waypoints.config

import de.md5lukas.waypoints.api.Icon
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class InventoryConfiguration(private val config: ConfigurationSection) {

  private val itemCache = HashMap<String, Icon>()

  fun createNewStack(path: String): ItemStack {
    val cached = itemCache[path]
    if (cached != null) {
      return cached.asItemStack()
    }

    val icon =
        Icon.icon(
            config.getString(path)
                ?: throw IllegalArgumentException("The configuration key '$path' is not present"))

    itemCache[path] = icon

    return icon.asItemStack()
  }
}
