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

    implementation("de.md5lukas:sqlite-kotlin-helper:1.0.1")

    implementation(kotlin("stdlib-jdk8"))
    api(project(":api-base"))
    api(project(":utils"))
    implementation("de.md5lukas:md5-commons:2.0.0-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTarget
}