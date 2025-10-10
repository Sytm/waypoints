package de.md5lukas.waypoints.util

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import de.md5lukas.commons.paper.placeholder
import de.md5lukas.commons.paper.registerEvents
import de.md5lukas.commons.paper.textComponent
import de.md5lukas.schedulers.AbstractScheduledTask
import de.md5lukas.waypoints.Environment
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

class UpdateChecker(
    private val plugin: WaypointsPlugin,
) : Runnable, Listener {

  private val typeToken = object : TypeToken<List<LatestReleasesResponse>>() {}
  private var consoleOutput = true
  private val notified = mutableSetOf<UUID>()
  private lateinit var message: Component
  private lateinit var taskHandle: AbstractScheduledTask

  fun setTaskHandle(taskHandle: AbstractScheduledTask) {
    this.taskHandle = taskHandle
  }

  override fun run() {
    val latestData =
        try {
          fetchLatestRelease()
        } catch (e: Exception) {
          plugin.componentLogger.warn(plugin.translations.UPDATE_COULD_NOT_CHECK.text, e)
          return
        }

    if (isLatestNewer(plugin.pluginMeta.version, latestData.version)) {
      val message =
          plugin.translations.UPDATE_NEW_VERSION_AVAILABLE.withReplacements(
              "latest" placeholder latestData.version,
              "link" placeholder
                  textComponent {
                    content(latestData.downloadUrl)
                    clickEvent(ClickEvent.openUrl(latestData.downloadUrl))
                  })
      plugin.componentLogger.info(message)
      this.message = plugin.translations.PREFIX.text.append(message)
      taskHandle.cancel()

      plugin.server.onlinePlayers.forEach { player ->
        if (player.hasPermission(WaypointsPermissions.UPDATE_NOTIFICATION)) {
          notified += player.uniqueId
          player.sendMessage(this.message)
        }
      }

      plugin.registerEvents(this)
    } else if (consoleOutput) {
      plugin.componentLogger.info(plugin.translations.UPDATE_USING_LATEST_VERSION.text)
    }
    consoleOutput = false
  }

  @EventHandler
  fun onJoin(e: PlayerJoinEvent) {
    if (e.player.uniqueId !in notified &&
        e.player.hasPermission(WaypointsPermissions.UPDATE_NOTIFICATION)) {
      e.player.sendMessage(message)
      notified += e.player.uniqueId
    }
  }

  private fun fetchLatestRelease(): LatestReleasesResponse {
    val client = HttpClient.newHttpClient()
    val request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "https://api.modrinth.com/v2/project/${Environment.MODRINTH_PLUGIN_ID}/version"))
            .header("Accept", "application/json")
            .header("User-Agent", "Sytm/waypoints/${plugin.pluginMeta.version}")
            .GET()
            .build()

    val response = client.send(request, BodyHandlers.ofInputStream())

    if (response.statusCode() != 200) {
      val body = response.body().use { stream -> stream.readNBytes(100).decodeToString() }
      throw IllegalStateException("Invalid API response (${response.statusCode()} - $body)")
    }

    return response.body().reader().use { Gson().fromJson(it, typeToken).also(::println).first() }
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
      @field:SerializedName("id") val id: String,
      @field:SerializedName("version_number") val version: String,
  ) {
    val downloadUrl: String
      get() = "https://modrinth.com/plugin/${Environment.MODRINTH_PLUGIN_ID}/version/$id"
  }
}
