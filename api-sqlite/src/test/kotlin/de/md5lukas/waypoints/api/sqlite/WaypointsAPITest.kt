package de.md5lukas.waypoints.api.sqlite

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import de.md5lukas.waypoints.api.*
import java.util.*
import kotlin.test.*

class WaypointsAPITest {

    lateinit var server: ServerMock
    lateinit var api: WaypointsAPI

    @BeforeTest
    fun createAPI() {
        server = MockBukkit.mock()
        val manager = SQLiteManager(MockBukkit.createMockPlugin(), DummyDatabaseConfiguration, null, DummyPointerManager, true)
        manager.initDatabase()
        api = manager.api
    }

    @AfterTest
    fun unmock() {
        MockBukkit.unmock()
    }

    @Test
    fun newPlayerDoesNotExist() {
        assertFalse(api.waypointsPlayerExists(UUID.randomUUID()))
    }

    @Test
    fun requestedPlayerExists() {
        val id = UUID.randomUUID()
        api.getWaypointPlayer(id)
        assertTrue(api.waypointsPlayerExists(id))
    }

    @Test
    fun waypointNotFoundIfNoneCreated() {
        assertNull(api.getWaypointByID(UUID.randomUUID()))
    }

    @Test
    fun waypointFoundIfPrivateWaypoint() {
        val waypoint = api.getWaypointPlayer(UUID.randomUUID()).createWaypoint("Test", server.createLocation("world", 1, 2, 3)).id

        assertNotNull(api.getWaypointByID(waypoint))
    }
}