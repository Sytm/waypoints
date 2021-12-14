import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
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
    implementation(project(":waypoints-api"))

    implementation("de.md5lukas:nbt:1.2.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTarget
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    dependencies {
        include(dependency("de.md5lukas:nbt"))
    }

    relocate("de.md5lukas.nbt", "de.md5lukas.waypoints.legacy.nbt")
}