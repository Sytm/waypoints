plugins { `java-library` }

dependencies {
  api(libs.paper)
  compileOnly(libs.annotations)
  implementation(libs.protocollib)
}
