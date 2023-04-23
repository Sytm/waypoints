import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import de.md5lukas.resourceindex.ResourceIndexTask
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("com.modrinth.minotaur") version "2.+"
    id("xyz.jpenilla.run-paper") version "2.+"
}

description = "Waypoints plugin"

repositories {
    maven("https://repo.codemc.io/repository/maven-public/") // AnvilGUI
    maven("https://jitpack.io") // BlueMap
    maven("https://libraries.minecraft.net") // Brigadier

    maven("https://repo.mikeprimm.com/") // DynMap
}

val paperVersion: String by project
val commandApiVersion: String by project

dependencies {
    val md5CommonsVersion: String by project
    val kinvsVersion: String by project
    val anvilGUIVersion: String by project
    val bstatsVersion: String by project
    val vaultVersion: String by project
    val dynmapVersion: String by project
    val squareMapVersion: String by project
    val blueMapVersion: String by project

    implementation("io.papermc.paper:paper-api:$paperVersion")
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":utils"))
    implementation(project(":pointers"))
    implementation(project(":waypoints-api"))
    implementation(project(":api-base"))
    implementation(project(":api-sqlite", "shadow"))

    // Dependencies on own projects
    implementation("de.md5lukas:md5-commons:$md5CommonsVersion")
    implementation("de.md5lukas:kinvs:$kinvsVersion")

    // Required dependencies
    implementation("net.wesjd:anvilgui:$anvilGUIVersion")
    implementation("org.bstats:bstats-bukkit:$bstatsVersion")
    implementation("dev.jorel:commandapi-bukkit-shade:$commandApiVersion")
    implementation("dev.jorel:commandapi-bukkit-kotlin:$commandApiVersion")
    implementation("com.mojang:brigadier:1.0.18")

    // Optional dependencies
    implementation("com.github.MilkBowl:VaultAPI:$vaultVersion")

    implementation("us.dynmap:DynmapCoreAPI:$dynmapVersion:all")
    implementation("us.dynmap:dynmap-api:$dynmapVersion:unshaded") {
        isTransitive = false
    }

    implementation("xyz.jpenilla:squaremap-api:$squareMapVersion")

    implementation("com.github.BlueMap-Minecraft:BlueMapAPI:$blueMapVersion")
}

tasks.register<ResourceIndexTask>("createResourceIndex")

tasks.withType<ProcessResources> {
    dependsOn("createResourceIndex")

    val apiVersion = paperVersion.split('.').let { "${it[0]}.${it[1]}" }

    inputs.property("version", project.version)
    inputs.property("apiVersion", apiVersion)
    inputs.property("kotlinVersion", getKotlinPluginVersion())
    inputs.property("commandApiVersion", commandApiVersion)

    filteringCharset = "UTF-8"

    filesMatching("paper-plugin.yml") {

        expand(
            "version" to project.version,
            "apiVersion" to apiVersion,
        )
    }
    filesMatching("dependencies.yml") {
        expand(
            "kotlinVersion" to getKotlinPluginVersion(),
            "commandApiVersion" to commandApiVersion,
        )
    }
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}

tasks.withType<KotlinCompile> {
    // To make sure we have an explicit dependency on the project itself because otherwise we will get a warning that we only depend on an output file and not the project itself
    dependsOn(project(":api-sqlite").tasks.shadowJar)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    minimize {
        // Exclude AnvilGUI because the version specific NMS adapters are loaded via reflection and are not directly referenced
        exclude(dependency("net.wesjd:anvilgui"))

        exclude(project(":waypoints-api"))
        exclude(project(":utils"))
    }

    exclude("META-INF/")

    dependencies {
        include(project(":utils"))
        include(project(":pointers"))
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

tasks.withType<RunServer> {
    dependsOn("jar")
    minecraftVersion(paperVersion.substringBefore('-'))
}

modrinth {
    val modrinthToken: String? by project

    token.set(modrinthToken)

    projectId.set("waypoints")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar as Any)

    gameVersions.addAll(paperVersion.substringBefore('-'))
    loaders.addAll("paper")

    syncBodyFrom.set(rootProject.file("README.md").readText())

    debugMode.set(false)
}
