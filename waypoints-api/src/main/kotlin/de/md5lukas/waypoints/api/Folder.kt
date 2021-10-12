package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIFolder
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

interface Folder : GUIFolder {

    val id: UUID

    val owner: UUID?

    override var name: String

    var description: String?

    var material: Material?

    val amount: Int

    fun getAmountVisibleForPlayer(player: Player): Int

    override val folders: List<Folder>

    override val waypoints: List<Waypoint>

    fun delete()
}