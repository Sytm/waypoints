package de.md5lukas.waypoints.config.sounds

import de.md5lukas.konfig.ConfigPath
import de.md5lukas.konfig.Configurable
import net.kyori.adventure.sound.Sound

@Configurable
class SoundsConfiguration {

  private val seed: Long
    get() = System.currentTimeMillis()

  @ConfigPath("openGui") private lateinit var openGui0: Sound

  @ConfigPath("click.normal") private lateinit var clickNormal0: Sound

  @ConfigPath("click.danger") private lateinit var clickDanger0: Sound

  @ConfigPath("click.dangerAbort") private lateinit var clickDangerAbort0: Sound

  @ConfigPath("click.success") private lateinit var clickSuccess0: Sound

  @ConfigPath("click.error") private lateinit var clickError0: Sound

  @ConfigPath("waypoint.created") private lateinit var waypointCreated0: Sound

  @ConfigPath("waypoint.selected") private lateinit var waypointSelected0: Sound

  @ConfigPath("player.selected") private lateinit var playerSelected0: Sound

  @ConfigPath("player.notification") private lateinit var playerNotification0: Sound

  @ConfigPath("teleport") private lateinit var teleport0: Sound

  val openGui: Sound
    get() = Sound.sound(openGui0).seed(seed).build()

  val clickNormal: Sound
    get() = Sound.sound(clickNormal0).seed(seed).build()

  val clickDanger: Sound
    get() = Sound.sound(clickDanger0).seed(seed).build()

  val clickDangerAbort: Sound
    get() = Sound.sound(clickDangerAbort0).seed(seed).build()

  val clickSuccess: Sound
    get() = Sound.sound(clickSuccess0).seed(seed).build()

  val clickError: Sound
    get() = Sound.sound(clickError0).seed(seed).build()

  val waypointCreated: Sound
    get() = Sound.sound(waypointCreated0).seed(seed).build()

  val waypointSelected: Sound
    get() = Sound.sound(waypointSelected0).seed(seed).build()

  val playerSelected: Sound
    get() = Sound.sound(playerSelected0).seed(seed).build()

  val playerNotification: Sound
    get() = Sound.sound(playerNotification0).seed(seed).build()

  val teleport: Sound
    get() = Sound.sound(teleport0).seed(seed).build()
}
