import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "de.md5lukas"
version = parent!!.version

dependencies {
    implementation("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":waypoints-api"))

    implementation("de.md5lukas:nbt:1.2.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    dependencies {
        include(dependency("de.md5lukas:nbt"))
    }

    relocate("de.md5lukas.nbt", "de.md5lukas.waypoints.legacy.nbt")
}