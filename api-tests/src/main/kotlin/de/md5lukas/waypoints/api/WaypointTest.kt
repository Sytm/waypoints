package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.event.WaypointPostDeleteEvent
import de.md5lukas.waypoints.api.event.WaypointPreDeleteEvent
import java.sql.SQLException
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.*
import kotlinx.coroutines.runBlocking
import org.bukkit.Material
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

abstract class WaypointTest : TestBase() {

  @TypesNoDeath
  fun deleteWaypoint(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

    assertEquals(1, holder.getWaypointsAmount())

    waypoint.delete()

    server.pluginManager.assertEventFired(WaypointPreDeleteEvent::class.java)
    server.pluginManager.assertEventFired(WaypointPostDeleteEvent::class.java)

    assertEquals(0, holder.getWaypointsAmount())
  }

  @TypesNoDeath
  fun propertiesSaved(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    var waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

    waypoint.setName("Other name")
    waypoint.setDescription("Some description")
    if (type === Type.PERMISSION) {
      waypoint.setPermission("permission")
    }
    waypoint.setMaterial(Material.GRASS_BLOCK)
    waypoint.setBeaconColor(Material.LIGHT_GRAY_STAINED_GLASS)

    waypoint = holder.getWaypoints()[0]

    assertAll(
        { assertEquals("Other name", waypoint.name) },
        { assertEquals("Some description", waypoint.description) },
        {
          if (type === Type.PERMISSION) {
            assertEquals("permission", waypoint.permission)
          }
        },
        { assertEquals(Material.GRASS_BLOCK, waypoint.material) },
        { assertEquals(Material.LIGHT_GRAY_STAINED_GLASS, waypoint.beaconColor) })
  }

  @Nested
  inner class WaypointMeta {

    @TypesNoDeath
    fun newMetaDefaultValues(type: Type) = runBlocking {
      val holder = api.holderOfType(type)
      val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      val player = UUID.randomUUID()
      api.getWaypointPlayer(player)

      assertEquals(0, waypoint.getWaypointMeta(player).teleportations)
    }

    @TypesNoDeath
    fun propertiesSaved(type: Type) = runBlocking {
      val holder = api.holderOfType(type)
      val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      val player = UUID.randomUUID()
      api.getWaypointPlayer(player)

      waypoint.getWaypointMeta(player).setTeleportations(1)

      assertEquals(1, waypoint.getWaypointMeta(player).teleportations)
    }

    @TypesNoDeath
    fun noCrossSaving(type: Type) = runBlocking {
      val holder = api.holderOfType(type)
      val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      val player1 = UUID.randomUUID()
      val player2 = UUID.randomUUID()
      api.getWaypointPlayer(player1)
      api.getWaypointPlayer(player2)

      waypoint.getWaypointMeta(player1).setTeleportations(1)
      waypoint.getWaypointMeta(player2).setTeleportations(2)

      assertEquals(1, waypoint.getWaypointMeta(player1).teleportations)
      assertEquals(2, waypoint.getWaypointMeta(player2).teleportations)
    }
  }

  @Nested
  inner class WaypointCustomData {

    @TypesNoDeath
    fun customDataSaved(type: Type) = runBlocking {
      val holder = api.holderOfType(type)
      val waypoint = holder.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      waypoint.setCustomData("some key", "some data")
      assertEquals("some data", waypoint.getCustomData("some key"))

      waypoint.setCustomData("some key", null)
      assertNull(waypoint.getCustomData("some key"))
    }

    @TypesNoDeath
    fun noInWaypointCrossSave(type: Type) = runBlocking {
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
    fun noOverWaypointCrossSave(type: Type) = runBlocking {
      val holder = api.holderOfType(type)

      val waypoint1 = holder.createWaypoint("Test 1", server.createLocation("world", 1, 2, 3))
      val waypoint2 = holder.createWaypoint("Test 2", server.createLocation("world", 1, 2, 3))

      waypoint1.setCustomData("some key", "some data")
      waypoint2.setCustomData("some key", "some other data")
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

  @Nested
  inner class WaypointShares {
    @Test
    fun noSharesByDefault() = runBlocking {
      val player = api.getWaypointPlayer(UUID.randomUUID())
      val waypoint = player.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      assertEquals(0, player.getSharingWaypoints().size)
      assertFalse(player.hasSharedWaypoints())
      assertEquals(0, player.getSharedWaypoints().size)
      assertEquals(0, waypoint.getSharedWith().size)
    }

    @Test
    fun playerMustExistToShare() = runBlocking {
      val player = api.getWaypointPlayer(UUID.randomUUID())
      val waypoint = player.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      assertThrows<SQLException> { waypoint.shareWith(UUID.randomUUID()) }

      assertEquals(0, player.getSharingWaypoints().size)
      assertFalse(player.hasSharedWaypoints())
      assertEquals(0, player.getSharedWaypoints().size)
      assertEquals(0, waypoint.getSharedWith().size)
    }

    @Test
    fun shareIsStored() = runBlocking {
      val player1 = api.getWaypointPlayer(UUID.randomUUID())
      val player2 = api.getWaypointPlayer(UUID.randomUUID())
      val waypoint = player1.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      waypoint.shareWith(player2.id)

      assertEquals(1, player1.getSharingWaypoints().size)
      assertFalse(player1.hasSharedWaypoints())
      assertEquals(0, player1.getSharedWaypoints().size)
      assertEquals(1, waypoint.getSharedWith().size)

      val player2shared = player2.getSharedWaypoints()
      assertEquals(0, player2.getSharingWaypoints().size)
      assertTrue(player2.hasSharedWaypoints())
      assertEquals(1, player2shared.size)

      val share = player2shared.first()
      assertEquals(player1.id, share.owner)
      assertEquals(player2.id, share.sharedWith)
      assertEquals(waypoint.id, share.waypointId)
      assertNull(share.expires)
      assertEquals(waypoint, share.getWaypoint())
    }

    @Test
    fun shareExpires() = runBlocking {
      val player1 = api.getWaypointPlayer(UUID.randomUUID())
      val player2 = api.getWaypointPlayer(UUID.randomUUID())
      val waypoint = player1.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      waypoint.shareWith(player2.id, OffsetDateTime.now().minusMinutes(1))

      assertBothPlayersNoWaypoints(player1, player2)
      assertEquals(0, waypoint.getSharedWith().size)
    }

    @Test
    fun shareCanBeDeleted() = runBlocking {
      val player1 = api.getWaypointPlayer(UUID.randomUUID())
      val player2 = api.getWaypointPlayer(UUID.randomUUID())
      val waypoint = player1.createWaypoint("Test", server.createLocation("world", 1, 2, 3))

      waypoint.shareWith(player2.id)

      val waypointShared = waypoint.getSharedWith()

      assertEquals(1, player1.getSharingWaypoints().size)
      assertFalse(player1.hasSharedWaypoints())
      assertEquals(0, player1.getSharedWaypoints().size)
      assertEquals(1, waypointShared.size)

      assertEquals(0, player2.getSharingWaypoints().size)
      assertTrue(player2.hasSharedWaypoints())
      assertEquals(1, player2.getSharedWaypoints().size)

      waypointShared.first().delete()

      assertBothPlayersNoWaypoints(player1, player2)
      assertEquals(0, waypoint.getSharedWith().size)
    }
  }

  private suspend fun assertBothPlayersNoWaypoints(
      player1: WaypointsPlayer,
      player2: WaypointsPlayer,
  ) {
    assertEquals(0, player1.getSharingWaypoints().size)
    assertFalse(player1.hasSharedWaypoints())
    assertEquals(0, player1.getSharedWaypoints().size)

    assertEquals(0, player2.getSharingWaypoints().size)
    assertFalse(player2.hasSharedWaypoints())
    assertEquals(0, player2.getSharedWaypoints().size)
  }
}
