plugins { alias(libs.plugins.kotlin) }

dependencies {
  implementation(libs.ksp)
  implementation(libs.bundles.kotlinPoet)
}
