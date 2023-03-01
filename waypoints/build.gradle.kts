import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import de.md5lukas.resourceindex.ResourceIndexTask
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("com.modrinth.minotaur") version "2.+"
}

description = "Waypoints plugin"

repositories {
    mavenCentral()

    maven(url = "https://repo.codemc.io/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://repo.minebench.de/")
    maven(url = "https://jitpack.io")

    maven(url = "https://repo.mikeprimm.com/")

    maven(url = "https://repo.dmulloy2.net/repository/public/")

    maven(url = "https://repo.md5lukas.de/releases/")
}

val spigotVersion: String by project

dependencies {
    val md5CommonsVersion: String by project
    val kinvsVersion: String by project
    val anvilGUIVersion: String by project
    val bstatsVersion: String by project
    val vaultVersion: String by project
    val dynmapVersion: String by project
    val squareMapVersion: String by project
    val blueMapVersion: String by project
    val protocolLibVersion: String by project

    implementation("org.spigotmc:spigot-api:$spigotVersion")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":utils"))
    implementation(project(":waypoints-api"))
    implementation(project(":api-base"))
    implementation(project(":api-sqlite", "shadow"))

    // Dependencies on own projects
    implementation("de.md5lukas:md5-commons:$md5CommonsVersion")
    implementation("de.md5lukas:kinvs:$kinvsVersion")

    // Required dependencies
    implementation("net.wesjd:anvilgui:$anvilGUIVersion")
    implementation("org.bstats:bstats-bukkit:$bstatsVersion")

    // Optional dependencies
    implementation("com.github.MilkBowl:VaultAPI:$vaultVersion")

    implementation("us.dynmap:DynmapCoreAPI:$dynmapVersion:all")
    implementation("us.dynmap:dynmap-api:$dynmapVersion:unshaded") {
        isTransitive = false
    }

    implementation("xyz.jpenilla:squaremap-api:$squareMapVersion")

    implementation("com.github.BlueMap-Minecraft:BlueMapAPI:$blueMapVersion")

    implementation("com.comphenix.protocol:ProtocolLib:$protocolLibVersion")
}

tasks.register("createResourceIndex", ResourceIndexTask::class.java) {
}

tasks.withType<ProcessResources> {
    dependsOn("createResourceIndex")

    inputs.property("version", project.version)
    inputs.property("spigotVersion", spigotVersion)
    inputs.property("kotlinVersion", getKotlinPluginVersion())

    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {
        var apiVersion = spigotVersion.substringBefore('-')
        if (apiVersion.count { it == '.' } > 1) {
            apiVersion = apiVersion.substringBeforeLast('.')
        }

        expand(
            "version" to project.version,
            "kotlinVersion" to getKotlinPluginVersion(),
            "apiVersion" to apiVersion,
        )
    }
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}

tasks.withType<KotlinCompile> {
    // To make sure we have an explicit dependency on the project itself because otherwise we will get a warning that we only depend on an output file and not the project itself
    dependsOn(project(":api-sqlite").tasks.jar)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    minimize {
        // Exclude AnvilGUI because the version specific NMS adapters are loaded via reflection and are not directly referenced
        exclude(dependency("net.wesjd:anvilgui"))

        exclude(project(":waypoints-api"))
    }

    exclude("META-INF/")

    dependencies {
        include(project(":utils"))
        include(project(":waypoints-api"))
        include(project(":api-base"))
        include(project(":api-sqlite"))

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

modrinth {
    val modrinthToken: String? by project

    token.set(modrinthToken)

    projectId.set("waypoints")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar as Any)

    gameVersions.addAll("1.19.3", "1.18.2", "1.17.1")
    loaders.addAll("spigot", "paper")

    syncBodyFrom.set(rootProject.file("README.md").readText())

    debugMode.set(false)
}
