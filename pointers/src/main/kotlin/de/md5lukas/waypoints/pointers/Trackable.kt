package de.md5lukas.waypoints.pointers

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

typealias TrackablePredicate = (Trackable) -> Boolean

/** Describes a general trackable location for the pointers to guide towards. */
interface Trackable {

  /** The location the Trackable is located at. This location may change. */
  val location: Location

  /**
   * The text the hologram pointer should use.
   *
   * If this value is null the hologram pointer will not be available.
   */
  fun getHologramText(player: Player): Component? = null

  /**
   * The item the hologram pointer should use.
   *
   * Requires [getHologramText] to not be null.
   *
   * If this value is null the icon will not be available.
   */
  val hologramItem: ItemStack?
    get() = null

  fun asPredicate(): TrackablePredicate = { it === this }
}
