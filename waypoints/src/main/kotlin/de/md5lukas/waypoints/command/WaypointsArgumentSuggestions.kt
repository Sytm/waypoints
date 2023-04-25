package de.md5lukas.waypoints.command

import com.mojang.brigadier.Message
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.util.Formatters
import de.md5lukas.waypoints.util.containsNonWordCharacter
import de.md5lukas.waypoints.util.runTaskAsync
import dev.jorel.commandapi.BukkitTooltip
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

class WaypointsArgumentSuggestions(
    private val plugin: WaypointsPlugin,
    private val textMode: Boolean,
    private val allowGlobals: Boolean,
    private val filter: ((CommandSender, Waypoint) -> Boolean)? = null,
) : ArgumentSuggestions<CommandSender> {

    private var reloadError = true

    override fun suggest(
        info: SuggestionInfo<CommandSender>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        if (!plugin.isEnabled) {
            // The server has been reloaded and this reference to the plugin is outdated.
            if (reloadError) {
                reloadError = false
                plugin.logger.log(Level.SEVERE, "The server has been reloaded, which breaks the command completion. Consider restarting")
            }
            return builder.buildFuture()
        }

        val sender = info.sender
        val future = CompletableFuture<Suggestions>()

        plugin.runTaskAsync {
            val query = if (textMode) {
                info.currentArg.removePrefix("\"").removeSuffix("\"")
            } else info.currentArg

            if (allowGlobals) {
                val publicPrefix = plugin.translations.COMMAND_SEARCH_PREFIX_PUBLIC.text + "/"
                val permissionPrefix = plugin.translations.COMMAND_SEARCH_PREFIX_PERMISSION.text + "/"
                arrayOf(publicPrefix, permissionPrefix).forEach {
                    if (it.startsWith(query, true)) {
                        builder.suggest(formatSuggestion(it))
                    }
                }
                plugin.api.publicWaypoints.searchWaypoints(query.removePrefix(publicPrefix)).forEach {
                    if (shouldDiscard(sender, it.t)) {
                        return@forEach
                    }
                    builder.suggest(formatSuggestion("$publicPrefix${it.indexedName}"), it.t.getTooltip())
                }
                plugin.api.permissionWaypoints.searchWaypoints(query.removePrefix(permissionPrefix), sender).forEach {
                    if (shouldDiscard(sender, it.t)) {
                        return@forEach
                    }
                    builder.suggest(formatSuggestion("$permissionPrefix${it.indexedName}"), it.t.getTooltip())
                }
            }
            if (sender is Player) {
                plugin.api.getWaypointPlayer(sender.uniqueId).searchWaypoints(query).forEach {
                    if (shouldDiscard(sender, it.t)) {
                        return@forEach
                    }
                    builder.suggest(formatSuggestion(it.indexedName), it.t.getTooltip())
                }
            }
            future.complete(builder.build())
        }
        return future
    }

    private fun formatSuggestion(name: String) = if (textMode && name.containsNonWordCharacter()) {
        "\"$name\""
    } else {
        name
    }

    private fun Waypoint.getTooltip(): Message = BukkitTooltip.messageFromString(createdAt.format(Formatters.SHORT_DATE_TIME_FORMATTER))

    private fun shouldDiscard(sender: CommandSender, waypoint: Waypoint) =
        waypoint.location.world === null || filter?.invoke(sender, waypoint) == false
}