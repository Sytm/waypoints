import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
  implementation(project(":utils"))
  implementation(libs.md5Commons)

  testImplementation(kotlin("test-junit5"))
  testImplementation(libs.junitJupiter)
  testImplementation(libs.mockBukkit)
  testRuntimeOnly(libs.sqliteJdbc)
}

kotlin { jvmToolchain(libs.versions.jvmToolchain.get().toInt()) }

tasks.withType<ShadowJar> {
  archiveClassifier.set("")

  dependencies { include(dependency(libs.sqliteHelper.get())) }

  relocate("de.md5lukas.jdbc", "de.md5lukas.waypoints.api.sqlite.jdbc")
}

tasks.withType<Test> {
  useJUnitPlatform()

  testLogging { events("passed", "skipped", "failed") }
}
