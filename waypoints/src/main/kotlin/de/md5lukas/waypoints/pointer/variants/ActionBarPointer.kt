package de.md5lukas.waypoints.pointer.variants

import de.md5lukas.waypoints.WaypointsPlugin
import de.md5lukas.waypoints.api.Trackable
import de.md5lukas.waypoints.config.pointers.ActionBarConfiguration
import de.md5lukas.waypoints.pointer.Pointer
import de.md5lukas.waypoints.util.getAngleToTarget
import de.md5lukas.waypoints.util.placeholder
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.math.roundToInt

class ActionBarPointer(
    plugin: WaypointsPlugin,
    private val config: ActionBarConfiguration,
) : Pointer(plugin, config.interval) {

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        player.sendActionBar(
            if (translatedTarget !== null) {
                if (config.showDistanceEnabled && player.isSneaking) {
                    plugin.translations.POINTERS_ACTION_BAR_DISTANCE.withReplacements(
                        "distance3D" placeholder player.location.distance(translatedTarget),
                        "heightDifference" placeholder (player.location.y - trackable.location.y)
                    )
                } else {
                    // TODO properly convert to adventure
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                        generateDirectionIndicator(
                            deltaAngleToTarget(
                                player.location,
                                translatedTarget
                            )
                        )
                    )
                }
            } else {
                plugin.translations.POINTERS_ACTION_BAR_WRONG_WORLD.withReplacements(
                    "current" placeholder plugin.worldTranslations.getWorldName(player.world),
                    "correct" placeholder plugin.worldTranslations.getWorldName(trackable.location.world!!),
                )
            }
        )
    }

    private fun generateDirectionIndicator(angle: Double): String {
        if (angle > config.range) {
            return (config.indicatorColor + config.leftArrow
                    + config.normalColor
                    + config.section.repeat(config.amountOfSections)
                    + config.rightArrow)
        }
        if (-angle > config.range) {
            return (config.normalColor + config.leftArrow
                    + config.section.repeat(config.amountOfSections)
                    + config.indicatorColor
                    + config.rightArrow)
        }

        val percent: Double = -(angle / config.range)

        var nthSection = ((config.amountOfSections - 1).toDouble() / 2 * percent).roundToInt()

        nthSection += (config.amountOfSections.toDouble() / 2).roundToInt()

        return (config.normalColor + config.leftArrow
                + config.section.repeat(nthSection - 1)
                + config.indicatorColor + config.section
                + config.normalColor + config.section.repeat(config.amountOfSections - nthSection)
                + config.rightArrow)
    }

    /**
     * The returned values range from -180 to 180 degrees, where as negative numbers mean you look to much left and
     * positive numbers you look too much right
     *
     * @param location The location to calculate the angle from
     * @param target   The target when looked at the angle is 0
     * @return The delta angle
     */
    private fun deltaAngleToTarget(location: Location, target: Location): Double {
        var playerAngle = (location.yaw + 90).toDouble()

        while (playerAngle < 0) {
            playerAngle += 360.0
        }

        var angle = playerAngle - getAngleToTarget(location, target) + 180

        while (angle > 360) {
            angle -= 360.0
        }

        if (angle > 180) {
            angle = -(360 - angle)
        }

        return angle
    }
}