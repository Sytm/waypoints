package de.md5lukas.waypoints.config.inventory

import de.md5lukas.commons.paper.editMeta
import de.md5lukas.commons.paper.getStringNotNull
import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.ExportConfigurationSection
import de.md5lukas.konfig.SkipConfig
import de.md5lukas.waypoints.api.base.parseIcon
import de.md5lukas.waypoints.util.createCustomPlayerHead
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

@Configurable
class InventoryConfiguration {

  @SkipConfig private var _rootConfig: ConfigurationSection? = null

  @ExportConfigurationSection(true)
  private var rootConfig: ConfigurationSection
    set(value) {
      _rootConfig = value
      itemCache.clear()
    }
    get() = _rootConfig!!

  private val itemCache = HashMap<String, ItemStack>()

  private val headPrefix = Material.PLAYER_HEAD.name + ":"

  fun createNewStack(plugin: Plugin, path: String): ItemStack {
    val cached = itemCache[path]
    if (cached != null) {
      return cached.clone()
    }

    val materialString = rootConfig.getStringNotNull(path)
    val stack =
        if (materialString.startsWith(headPrefix)) {
          try {
            createCustomPlayerHead(plugin, materialString.substring(headPrefix.length))
          } catch (t: Throwable) {
            throw IllegalArgumentException("Invalid player head format at $path", t)
          }
        } else {
          try {
            materialString.parseIcon().let { icon ->
              ItemStack(icon.material).also { stack ->
                icon.customModelData?.let { customModelData ->
                  stack.editMeta<ItemMeta> { setCustomModelData(customModelData) }
                }
              }
            }
          } catch (t: Throwable) {
            throw IllegalArgumentException("The material $materialString at $path is not valid", t)
          }
        }

    itemCache[path] = stack

    return stack.clone()
  }
}
