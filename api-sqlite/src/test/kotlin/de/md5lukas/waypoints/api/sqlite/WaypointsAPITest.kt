package de.md5lukas.waypoints.api.sqlite

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import de.md5lukas.waypoints.api.DummyDatabaseConfiguration
import de.md5lukas.waypoints.api.SQLiteManager
import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.api.createLocation
import java.util.*
import kotlin.test.*
import kotlinx.coroutines.runBlocking

class WaypointsAPITest {

  lateinit var server: ServerMock
  lateinit var api: WaypointsAPI

  @BeforeTest
  fun createAPI() {
    server = MockBukkit.mock()
    val manager =
        SQLiteManager(MockBukkit.createMockPlugin(), DummyDatabaseConfiguration, null, true)
    manager.initDatabase()
    api = manager.api
  }

  @AfterTest
  fun unmock() {
    MockBukkit.unmock()
  }

  @Test
  fun newPlayerDoesNotExist() = runBlocking {
    assertFalse(api.waypointsPlayerExists(UUID.randomUUID()))
  }

  @Test
  fun requestedPlayerExists() = runBlocking {
    val id = UUID.randomUUID()
    api.getWaypointPlayer(id)
    assertTrue(api.waypointsPlayerExists(id))
  }

  @Test
  fun waypointNotFoundIfNoneCreated() = runBlocking {
    assertNull(api.getWaypointByID(UUID.randomUUID()))
  }

  @Test
  fun waypointFoundIfPrivateWaypoint(): Unit = runBlocking {
    val waypoint =
        api.getWaypointPlayer(UUID.randomUUID())
            .createWaypoint("Test", server.createLocation("world", 1, 2, 3))
            .id

    assertNotNull(api.getWaypointByID(waypoint))
  }
}
