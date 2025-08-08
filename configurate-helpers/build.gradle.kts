plugins { with(libs.plugins) { alias(kotlin) } }

kotlin { jvmToolchain(21) }

dependencies {
  implementation(libs.paper)
  implementation(libs.configurate.core)
}
