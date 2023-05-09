package de.md5lukas.waypoints.api.sqlite

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.MockPlugin
import be.seeseemelk.mockbukkit.ServerMock
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.event.FolderCreateEvent
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import kotlin.test.*
import kotlinx.coroutines.runBlocking

class WaypointHolderTest {

  private val TEST_PERMISSION = "permission"

  private lateinit var server: ServerMock
  private lateinit var plugin: MockPlugin
  private lateinit var api: WaypointsAPI

  @BeforeTest
  fun createAPI() {
    server = MockBukkit.mock()
    plugin = MockBukkit.createMockPlugin()
    val manager = SQLiteManager(plugin, DummyDatabaseConfiguration, null, true)
    manager.initDatabase()
    api = manager.api
  }

  @AfterTest
  fun unmock() {
    MockBukkit.unmock()
  }

  @TypesNoDeath
  fun newHolderEmpty(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    assertEquals(0, holder.getWaypoints().size)
    assertEquals(0, holder.getWaypointsAmount())
    assertEquals(0, holder.getFolders().size)
    assertEquals(0, holder.getFoldersAmount())
    assertEquals(0, holder.getAllWaypoints().size)

    assertEquals(0, holder.getWaypointsVisibleForPlayer(server.addPlayer()))
  }

  @TypesNoDeath
  fun createWaypoint(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    val location = server.createLocation("world", 1, 2, 3)

    val id =
        holder
            .createWaypoint("Test", location)
            .also {
              if (type === Type.PERMISSION) {
                it.setPermission(TEST_PERMISSION)
              }
            }
            .id
    server.pluginManager.assertEventFired(WaypointCreateEvent::class.java)

    assertEquals(1, holder.getWaypoints().size)
    assertEquals(1, holder.getWaypointsAmount())
    assertEquals(1, holder.getAllWaypoints().size)

    assertEquals(id, holder.getWaypoints()[0].id)

    if (type === Type.PERMISSION) {
      assertEquals(0, holder.getWaypointsVisibleForPlayer(server.addPlayer()))
      assertEquals(
          1,
          holder.getWaypointsVisibleForPlayer(
              server.addPlayer().also { it.addAttachment(plugin, TEST_PERMISSION, true) }))
    }
  }

  @TypesNoDeath
  fun checkDuplicateWaypointNames(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    val location = server.createLocation("world", 1, 2, 3)

    holder.createWaypoint("Test", location)

    assertFalse(holder.isDuplicateWaypointName("Other name"))
    assertTrue(holder.isDuplicateWaypointName("Test"))
    assertTrue(holder.isDuplicateWaypointName("tEsT"))
  }

  @TypesNoDeath
  fun createFolder(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    val id = holder.createFolder("Test").id
    server.pluginManager.assertEventFired(FolderCreateEvent::class.java)

    assertEquals(1, holder.getFolders().size)
    assertEquals(1, holder.getFoldersAmount())

    assertEquals(id, holder.getFolders()[0].id)
  }

  @TypesNoDeath
  fun checkDuplicateFolderNames(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    holder.createFolder("Test")

    assertFalse(holder.isDuplicateFolderName("Other name"))
    assertTrue(holder.isDuplicateFolderName("Test"))
    assertTrue(holder.isDuplicateFolderName("tEsT"))
  }

  @TypesNoDeath
  fun moveWaypointIntoFolder(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    val folder = holder.createFolder("Test")

    val location = server.createLocation("world", 1, 2, 3)

    val waypoint =
        holder.createWaypoint("Test", location).also {
          if (type === Type.PERMISSION) {
            it.setPermission(TEST_PERMISSION)
          }
          it.setFolder(folder)
        }

    assertEquals(0, holder.getWaypoints().size)
    assertEquals(1, holder.getWaypointsAmount())
    assertEquals(1, holder.getFolders().size)
    assertEquals(1, holder.getFoldersAmount())
    assertEquals(1, holder.getAllWaypoints().size)

    assertEquals(waypoint.getFolder(), folder)

    if (type === Type.PERMISSION) {
      assertEquals(0, holder.getWaypointsVisibleForPlayer(server.addPlayer()))
      assertEquals(
          1,
          holder.getWaypointsVisibleForPlayer(
              server.addPlayer().also { it.addAttachment(plugin, TEST_PERMISSION, true) }))
    }
  }
}
