package de.md5lukas.waypoints.config.general

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import de.md5lukas.konfig.TypeAdapter
import de.md5lukas.konfig.UseAdapter
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.block.Action

@Configurable
class OpenWithItemConfiguration {

  var enabled: Boolean = false
    private set

  @UseAdapter(ActionAdapter::class)
  @ConfigPath("click")
  var validClicks: List<Action> = emptyList()
    private set

  var mustSneak: Boolean = false
    private set

  var items: List<Material> = emptyList()
    private set

  private class ActionAdapter : TypeAdapter<List<Action>> {
    override fun get(section: ConfigurationSection, path: String) =
        section.getString(path)?.let {
          when (val click = it.lowercase()) {
            "right" -> listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
            "left" -> listOf(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK)
            else -> throw IllegalArgumentException("The click type $click is invalid")
          }
        }
  }
}
