package de.md5lukas.waypoints.api.sqlite

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WaypointPlayerTest {

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
    fun showGlobalsIsSaved() {
        val id = UUID.randomUUID()
        var player = api.getWaypointPlayer(id)

        val invertedValue = !player.showGlobals
        player.showGlobals = invertedValue

        player = api.getWaypointPlayer(id) // Recreate WaypointsPlayer, to fetch value

        assertEquals(invertedValue, player.showGlobals)
    }

    @ParameterizedTest
    @EnumSource(OverviewSort::class)
    fun overviewSortIsSaved(sortBy: OverviewSort) {
        val id = UUID.randomUUID()
        var player = api.getWaypointPlayer(id)

        player.sortBy = sortBy

        player = api.getWaypointPlayer(id) // Recreate WaypointsPlayer, to fetch value

        assertEquals(sortBy, player.sortBy)
    }

    @Test
    fun newPlayerHasNoCompassTarget() {
        val player = api.getWaypointPlayer(UUID.randomUUID())

        assertNull(player.getCompassTarget())
    }

    @Test
    fun compassTargetIsSaved() {
        val location = server.createLocation("world", 1, 2, 3)

        val player = api.getWaypointPlayer(UUID.randomUUID())

        player.setCompassTarget(location)

        assertEquals(location, player.getCompassTarget())
    }

    @Test
    fun compassTargetIsOverwritten() {
        val location = server.createLocation("world", 1, 2, 3)
        val location2 = server.createLocation("world", 2, 3, 4)

        val player = api.getWaypointPlayer(UUID.randomUUID())

        player.setCompassTarget(location)
        player.setCompassTarget(location2)

        assertEquals(location2, player.getCompassTarget())
    }

    @ParameterizedTest
    @EnumSource(Type::class)
    fun newPlayerHasNoCooldown(type: Type) {
        val player = api.getWaypointPlayer(UUID.randomUUID())

        assertNull(player.getCooldownUntil(type))
    }

    @ParameterizedTest
    @EnumSource(Type::class)
    fun sameCooldownUntilIsReturned(type: Type) {
        val now = OffsetDateTime.now()

        val player = api.getWaypointPlayer(UUID.randomUUID())

        player.setCooldownUntil(type, now)

        assertEquals(now, player.getCooldownUntil(type))
    }

    @ParameterizedTest
    @EnumSource(Type::class)
    fun cooldownIsOverwritten(type: Type) {
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
        fun newPlayerDeathFolderEmpty() {
            val player = api.getWaypointPlayer(UUID.randomUUID())

            val deathFolder = player.deathFolder
            assertEquals(0, deathFolder.amount)
            assertEquals(0, deathFolder.waypoints.size)
        }

        @Test
        fun deathLocationSaved() {
            val location = server.createLocation("world", 1, 2, 3)

            val player = api.getWaypointPlayer(UUID.randomUUID())

            player.addDeathLocation(location)
            server.pluginManager.assertEventFired(WaypointCreateEvent::class.java)

            val deathFolder = player.deathFolder
            assertEquals(1, deathFolder.amount)
            val waypoints = deathFolder.waypoints
            assertEquals(1, waypoints.size)
            assertEquals(location, waypoints[0].location)
        }

        @Test
        fun deathFolderCannotBeDeleted() {
            assertThrows<UnsupportedOperationException> {
                api.getWaypointPlayer(UUID.randomUUID()).deathFolder.delete()
            }
        }
    }
}