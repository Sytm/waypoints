package de.md5lukas.waypoints.api.sqlite

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.api.event.WaypointPreDeleteEvent
import de.md5lukas.waypoints.pointers.BeaconColor
import org.bukkit.Material
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.test.*

class WaypointTest {

    lateinit var server: ServerMock
    lateinit var api: WaypointsAPI

    @BeforeTest
    fun createAPI() {
        server = MockBukkit.mock()
        val manager = SQLiteManager(MockBukkit.createMockPlugin(), DummyDatabaseConfiguration, null, true)
        manager.initDatabase()
        api = manager.api
    }

    @AfterTest
    fun unmock() {
        MockBukkit.unmock()
    }

    @TypesNoDeath
    fun deleteWaypoint(type: Type) {
        val holder = api.holderOfType(type)

        val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

        assertEquals(1, holder.waypointsAmount)

        waypoint.delete()

        server.pluginManager.assertEventFired(WaypointPreDeleteEvent::class.java)
        server.pluginManager.assertEventFired(WaypointPostDeleteEvent::class.java)

        assertEquals(0, holder.waypointsAmount)
    }

    @TypesNoDeath
    fun propertiesSaved(type: Type) {
        val holder = api.holderOfType(type)

        var waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

        waypoint.name = "Other name"
        waypoint.description = "Some description"
        waypoint.permission = "permission"
        waypoint.material = Material.GRASS_BLOCK
        waypoint.beaconColor = BeaconColor.LIGHT_GRAY

        waypoint = holder.waypoints[0]

        assertAll({
            assertEquals("Other name", waypoint.name)
        }, {
            assertEquals("Some description", waypoint.description)
        }, {
            assertEquals("permission", waypoint.permission)
        }, {
            assertEquals(Material.GRASS_BLOCK, waypoint.material)
        }, {
            assertEquals(BeaconColor.LIGHT_GRAY, waypoint.beaconColor)
        })
    }

    @Nested
    inner class WaypointsMeta {

        @TypesNoDeath
        fun newMetaDefaultValues(type: Type) {
            val holder = api.holderOfType(type)
            val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

            val player = UUID.randomUUID()
            api.getWaypointPlayer(player)

            assertEquals(0, waypoint.getWaypointMeta(player).teleportations)
        }

        @TypesNoDeath
        fun propertiesSaved(type: Type) {
            val holder = api.holderOfType(type)
            val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

            val player = UUID.randomUUID()
            api.getWaypointPlayer(player)

            waypoint.getWaypointMeta(player).teleportations = 1

            assertEquals(1, waypoint.getWaypointMeta(player).teleportations)
        }

        @TypesNoDeath
        fun noCrossSaving(type: Type) {
            val holder = api.holderOfType(type)
            val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

            val player1 = UUID.randomUUID()
            val player2 = UUID.randomUUID()
            api.getWaypointPlayer(player1)
            api.getWaypointPlayer(player2)

            waypoint.getWaypointMeta(player1).teleportations = 1
            waypoint.getWaypointMeta(player2).teleportations = 2

            assertEquals(1, waypoint.getWaypointMeta(player1).teleportations)
            assertEquals(2, waypoint.getWaypointMeta(player2).teleportations)
        }
    }

    @Nested
    inner class WaypointsCustomData {

        @TypesNoDeath
        fun customDataSaved(type: Type) {
            val holder = api.holderOfType(type)
            val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

            waypoint.setCustomData("some key", "some data")
            assertEquals("some data", waypoint.getCustomData("some key"))

            waypoint.setCustomData("some key", null)
            assertNull(waypoint.getCustomData("some key"))
        }

        @TypesNoDeath
        fun noInWaypointCrossSave(type: Type) {
            val holder = api.holderOfType(type)
            val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

            waypoint.setCustomData("some key", "some data")
            waypoint.setCustomData("some other key", "some other data")
            assertEquals("some data", waypoint.getCustomData("some key"))
            assertEquals("some other data", waypoint.getCustomData("some other key"))

            waypoint.setCustomData("some key", null)
            assertNull(waypoint.getCustomData("some key"))
            assertNotNull(waypoint.getCustomData("some other key"))

            waypoint.setCustomData("some other key", null)
            assertNull(waypoint.getCustomData("some key"))
            assertNull(waypoint.getCustomData("some other key"))
        }

        @TypesNoDeath
        fun noOverWaypointCrossSave(type: Type) {
            val holder = api.holderOfType(type)

            val waypoint1 = holder.createWaypoint("Test 1", server.createLocation("world", 1, 2, 3))
            val waypoint2 = holder.createWaypoint("Test 2", server.createLocation("world", 1, 2, 3))

            waypoint1.setCustomData("some key", "some data")
            waypoint2.setCustomData("some key", "some other data");
            assertEquals("some data", waypoint1.getCustomData("some key"))
            assertEquals("some other data", waypoint2.getCustomData("some key"))

            waypoint1.setCustomData("some key", null)
            assertNull(waypoint1.getCustomData("some key"))
            assertNotNull(waypoint2.getCustomData("some key"))

            waypoint2.setCustomData("some key", null)
            assertNull(waypoint1.getCustomData("some key"))
            assertNull(waypoint2.getCustomData("some key"))
        }
    }
}