package de.md5lukas.waypoints.util

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import de.md5lukas.waypoints.WaypointsPermissions
import de.md5lukas.waypoints.WaypointsPlugin
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("UnstableApiUsage")
class UpdateChecker(
    private val plugin: WaypointsPlugin,
    private val owner: String,
    private val repository: String,
) : Runnable, Listener {

  private val notified = mutableSetOf<UUID>()
  private lateinit var message: Component

  override fun run() {
    val latestData = latestRelease

    if (latestData === null) {
      plugin.componentLogger.warn(plugin.translations.UPDATE_COULD_NOT_CHECK.text)
      return
    }

    if (isLatestNewer(plugin.pluginMeta.version, latestData.tagName.removePrefix("v"))) {
      val message =
          plugin.translations.UPDATE_NEW_VERSION_AVAILABLE.withReplacements(
              "latest" placeholder latestData.tagName,
              "link" placeholder
                  text {
                    content(latestData.htmlUrl)
                    clickEvent(ClickEvent.openUrl(latestData.htmlUrl))
                  })
      plugin.componentLogger.info(message)
      this.message = plugin.translations.PREFIX.text.append(message)
      plugin.registerEvents(this)
    } else {
      plugin.componentLogger.info(plugin.translations.UPDATE_USING_LATEST_VERSION.text)
    }
  }

  @EventHandler
  fun onJoin(e: PlayerJoinEvent) {
    if (e.player.uniqueId !in notified &&
        e.player.hasPermission(WaypointsPermissions.UPDATE_NOTIFICATION)) {
      e.player.sendMessage(message)
      notified += e.player.uniqueId
    }
  }

  private val latestRelease: LatestReleasesResponse?
    get() {
      val client = HttpClient.newHttpClient()
      val request =
          HttpRequest.newBuilder()
              .uri(
                  URI.create(
                      "https://api.github.com/repos/%s/%s/releases/latest"
                          .format(owner, repository)))
              .header("Accept", "application/vnd.github+json")
              .header("X-GitHub-Api-Version", " 2022-11-28")
              .header("User-Agent", "Plugin Update Checker (${plugin.pluginMeta.displayName})")
              .GET()
              .build()

      val response =
          try {
            client.send(request, BodyHandlers.ofInputStream())
          } catch (e: Exception) {
            return null
          }

      if (response.statusCode() != 200) {
        return null
      }

      return response.body().reader().use {
        Gson().fromJson(it, LatestReleasesResponse::class.java)
      }
    }

  private fun isLatestNewer(current: String, latest: String): Boolean {
    val regex = Regex("\\D+")
    val currentParts = current.split(regex)
    val latestParts = latest.split(regex)

    latestParts.forEachIndexed { index, latestPart ->
      val comparison =
          currentParts.getOrElse(index) { "0" }.toInt().compareTo(latestPart.toIntOrNull() ?: 0)
      if (comparison > 0) { // Current is higher
        return false
      }
      if (comparison < 0) { // Latest is higher
        return true
      }
    }
    return false // They are equal
  }

  private data class LatestReleasesResponse(
      @field:SerializedName("tag_name") val tagName: String,
      @field:SerializedName("html_url") val htmlUrl: String,
  )
}
