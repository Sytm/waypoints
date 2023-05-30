package de.md5lukas.waypoints.pointers

import de.md5lukas.commons.paper.placeholder
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.util.getResolvers
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class PlayerTrackable(private val plugin: WaypointsPlugin, val player: Player) : Trackable {

  override val location: Location
    get() = player.location

  override fun getHologramText(player: Player) =
      plugin.translations.POINTERS_HOLOGRAM_PLAYER_TRACKING.withReplacements(
          "name" placeholder this.player.displayName(), *location.getResolvers(plugin, player))

  override val hologramItem =
      ItemStack(Material.PLAYER_HEAD).also { stack ->
        stack.editMeta {
          it as SkullMeta
          it.setOwningPlayer(player)
        }
      }

  override fun equals(other: Any?): Boolean {
    return player == (other as? PlayerTrackable)?.player
  }

  override fun hashCode(): Int {
    return player.hashCode()
  }
}
