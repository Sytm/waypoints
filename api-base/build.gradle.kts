plugins {
    kotlin("jvm")
}

dependencies {
    val paperVersion: String by project
    api("io.papermc.paper:paper-api:$paperVersion")

    implementation(kotlin("stdlib-jdk8"))
    api(project(":waypoints-api"))
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}