package de.md5lukas.waypoints.api

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.MockPlugin
import be.seeseemelk.mockbukkit.ServerMock
import de.md5lukas.waypoints.api.base.DatabaseManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class TestBase {

  protected lateinit var server: ServerMock
  protected lateinit var plugin: MockPlugin
  protected lateinit var api: WaypointsAPI

  @BeforeEach
  fun createAPI() {
    server = MockBukkit.mock()
    plugin = MockBukkit.createMockPlugin()
    val manager = createDatabaseManager()
    manager.initDatabase()
    api = manager.api
  }

  abstract fun createDatabaseManager(): DatabaseManager

  @AfterEach
  fun unmock() {
    MockBukkit.unmock()
  }
}
