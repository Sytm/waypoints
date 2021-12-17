import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import de.md5lukas.resourceindex.ResourceIndexTask
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

group = "de.md5lukas"
description = "Waypoints plugin"

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

val spigotVersion: String by project

dependencies {
    val md5CommonsVersion: String by project
    val kinvsVersion: String by project
    val anvilGUIVersion: String by project
    val bstatsVersion: String by project
    val vaultVersion: String by project
    val dynmapVersion: String by project

    api("org.spigotmc:spigot-api:$spigotVersion")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":utils"))
    implementation(project(":waypoints-api"))
    implementation(project(":api-base"))
    implementation(project(":api-sqlite", "shadow"))
    implementation(project(":legacy-importer", "shadow"))

    // Dependencies on own projects
    implementation("de.md5lukas:md5-commons:$md5CommonsVersion")
    implementation("de.md5lukas:kinvs:$kinvsVersion")

    // Required dependencies
    implementation("net.wesjd:anvilgui:$anvilGUIVersion")
    implementation("org.bstats:bstats-bukkit:$bstatsVersion")

    // Optional dependencies
    implementation("com.github.MilkBowl:VaultAPI:$vaultVersion")

    implementation("us.dynmap:DynmapCoreAPI:$dynmapVersion:all")
    implementation("us.dynmap:dynmap-api:$dynmapVersion:unshaded")
    implementation("us.dynmap:spigot:$dynmapVersion:unshaded") {
        isTransitive = false
    }
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
    val jvmTarget: String by project

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