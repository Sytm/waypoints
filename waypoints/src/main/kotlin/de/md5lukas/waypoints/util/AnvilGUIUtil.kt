package de.md5lukas.waypoints.util

import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material

operator fun AnvilGUI.StateSnapshot.component1(): Boolean =
    outputItem === null || outputItem.type === Material.AIR

operator fun AnvilGUI.StateSnapshot.component2(): String = text
