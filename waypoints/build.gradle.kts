import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "de.md5lukas"
version = "3.0.0-SNAPSHOT"
description = "Waypoints plugin"

dependencies {
    implementation("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":waypoints-api"))

    implementation("de.md5lukas:painventories:1.0.0-SNAPSHOT")
    implementation("de.md5lukas:sqlite-kotlin-helper:1.0.0")
}

tasks.withType<ProcessResources> {
    // Force refresh, because gradle does not detect changes in the variables used by expand
    this.outputs.upToDateWhen { false }
    expand("version" to project.version)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    dependencies {
        include(dependency("org.jetbrains.kotlin::"))

        include(project(":waypoints-api"))

        include(dependency("de.md5lukas:sqlite-kotlin-helper"))
    }

    relocate("kotlin", "de.md5lukas.waypoints.lib.kt")
    relocate("de.md5lukas.jdbc", "de.md5lukas.waypoints.lib.jdbc")
}
