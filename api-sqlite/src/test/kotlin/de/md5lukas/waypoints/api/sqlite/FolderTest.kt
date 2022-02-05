package de.md5lukas.waypoints.api.sqlite

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import de.md5lukas.waypoints.api.*
import de.md5lukas.waypoints.api.event.FolderPostDeleteEvent
import de.md5lukas.waypoints.api.event.FolderPreDeleteEvent
import org.bukkit.Material
import org.junit.jupiter.api.assertAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class FolderTest {

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

    @TypesNoDeath
    fun deleteFolder(type: Type) {
        val holder = api.holderOfType(type)

        val folder = holder.createFolder("Test")

        assertEquals(1, holder.foldersAmount)

        folder.delete()

        server.pluginManager.assertEventFired(FolderPreDeleteEvent::class.java)
        server.pluginManager.assertEventFired(FolderPostDeleteEvent::class.java)

        assertEquals(0, holder.foldersAmount)
    }

    @TypesNoDeath
    fun propertiesSaved(type: Type) {
        val holder = api.holderOfType(type)

        var folder = holder.createFolder("Test")

        folder.name = "Other name"
        folder.description = "Some description"
        folder.material = Material.GRASS_BLOCK

        folder = holder.folders[0]

        assertAll({
            assertEquals("Other name", folder.name)
        }, {
            assertEquals("Some description", folder.description)
        }, {
            assertEquals(Material.GRASS_BLOCK, folder.material)
        })
    }
}