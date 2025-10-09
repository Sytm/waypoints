package de.md5lukas.waypoints.data

import de.md5lukas.waypoints.api.Icon
import de.md5lukas.waypoints.api.Type
import de.md5lukas.waypoints.api.event.FolderPostDeleteEvent
import de.md5lukas.waypoints.api.event.FolderPreDeleteEvent
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.bukkit.inventory.ItemType
import org.junit.jupiter.api.Disabled
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
    val grassBlock: ItemType = ItemType.GRASS_BLOCK
    folder.setIcon(Icon.Default(grassBlock, null))

    folder = holder.getFolders()[0]

    assertAll(
        { assertEquals("Other name", folder.name) },
        { assertEquals("Some description", folder.description) },
        { assertEquals(Icon.Default(grassBlock, null), folder.icon) })
  }

  @Disabled("DataComponentAPI not implemented")
  @TypesNoDeath
  fun customModelDataSaved(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    var folder = holder.createFolder("Test")

    val grassBlock: ItemType = ItemType.GRASS_BLOCK
    val customModelData = """{"floats": [ 0.2, 0.1, 200 ]}"""
    folder.setIcon(Icon.Default(grassBlock, customModelData))

    folder = holder.getFolders()[0]

    assertEquals(Icon.Default(grassBlock, customModelData), folder.icon)
  }

  @Disabled("DataComponentAPI not implemented")
  @TypesNoDeath
  fun playerHeadSaved(type: Type) = runBlocking {
    val holder = api.holderOfType(type)

    var folder = holder.createFolder("Test")

    val texture = "96775476bf1ca6c730cd9dfc8675a4f497ce7aa5d401098373c8eca177159c79"
    folder.setIcon(Icon.PlayerHead(texture))

    folder = holder.getFolders()[0]

    assertEquals(Icon.PlayerHead(texture), folder.icon)
  }
}
