plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.paper)

    implementation(kotlin("stdlib-jdk8"))
    api(project(":waypoints-api"))
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}