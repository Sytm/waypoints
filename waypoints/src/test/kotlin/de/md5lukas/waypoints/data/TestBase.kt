package de.md5lukas.waypoints.data

import de.md5lukas.waypoints.api.WaypointsAPI
import de.md5lukas.waypoints.config.WaypointsConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.plugin.PluginMock

abstract class TestBase {

  protected lateinit var server: ServerMock
  protected lateinit var plugin: PluginMock
  protected lateinit var api: WaypointsAPI

  @BeforeEach
  fun createAPI() {
    server = MockBukkit.mock()
    plugin = MockBukkit.createMockPlugin()
    val manager = SQLiteManager(plugin, WaypointsConfiguration.Database(), null, true)
    manager.initDatabase()
    api = manager.api
  }

  @AfterEach
  fun unmock() {
    MockBukkit.unmock()
  }
}
