plugins { with(libs.plugins) { alias(kotlin) } }

kotlin { jvmToolchain(21) }

dependencies {
  implementation(libs.paper)
  api(libs.configurate.core)
  api(libs.configurate.kotlin)
}
