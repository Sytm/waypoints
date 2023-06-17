package de.md5lukas.waypoints.config.sounds

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import net.kyori.adventure.sound.Sound

@Configurable
class SoundsConfiguration {

  lateinit var openGui: Sound
    private set

  @ConfigPath("click.normal")
  lateinit var clickNormal: Sound
    private set

  @ConfigPath("click.danger")
  lateinit var clickDanger: Sound
    private set

  @ConfigPath("click.dangerAbort")
  lateinit var clickDangerAbort: Sound
    private set

  @ConfigPath("click.success")
  lateinit var clickSuccess: Sound
    private set

  @ConfigPath("click.error")
  lateinit var clickError: Sound
    private set

  @ConfigPath("waypoint.created")
  lateinit var waypointCreated: Sound
    private set

  @ConfigPath("waypoint.selected")
  lateinit var waypointSelected: Sound
    private set

  @ConfigPath("player.selected")
  lateinit var playerSelected: Sound
    private set

  @ConfigPath("player.notification")
  lateinit var playerNotification: Sound
    private set

  lateinit var teleport: Sound
    private set
}
