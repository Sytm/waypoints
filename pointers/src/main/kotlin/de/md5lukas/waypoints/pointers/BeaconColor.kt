package de.md5lukas.waypoints.pointers

import java.util.*
import org.bukkit.Material

/**
 * This class represents all possible beacon beam colors that can be created with one colored glass
 * block
 */
enum class BeaconColor(val material: Material) {

  /** Beacon is covered with [Material.GLASS] */
  CLEAR(Material.GLASS),

  /** Beacon is covered with [Material.LIGHT_GRAY_STAINED_GLASS] */
  LIGHT_GRAY(Material.LIGHT_GRAY_STAINED_GLASS),

  /** Beacon is covered with [Material.GRAY_STAINED_GLASS] */
  GRAY(Material.GRAY_STAINED_GLASS),

  /** Beacon is covered with [Material.PINK_STAINED_GLASS] */
  PINK(Material.PINK_STAINED_GLASS),

  /** Beacon is covered with [Material.LIME_STAINED_GLASS] */
  LIME(Material.LIME_STAINED_GLASS),

  /** Beacon is covered with [Material.YELLOW_STAINED_GLASS] */
  YELLOW(Material.YELLOW_STAINED_GLASS),

  /** Beacon is covered with [Material.LIGHT_BLUE_STAINED_GLASS] */
  LIGHT_BLUE(Material.LIGHT_BLUE_STAINED_GLASS),

  /** Beacon is covered with [Material.MAGENTA_STAINED_GLASS] */
  MAGENTA(Material.MAGENTA_STAINED_GLASS),

  /** Beacon is covered with [Material.ORANGE_STAINED_GLASS] */
  ORANGE(Material.ORANGE_STAINED_GLASS),

  /** Beacon is covered with [Material.WHITE_STAINED_GLASS] */
  WHITE(Material.WHITE_STAINED_GLASS),

  /** Beacon is covered with [Material.BLACK_STAINED_GLASS] */
  BLACK(Material.BLACK_STAINED_GLASS),

  /** Beacon is covered with [Material.RED_STAINED_GLASS] */
  RED(Material.RED_STAINED_GLASS),

  /** Beacon is covered with [Material.GREEN_STAINED_GLASS] */
  GREEN(Material.GREEN_STAINED_GLASS),

  /** Beacon is covered with [Material.BROWN_STAINED_GLASS] */
  BROWN(Material.BROWN_STAINED_GLASS),

  /** Beacon is covered with [Material.BLUE_STAINED_GLASS] */
  BLUE(Material.BLUE_STAINED_GLASS),

  /** Beacon is covered with [Material.CYAN_STAINED_GLASS] */
  CYAN(Material.CYAN_STAINED_GLASS),

  /** Beacon is covered with [Material.PURPLE_STAINED_GLASS] */
  PURPLE(Material.PURPLE_STAINED_GLASS),
  ;

  val blockData by lazy { material.createBlockData() }

  companion object {
    private val entries: List<BeaconColor> =
        Collections.unmodifiableList(BeaconColor.values().toList())

    fun byMaterial(material: Material) = this.entries.firstOrNull { it.material === material }
  }
}
