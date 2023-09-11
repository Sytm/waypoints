package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

abstract class WaypointPlayerTest : TestBase() {

  @Test
  fun showGlobalsIsSaved() = runBlocking {
    val id = UUID.randomUUID()
    var player = api.getWaypointPlayer(id)

    val invertedValue = !player.showGlobals
    player.setShowGlobals(invertedValue)

    player = api.getWaypointPlayer(id) // Recreate WaypointsPlayer, to fetch value

    assertEquals(invertedValue, player.showGlobals)
  }

  @ParameterizedTest
  @EnumSource(OverviewSort::class)
  fun overviewSortIsSaved(sortBy: OverviewSort) = runBlocking {
    val id = UUID.randomUUID()
    var player = api.getWaypointPlayer(id)

    player.setSortBy(sortBy)

    player = api.getWaypointPlayer(id) // Recreate WaypointsPlayer, to fetch value

    assertEquals(sortBy, player.sortBy)
  }

  @Test
  fun enabledPointersAreEmpty() = runBlocking {
    assertTrue(api.getWaypointPlayer(UUID.randomUUID()).enabledPointers.isEmpty())
  }

  @Test
  fun enabledPointersAreSaved() = runBlocking {
    val id = UUID.randomUUID()
    var player = api.getWaypointPlayer(id)

    val map =
        mapOf(
            "type1" to true,
            "type2" to true,
            "type3" to false,
            "type4" to true,
        )

    player.setEnabledPointers(map)

    player = api.getWaypointPlayer(id) // Recreate WaypointsPlayer, to fetch value

    assertEquals(map, player.enabledPointers)
  }

  @Test
  fun newPlayerHasNoSelectedWaypoints() = runBlocking {
    val player = api.getWaypointPlayer(UUID.randomUUID())

    assertTrue(player.getSelectedWaypoints().isEmpty())
  }

  @Test
  fun oneSelectedWaypointIsSaved() = runBlocking {
    val player = api.getWaypointPlayer(UUID.randomUUID())

    val selected = listOf(player.createWaypoint("Test", server.createLocation("world", 1, 2, 3)))

    player.setSelectedWaypoints(selected)

    assertEquals(selected, player.getSelectedWaypoints())
  }

  @Test
  fun multipleSelectedWaypointAreSaved() = runBlocking {
    val player = api.getWaypointPlayer(UUID.randomUUID())

    val selected =
        listOf(
            player.createWaypoint("Test 1", server.createLocation("world", 1, 2, 3)),
            player.createWaypoint("Test 2", server.createLocation("world", 2, 3, 4)),
            player.createWaypoint("Test 3", server.createLocation("world", 3, 4, 5)))

    player.setSelectedWaypoints(selected)

    assertEquals(selected, player.getSelectedWaypoints())
  }

  @Test
  fun selectedWaypointAreOverwritten() = runBlocking {
    val player = api.getWaypointPlayer(UUID.randomUUID())

    val selected =
        mutableListOf(
            player.createWaypoint("Test 1", server.createLocation("world", 1, 2, 3)),
            player.createWaypoint("Test 2", server.createLocation("world", 2, 3, 4)),
            player.createWaypoint("Test 3", server.createLocation("world", 3, 4, 5)),
        )

    player.setSelectedWaypoints(selected)

    assertEquals(selected, player.getSelectedWaypoints())

    selected.removeAt(1)

    player.setSelectedWaypoints(selected)

    assertEquals(selected, player.getSelectedWaypoints())
  }

  @Test
  fun newPlayerHasNoCompassTarget() = runBlocking {
    val player = api.getWaypointPlayer(UUID.randomUUID())

    assertNull(player.getCompassTarget())
  }

  @Test
  fun compassTargetIsSaved() = runBlocking {
    val location = server.createLocation("world", 1, 2, 3)

    val player = api.getWaypointPlayer(UUID.randomUUID())

    player.setCompassTarget(location)

    assertEquals(location, player.getCompassTarget())
  }

  @Test
  fun compassTargetIsOverwritten() = runBlocking {
    val location = server.createLocation("world", 1, 2, 3)
    val location2 = server.createLocation("world", 2, 3, 4)

    val player = api.getWaypointPlayer(UUID.randomUUID())

    player.setCompassTarget(location)
    player.setCompassTarget(location2)

    assertEquals(location2, player.getCompassTarget())
  }

  @ParameterizedTest
  @EnumSource(Type::class)
  fun newPlayerHasNoCooldown(type: Type) = runBlocking {
    val player = api.getWaypointPlayer(UUID.randomUUID())

    assertNull(player.getCooldownUntil(type))
  }

  @ParameterizedTest
  @EnumSource(Type::class)
  fun sameCooldownUntilIsReturned(type: Type) = runBlocking {
    val now = OffsetDateTime.now()

    val player = api.getWaypointPlayer(UUID.randomUUID())

    player.setCooldownUntil(type, now)

    assertEquals(now, player.getCooldownUntil(type))
  }

  @ParameterizedTest
  @EnumSource(Type::class)
  fun cooldownIsOverwritten(type: Type) = runBlocking {
    val now = OffsetDateTime.now()
    val future = now.plusHours(1)

    val player = api.getWaypointPlayer(UUID.randomUUID())

    player.setCooldownUntil(type, now)
    player.setCooldownUntil(type, future)

    assertEquals(future, player.getCooldownUntil(type))
  }

  @Nested
  inner class DeathFolderTest {
    @Test
    fun newPlayerDeathFolderEmpty() = runBlocking {
      val player = api.getWaypointPlayer(UUID.randomUUID())

      val deathFolder = player.deathFolder
      assertEquals(0, deathFolder.getAmount())
      assertEquals(0, deathFolder.getWaypoints().size)
    }

    @Test
    fun deathLocationSaved() = runBlocking {
      val location = server.createLocation("world", 1, 2, 3)

      val player = api.getWaypointPlayer(UUID.randomUUID())

      player.addDeathLocation(location)
      server.pluginManager.assertEventFired(WaypointCreateEvent::class.java)

      val deathFolder = player.deathFolder
      assertEquals(1, deathFolder.getAmount())
      val waypoints = deathFolder.getWaypoints()
      assertEquals(1, waypoints.size)
      assertEquals(location, waypoints[0].location)
    }

    @Test
    fun deletingDeathFolderClearsIt(): Unit = runBlocking {
      val location = server.createLocation("world", 1, 2, 3)

      val player = api.getWaypointPlayer(UUID.randomUUID())

      player.addDeathLocation(location)
      player.addDeathLocation(location)

      val deathFolder = player.deathFolder
      assertEquals(2, deathFolder.getAmount())
      assertEquals(2, deathFolder.getWaypoints().size)

      deathFolder.delete()

      assertEquals(0, deathFolder.getAmount())
      assertEquals(0, deathFolder.getWaypoints().size)
    }
  }
}
