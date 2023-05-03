plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.paper)

    implementation(kotlin("stdlib"))
    implementation(libs.coroutines)
    implementation(libs.skedule)

    api(project(":waypoints-api"))
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}