package de.md5lukas.waypoints.api.gui

import de.md5lukas.waypoints.api.Folder
import de.md5lukas.waypoints.api.Waypoint

interface GUIFolder : GUIDisplayable {

    val folders: List<Folder>

    val waypoints: List<Waypoint>
}