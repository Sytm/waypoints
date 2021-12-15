import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import de.md5lukas.resourceindex.ResourceIndexTask
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "de.md5lukas"
version = parent!!.version
description = "Waypoints plugin"

val spigotVersion: String by project
val jvmTarget: String by project

repositories {
    mavenLocal()
    mavenCentral()

    maven(url = "https://repo.codemc.io/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://repo.minebench.de/")
    maven(url = "https://jitpack.io")

    maven(url = "https://repo.mikeprimm.com/")
}

dependencies {
    api("org.spigotmc:spigot-api:${spigotVersion}")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":utils"))
    implementation(project(":waypoints-api"))
    implementation(project(":api-base"))
    implementation(project(":api-sqlite", "shadow"))
    implementation(project(":legacy-importer", "shadow"))

    // Dependencies on own projects
    implementation("de.md5lukas:md5-commons:2.0.0-SNAPSHOT")
    implementation("de.md5lukas:kinvs:1.0.0-SNAPSHOT")

    // Required dependencies
    implementation("net.wesjd:anvilgui:1.5.3-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:2.2.1")

    // Optional dependencies
    implementation("com.github.MilkBowl:VaultAPI:1.7.1")

    implementation("us.dynmap:DynmapCoreAPI:3.1:all")
    implementation("us.dynmap:dynmap-api:3.1:unshaded")
    implementation("us.dynmap:spigot:3.1:unshaded") {
        isTransitive = false
    }

    // Test dependencies
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.register("createResourceIndex", ResourceIndexTask::class.java) {
}

tasks.withType<ProcessResources> {
    dependsOn("createResourceIndex")
    // Force refresh, because gradle does not detect changes in the variables used by expand
    this.outputs.upToDateWhen { false }

    filesMatching("**/plugin.yml") {
        // 1.16.5-R0.1-SNAPSHOT
        var apiVersion = spigotVersion.substringBefore('-')
        if (apiVersion.count { it == '.' } > 1) {
            apiVersion = apiVersion.substringBeforeLast('.')
        }

        expand(
            "version" to project.version,
            "kotlinVersion" to getKotlinPluginVersion(),
            "apiVersion" to apiVersion
        )
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTarget
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    minimize {
        // Exclude AnvilGUI because the version specific NMS adapters are loaded via reflection and are not directly referenced
        exclude(dependency("net.wesjd:anvilgui"))

        exclude(project(":waypoints-api"))
        exclude(project(":legacy-importer"))
    }

    exclude("META-INF/")

    dependencies {
        include(project(":utils"))
        include(project(":waypoints-api"))
        include(project(":api-base"))
        include(project(":api-sqlite"))
        include(project(":legacy-importer"))

        include(dependency("de.md5lukas:md5-commons"))
        include(dependency("de.md5lukas:kinvs"))

        include(dependency("net.wesjd:anvilgui"))
        include(dependency("org.bstats::"))
    }

    relocate("de.md5lukas.commons", "de.md5lukas.waypoints.commons")
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
