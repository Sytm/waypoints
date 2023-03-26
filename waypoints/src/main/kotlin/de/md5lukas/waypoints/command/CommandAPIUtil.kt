package de.md5lukas.waypoints.command

import dev.jorel.commandapi.ArgumentTree
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument

inline fun CommandTree.permission(base: Argument<*>, permission: String, block: ArgumentTree.() -> Unit = {}): CommandTree =
    then(base.withPermission(permission).apply(block))

inline fun ArgumentTree.permission(base: Argument<*>, permission: String, block: ArgumentTree.() -> Unit = {}): ArgumentTree =
    then(base.withPermission(permission).apply(block))