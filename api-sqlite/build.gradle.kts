plugins {
  with(libs.plugins) {
    alias(kotlin)
    alias(shadow)
  }
}

dependencies {
  api(libs.paper)

  implementation(libs.coroutines)
  implementation(libs.sqliteHelper)

  api(libs.stdlib)
  api(project(":api-base"))
  implementation(libs.md5Commons)

  testImplementation(project(":api-tests"))
}

kotlin { jvmToolchain(libs.versions.jvmToolchain.get().toInt()) }

tasks {
  shadowJar {
    archiveClassifier.set("")

    dependencies { include(dependency(libs.sqliteHelper.get())) }

    relocate("de.md5lukas.jdbc", "de.md5lukas.waypoints.api.sqlite.jdbc")
  }

  test {
    useJUnitPlatform()

    testLogging { events("passed", "skipped", "failed") }
  }
}
