plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()

    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    val spigotVersion: String by project
    api("org.spigotmc:spigot-api:$spigotVersion")

    implementation(kotlin("stdlib-jdk8"))
    api(project(":waypoints-api"))
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}