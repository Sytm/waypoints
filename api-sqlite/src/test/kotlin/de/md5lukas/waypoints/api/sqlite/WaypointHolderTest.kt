package de.md5lukas.waypoints.api.sqlite

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.MockPlugin
import be.seeseemelk.mockbukkit.ServerMock
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.event.FolderCreateEvent
import de.md5lukas.waypoints.api.event.WaypointCreateEvent
import kotlin.test.*

class WaypointHolderTest {

    val TEST_PERMISSION = "permission"

    lateinit var server: ServerMock
    lateinit var plugin: MockPlugin
    lateinit var api: WaypointsAPI

    @BeforeTest
    fun createAPI() {
        server = MockBukkit.mock()
        plugin = MockBukkit.createMockPlugin()
        val manager = SQLiteManager(plugin, null, DummyPointerManager(), true)
        manager.initDatabase()
        api = manager.api
    }

    @AfterTest
    fun unmock() {
        MockBukkit.unmock()
    }

    @TypesNoDeath
    fun newHolderEmpty(type: Type) {
        val holder = api.holderOfType(type)

        assertEquals(0, holder.waypoints.size)
        assertEquals(0, holder.waypointsAmount)
        assertEquals(0, holder.folders.size)
        assertEquals(0, holder.foldersAmount)
        assertEquals(0, holder.allWaypoints.size)

        assertEquals(0, holder.getWaypointsVisibleForPlayer(server.addPlayer()))
    }

    @TypesNoDeath
    fun createWaypoint(type: Type) {
        val holder = api.holderOfType(type)

        val location = server.createLocation("world", 1, 2, 3)

        val id = holder.createWaypoint("Test", location).also {
            if (type === Type.PERMISSION) {
                it.permission = TEST_PERMISSION
            }
        }.id
        server.pluginManager.assertEventFired(WaypointCreateEvent::class.java)

        assertEquals(1, holder.waypoints.size)
        assertEquals(1, holder.waypointsAmount)
        assertEquals(1, holder.allWaypoints.size)

        assertEquals(id, holder.waypoints[0].id)

        if (type === Type.PERMISSION) {
            assertEquals(0, holder.getWaypointsVisibleForPlayer(server.addPlayer()))
            assertEquals(1, holder.getWaypointsVisibleForPlayer(server.addPlayer().also {
                it.addAttachment(plugin, TEST_PERMISSION, true)
            }))
        }
    }

    @TypesNoDeath
    fun checkDuplicateWaypointNames(type: Type) {
        val holder = api.holderOfType(type)

        val location = server.createLocation("world", 1, 2, 3)

        holder.createWaypoint("Test", location)

        assertFalse(holder.isDuplicateWaypointName("Other name"))
        assertTrue(holder.isDuplicateWaypointName("Test"))
        assertTrue(holder.isDuplicateWaypointName("tEsT"))
    }

    @TypesNoDeath
    fun createFolder(type: Type) {
        val holder = api.holderOfType(type)

        val id = holder.createFolder("Test").id
        server.pluginManager.assertEventFired(FolderCreateEvent::class.java)

        assertEquals(1, holder.folders.size)
        assertEquals(1, holder.foldersAmount)

        assertEquals(id, holder.folders[0].id)
    }

    @TypesNoDeath
    fun checkDuplicateFolderNames(type: Type) {
        val holder = api.holderOfType(type)

        holder.createFolder("Test")

        assertFalse(holder.isDuplicateFolderName("Other name"))
        assertTrue(holder.isDuplicateFolderName("Test"))
        assertTrue(holder.isDuplicateFolderName("tEsT"))
    }

    @TypesNoDeath
    fun moveWaypointIntoFolder(type: Type) {
        val holder = api.holderOfType(type)

        val folder = holder.createFolder("Test")

        val location = server.createLocation("world", 1, 2, 3)

        val waypoint = holder.createWaypoint("Test", location).also {
            if (type === Type.PERMISSION) {
                it.permission = TEST_PERMISSION
            }
            it.folder = folder
        }

        assertEquals(0, holder.waypoints.size)
        assertEquals(1, holder.waypointsAmount)
        assertEquals(1, holder.folders.size)
        assertEquals(1, holder.foldersAmount)
        assertEquals(1, holder.allWaypoints.size)

        assertEquals(waypoint.folder, folder)

        if (type === Type.PERMISSION) {
            assertEquals(0, holder.getWaypointsVisibleForPlayer(server.addPlayer()))
            assertEquals(1, holder.getWaypointsVisibleForPlayer(server.addPlayer().also {
                it.addAttachment(plugin, TEST_PERMISSION, true)
            }))
        }
    }
}