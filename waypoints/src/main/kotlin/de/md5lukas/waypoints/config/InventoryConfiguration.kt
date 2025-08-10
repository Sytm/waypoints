package de.md5lukas.waypoints.config

import de.md5lukas.commons.paper.getStringNotNull
import de.md5lukas.waypoints.api.Icon
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class InventoryConfiguration(private val config: ConfigurationSection) {

  private val itemCache = HashMap<String, ItemStack>()

  fun createNewStack(path: String): ItemStack {
    val cached = itemCache[path]
    if (cached != null) {
      return cached.clone()
    }

    val stack = Icon.icon(config.getStringNotNull(path)).asItemStack()

    itemCache[path] = stack

    return stack.clone()
  }
}
