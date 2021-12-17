import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "de.md5lukas"

repositories {
    mavenLocal()
    mavenCentral()

    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    val spigotVersion: String by project

    api("org.spigotmc:spigot-api:$spigotVersion")

    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":waypoints-api"))

    implementation("de.md5lukas:nbt:1.2.2") // Not going to change, so this version stays here
}

tasks.withType<KotlinCompile> {
    val jvmTarget: String by project

    kotlinOptions.jvmTarget = jvmTarget
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    dependencies {
        include(dependency("de.md5lukas:nbt"))
    }

    relocate("de.md5lukas.nbt", "de.md5lukas.waypoints.legacy.nbt")
}