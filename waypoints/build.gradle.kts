import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "de.md5lukas"
version = parent!!.version
description = "Waypoints plugin"

dependencies {
    api("org.spigotmc:spigot-api:${parent!!.ext["spigotVersion"]}")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":waypoints-api"))
    implementation(project(":waypoints-legacy-importer", "shadow"))


    implementation("de.md5lukas:md5-commons:2.0.0-SNAPSHOT")
    implementation("de.md5lukas:sqlite-kotlin-helper:1.0.1")
    implementation("de.md5lukas:kinvs:1.0.0-SNAPSHOT")

    implementation("net.wesjd:anvilgui:1.5.3-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:2.2.1")
    implementation("com.github.MilkBowl:VaultAPI:1.7.1")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.withType<ProcessResources> {
    // Force refresh, because gradle does not detect changes in the variables used by expand
    this.outputs.upToDateWhen { false }

    filesMatching("**/plugin.yml") {
        expand("version" to project.version, "kotlinVersion" to getKotlinPluginVersion())
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    minimize {
        // Exclude AnvilGUI because the version specific NMS adapters are loaded via reflection and are not directly referenced
        exclude(dependency("net.wesjd:anvilgui"))

        exclude(project(":waypoints-api"))
        exclude(project(":waypoints-legacy-importer"))
    }

    dependencies {

        include(project(":waypoints-api"))
        include(project(":waypoints-legacy-importer"))

        include(dependency("de.md5lukas:md5-commons"))
        include(dependency("de.md5lukas:sqlite-kotlin-helper"))
        include(dependency("de.md5lukas:kinvs"))

        include(dependency("net.wesjd:anvilgui"))
        include(dependency("org.bstats::"))
    }

    relocate("de.md5lukas.commons", "de.md5lukas.waypoints.commons")
    relocate("de.md5lukas.jdbc", "de.md5lukas.waypoints.jdbc")
    relocate("de.md5lukas.kinvs", "de.md5lukas.waypoints.kinvs")

    relocate("net.wesjd.anvilgui", "de.md5lukas.waypoints.anvilgui")
    relocate("org.bstats", "de.md5lukas.waypoints.bstats")
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}
