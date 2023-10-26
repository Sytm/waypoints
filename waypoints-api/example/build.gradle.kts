plugins { with(libs.plugins) { alias(kotlin) } }

dependencies {
  api(libs.paper)
  api(libs.stdlib)
  implementation(libs.coroutines)
  implementation(project(":waypoints-api"))
  implementation(project(":pointers"))
}
