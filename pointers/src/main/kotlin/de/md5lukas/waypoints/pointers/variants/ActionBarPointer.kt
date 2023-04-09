package de.md5lukas.waypoints.pointers.variants

import de.md5lukas.waypoints.pointers.Pointer
import de.md5lukas.waypoints.pointers.PointerManager
import de.md5lukas.waypoints.pointers.Trackable
import de.md5lukas.waypoints.pointers.config.ActionBarConfiguration
import de.md5lukas.waypoints.pointers.util.getAngleToTarget
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.math.roundToInt

internal class ActionBarPointer(
    pointerManager: PointerManager,
    private val config: ActionBarConfiguration,
) : Pointer(pointerManager, config.interval) {

    override fun update(player: Player, trackable: Trackable, translatedTarget: Location?) {
        player.sendActionBar(
            if (translatedTarget !== null) {
                if (config.showDistanceEnabled && player.isSneaking) {
                    pointerManager.hooks.actionBarHooks.formatDistanceMessage(
                        player,
                        player.location.distance(translatedTarget),
                        player.location.y - trackable.location.y
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
                pointerManager.hooks.actionBarHooks.formatWrongWorldMessage(player, player.world, trackable.location.world!!)
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