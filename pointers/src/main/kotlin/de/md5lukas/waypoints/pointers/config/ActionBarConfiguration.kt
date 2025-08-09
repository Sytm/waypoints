package de.md5lukas.waypoints.pointers.config

import de.md5lukas.configurate.NonEmptyString
import de.md5lukas.configurate.Positive
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.PostProcess

@ConfigSerializable
class ActionBarConfiguration() : RepeatingPointerConfiguration {

  override var enabled = false
    private set

  @Positive
  override var interval = 20
    private set

  @Comment(
      "The indicator color is used to show where the waypoint is by changing the color of one of the sections")
  var indicatorColor: Style = Style.style(NamedTextColor.DARK_RED, TextDecoration.BOLD)
    private set

  @Comment("The background color for the sections")
  var normalColor: Style = Style.style(NamedTextColor.GRAY, TextDecoration.BOLD)
    private set

  @Comment("Character to use as the colored direction indicators")
  @NonEmptyString
  var section: String = "⬛"
    private set

  @Comment(
      "If you turn too much to either side these arrows are highlighted to indicate you need to turn more")
  var arrow = Arrow()
    private set

  @ConfigSerializable
  class Arrow {

    @NonEmptyString
    var left: String = "<-"
      private set

    @NonEmptyString
    var right: String = "->"
      private set
  }

  @Comment(
      "The amount of sections used to show if you are walking in the correct direction. Must be an odd number or the plugin will correct it by adding one")
  @Positive
  var amountOfSections: Int = 35
    private set

  @Comment(
      "The range of between the center section and most outer section in degrees. So the field of view of the action bar indicator is range x 2")
  @Positive
  var range: Int = 70
    private set

  @Comment(
      "Show the distance to the waypoint instead of the direction indicator when the player is sneaking")
  var showDistance: Boolean = true
    private set

  @PostProcess
  fun postProcess() {
    if (amountOfSections % 2 == 0) {
      amountOfSections += 1
    }
  }
}
