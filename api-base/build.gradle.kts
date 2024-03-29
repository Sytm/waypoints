plugins { with(libs.plugins) { alias(kotlin) } }

dependencies {
  api(libs.paper)

  implementation(libs.stdlib)
  implementation(libs.coroutines)

  api(project(":waypoints-api"))
}

kotlin { jvmToolchain(libs.versions.jvmToolchain.get().toInt()) }
