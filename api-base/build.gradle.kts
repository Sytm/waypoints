import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "de.md5lukas"
version = parent!!.version

val spigotVersion: String by project
val jvmTarget: String by project

repositories {
    mavenLocal()
    mavenCentral()

    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api("org.spigotmc:spigot-api:${spigotVersion}")

    implementation(kotlin("stdlib-jdk8"))
    api(project(":waypoints-api"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTarget
}