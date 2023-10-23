package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.event.FolderPostDeleteEvent
import de.md5lukas.waypoints.api.event.FolderPreDeleteEvent
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.bukkit.Material
import org.junit.jupiter.api.assertAll

abstract class FolderTest : TestBase() {

  @TypesNoDeath
  fun deleteFolder(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    val folder = holder.createFolder("Test")

    assertEquals(1, holder.getFoldersAmount())

    folder.delete()

    server.pluginManager.assertEventFired(FolderPreDeleteEvent::class.java)
    server.pluginManager.assertEventFired(FolderPostDeleteEvent::class.java)

    assertEquals(0, holder.getFoldersAmount())
  }

  @TypesNoDeath
  fun propertiesSaved(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    var folder = holder.createFolder("Test")

    folder.setName("Other name")
    folder.setDescription("Some description")
    folder.setMaterial(Icon(Material.GRASS_BLOCK, null))

    folder = holder.getFolders()[0]

    assertAll(
        { assertEquals("Other name", folder.name) },
        { assertEquals("Some description", folder.description) },
        { assertEquals(Icon(Material.GRASS_BLOCK, null), folder.material) })
  }

  @TypesNoDeath
  fun customModelDataSaved(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    var folder = holder.createFolder("Test")

    folder.setMaterial(Icon(Material.GRASS_BLOCK, 1))

    folder = holder.getFolders()[0]

    assertEquals(Icon(Material.GRASS_BLOCK, 1), folder.material)
  }
}
