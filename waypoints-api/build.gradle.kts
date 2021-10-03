import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "de.md5lukas"
version = "3.0.0-SNAPSHOT"
description = "Waypoints api"

dependencies {
    api("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    api(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}