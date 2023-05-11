package de.md5lukas.waypoints.api

import java.util.*
import kotlin.test.*
import kotlinx.coroutines.runBlocking

abstract class WaypointsAPITest : TestBase() {

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
