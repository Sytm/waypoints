package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIDisplayable

/** The available sorting options for the GUI overview. */
enum class OverviewSort(private val comparator: Comparator<GUIDisplayable>) :
    Comparator<GUIDisplayable> {

  /**
   * This sorts first by [de.md5lukas.waypoints.api.gui.GUIType] and then by name if the type
   * matches, both ascending
   */
  TYPE_ASCENDING(compareBy<GUIDisplayable> { it.guiType }.thenBy { it.name }),

  /**
   * This sorts first by [de.md5lukas.waypoints.api.gui.GUIType] (descending) and then by name if
   * the type matches (ascending)
   */
  TYPE_DESCENDING(compareByDescending<GUIDisplayable> { it.guiType }.thenBy { it.name }),

  /** This sorts by the name */
  NAME_ASCENDING(compareBy { it.name }),

  /** This sorts by the name */
  NAME_DESCENDING(compareByDescending { it.name }),

  /** This sorts by the date of creation */
  CREATED_ASCENDING(compareBy { it.createdAt }),

  /** This sorts by the date of creation */
  CREATED_DESCENDING(compareByDescending { it.createdAt });

  override fun compare(o1: GUIDisplayable?, o2: GUIDisplayable?): Int = comparator.compare(o1, o2)
}
