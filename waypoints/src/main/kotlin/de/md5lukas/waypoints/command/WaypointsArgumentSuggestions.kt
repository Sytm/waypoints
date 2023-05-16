package de.md5lukas.waypoints.command

import com.mojang.brigadier.Message
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.okkero.skedule.future
import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Waypoint
import de.md5lukas.waypoints.util.containsNonWordCharacter
import dev.jorel.commandapi.BukkitTooltip
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import java.util.concurrent.CompletableFuture
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WaypointsArgumentSuggestions(
    private val plugin: WaypointsPlugin,
    private val textMode: Boolean,
    private val allowGlobals: Boolean,
    private val filter: (suspend (CommandSender, Waypoint) -> Boolean)? = null,
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
        plugin.slF4JLogger.error(
            "The server has been reloaded, which breaks the command completion. Consider restarting")
      }
      return builder.buildFuture()
    }

    val sender = info.sender

    return plugin.future {
      val query =
          if (textMode) {
            info.currentArg.removePrefix("\"").removeSuffix("\"")
          } else info.currentArg

      if (allowGlobals) {
        val publicPrefix = plugin.translations.COMMAND_SEARCH_PREFIX_PUBLIC.rawText + "/"
        val permissionPrefix = plugin.translations.COMMAND_SEARCH_PREFIX_PERMISSION.rawText + "/"
        arrayOf(publicPrefix, permissionPrefix).forEach {
          if (it.startsWith(query, true)) {
            builder.suggest(formatSuggestion(it))
          }
        }
        plugin.api.publicWaypoints.searchWaypoints(query.removePrefix(publicPrefix)).forEach {
          if (shouldDiscard(sender, it.t)) {
            return@forEach
          }
          builder.suggest(
              formatSuggestion("$publicPrefix${it.indexedName}"), it.t.getTooltip(sender))
        }
        plugin.api.permissionWaypoints
            .searchWaypoints(query.removePrefix(permissionPrefix), sender)
            .forEach {
              if (shouldDiscard(sender, it.t)) {
                return@forEach
              }
              builder.suggest(
                  formatSuggestion("$permissionPrefix${it.indexedName}"), it.t.getTooltip(sender))
            }
      }
      if (sender is Player) {
        plugin.api.getWaypointPlayer(sender.uniqueId).searchWaypoints(query).forEach {
          if (shouldDiscard(sender, it.t)) {
            return@forEach
          }
          builder.suggest(formatSuggestion(it.indexedName), it.t.getTooltip(sender))
        }
      }
      builder.build()
    }
  }

  private fun formatSuggestion(name: String) =
      if (textMode && name.containsNonWordCharacter()) {
        "\"$name\""
      } else {
        name
      }

  private fun Waypoint.getTooltip(sender: CommandSender): Message =
      plugin.apiExtensions.run {
        BukkitTooltip.messageFromAdventureComponent(
            plugin.translations.COMMAND_SEARCH_TOOLTIP.withReplacements(
                *getResolvers(sender as? Player)))
      }

  private suspend fun shouldDiscard(sender: CommandSender, waypoint: Waypoint) =
      waypoint.location.world === null || filter?.invoke(sender, waypoint) == false
}
