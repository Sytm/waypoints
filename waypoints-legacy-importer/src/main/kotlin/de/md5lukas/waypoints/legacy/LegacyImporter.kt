package de.md5lukas.waypoints.legacy

import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.legacy.internal.*
import java.io.File
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class LegacyImporter(
    private val logger: Logger,
    private val api: WaypointsAPI,
) {

    init {
        setupNBT()
    }

    fun performImport() {
        importGlobals()

        if (BasicPlayerStore.PLAYER_DATA_FOLDER.exists()) {
            BasicPlayerStore.PLAYER_DATA_FOLDER.listFiles()!!.forEach(::importPlayer)
        } else {
            logger.log(Level.INFO, "Could not find any player data in ${BasicPlayerStore.PLAYER_DATA_FOLDER.absolutePath}")
        }
    }

    private fun importPlayer(file: File) {
        val uuid = UUID.fromString(file.nameWithoutExtension)
        logger.log(Level.INFO, "Importing player data for player $uuid")

        val playerData = api.getWaypointPlayer(uuid)

        val legacyPlayerData = LegacyPlayerData(BasicPlayerStore.getPlayerStore(file))

        playerData.showGlobals = legacyPlayerData.settings.showGlobals
        playerData.sortBy = legacyPlayerData.settings.sortMode.overviewSort

        var count = legacyPlayerData.waypoints.size

        legacyPlayerData.deathWaypoint?.let {
            if (it.location.world == null) {
                logger.log(Level.WARNING, "Skipping death waypoint because the world no longer exists")
            } else {
                logger.log(Level.INFO, "Importing death waypoint")
                count++
                playerData.addDeathLocation(it.location)
            }
        }

        legacyPlayerData.waypoints.forEach { legacyWaypoint ->
            if (legacyWaypoint.location.world == null) {
                logger.log(Level.WARNING, "Skipping waypoint ${legacyWaypoint.name} because the world no longer exists")
            } else {
                logger.log(Level.FINE, "Importing private waypoint ${legacyWaypoint.name}")
                playerData.createWaypoint(legacyWaypoint.name, legacyWaypoint.location).also {
                    it.material = legacyWaypoint.material
                    it.beaconColor = legacyWaypoint.beaconColor?.beaconColor
                }
            }
        }

        legacyPlayerData.folders.forEach { legacyFolder ->
            count += legacyFolder.waypoints.size
            logger.log(Level.FINE, "Importing private folder ${legacyFolder.name}")

            val folder = playerData.createFolder(legacyFolder.name)
            folder.material = legacyFolder.material


            legacyFolder.waypoints.forEach { legacyWaypoint ->
                if (legacyWaypoint.location.world == null) {
                    logger.log(Level.WARNING, "Skipping waypoint ${legacyWaypoint.name} because the world no longer exists")
                } else {
                    logger.log(Level.FINE, "Importing private waypoint ${legacyWaypoint.name}")
                    playerData.createWaypoint(legacyWaypoint.name, legacyWaypoint.location).also {
                        it.material = legacyWaypoint.material
                        it.beaconColor = legacyWaypoint.beaconColor?.beaconColor
                        it.folder = folder
                    }
                }
            }
        }

        logger.log(Level.INFO, "Imported $count waypoints")
    }

    private fun importGlobals() {
        val legacyGlobalStore = LegacyGlobalStore(logger)
        if (!legacyGlobalStore.load()) {
            return
        }
        logger.log(Level.INFO, "Importing global waypoints")

        legacyGlobalStore.publicFolder?.also { legacyPublicFolder ->
            logger.log(Level.INFO, "Importing ${legacyPublicFolder.waypoints.size} public waypoints")
            legacyPublicFolder.waypoints.forEach { legacyWaypoint ->
                if (legacyWaypoint.location.world == null) {
                    logger.log(Level.WARNING, "Skipping public waypoint ${legacyWaypoint.name} because the world no longer exists")
                } else {
                    logger.log(Level.FINE, "Importing public waypoint ${legacyWaypoint.name}")
                    api.publicWaypoints.createWaypoint(legacyWaypoint.name, legacyWaypoint.location).also {
                        it.material = legacyWaypoint.material
                        it.beaconColor = legacyWaypoint.beaconColor?.beaconColor
                    }
                }
            }
        }

        legacyGlobalStore.permissionFolder?.also { legacyPermissionFolder ->
            logger.log(Level.INFO, "Importing ${legacyPermissionFolder.waypoints.size} permission waypoints")
            legacyPermissionFolder.waypoints.forEach { legacyWaypoint ->
                if (legacyWaypoint.location.world == null) {
                    logger.log(Level.WARNING, "Skipping permission waypoint ${legacyWaypoint.name} because the world no longer exists")
                } else {
                    logger.log(Level.FINE, "Importing permission waypoint ${legacyWaypoint.name}")
                    api.permissionWaypoints.createWaypoint(legacyWaypoint.name, legacyWaypoint.location).also {
                        it.permission = (legacyWaypoint as LegacyPermissionWaypoint).permission
                        it.material = legacyWaypoint.material
                        it.beaconColor = legacyWaypoint.beaconColor?.beaconColor
                    }
                }
            }
        }
    }
}