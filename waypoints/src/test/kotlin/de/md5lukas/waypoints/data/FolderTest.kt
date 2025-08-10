package de.md5lukas.waypoints.data

import de.md5lukas.waypoints.api.Icon
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.event.FolderPostDeleteEvent
import de.md5lukas.waypoints.api.event.FolderPreDeleteEvent
import de.md5lukas.waypoints.util.Items
import de.md5lukas.waypoints.util.getValue
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertAll

class FolderTest : TestBase() {

  @TypesNoDeath
  fun deleteFolder(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    val folder = holder.createFolder("Test")

    assertEquals(1, holder.getFoldersAmount())

    folder.delete()

    server.pluginManager.assertEvent<FolderPreDeleteEvent>()
    server.pluginManager.assertEvent<FolderPostDeleteEvent>()

    assertEquals(0, holder.getFoldersAmount())
  }

  @TypesNoDeath
  fun propertiesSaved(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    var folder = holder.createFolder("Test")

    folder.setName("Other name")
    folder.setDescription("Some description")
    val grassBlock = Items.GRASS_BLOCK.getValue()
    folder.setIcon(Icon(grassBlock, null, null))

    folder = holder.getFolders()[0]

    assertAll(
        { assertEquals("Other name", folder.name) },
        { assertEquals("Some description", folder.description) },
        { assertEquals(Icon(grassBlock, null, null), folder.icon) })
  }

  @TypesNoDeath
  fun customModelDataSaved(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    var folder = holder.createFolder("Test")

    val grassBlock = Items.GRASS_BLOCK.getValue()
    val customModelData = """{"floats": [ 0.2, 0.1, 200 ]}"""
    val texture = "96775476bf1ca6c730cd9dfc8675a4f497ce7aa5d401098373c8eca177159c79"
    folder.setIcon(Icon(grassBlock, customModelData, texture))

    folder = holder.getFolders()[0]

    assertEquals(Icon(grassBlock, customModelData, texture), folder.icon)
  }
}
