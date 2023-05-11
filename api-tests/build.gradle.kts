plugins { with(libs.plugins) { alias(kotlin) } }

dependencies {
  api(libs.paper)

  implementation(libs.coroutines)

  api(libs.stdlib)
  api(project(":api-base"))

  api(kotlin("test-junit5"))
  api(libs.junitJupiter)
  api(libs.mockBukkit)
}

kotlin { jvmToolchain(libs.versions.jvmToolchain.get().toInt()) }
