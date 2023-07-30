plugins { with(libs.plugins) { alias(kotlin) } }

dependencies {
  api(libs.paper)
  api(libs.stdlib)
  implementation(libs.md5Commons)

  // Test dependencies
  testImplementation(kotlin("test-junit5"))
  testImplementation(libs.junitJupiter)
  testRuntimeOnly(libs.junitLauncher)
}

kotlin { jvmToolchain(libs.versions.jvmToolchain.get().toInt()) }

tasks {
  test {
    useJUnitPlatform()

    testLogging { events("passed", "skipped", "failed") }
  }
}
