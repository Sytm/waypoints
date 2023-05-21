import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import de.md5lukas.resourceindex.ResourceIndexTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.jpenilla.runpaper.task.RunServer

plugins {
  with(libs.plugins) {
    alias(kotlin)
    alias(shadow)
    alias(minotaur)
    alias(runPaper)
  }
}

description = "Waypoints plugin"

repositories {
  maven("https://repo.codemc.io/repository/maven-public/") // AnvilGUI
  maven("https://libraries.minecraft.net") // Brigadier

  maven("https://jitpack.io") // Vault and BlueMap
  maven("https://repo.mikeprimm.com/") // DynMap
  exclusiveContent { // Pl3xMap
    forRepository { maven("https://api.modrinth.com/maven") }
    filter { includeGroup("maven.modrinth") }
  }
}

dependencies {
  implementation(libs.paper)
  implementation(libs.stdlib)
  implementation(libs.coroutines)

  implementation(project(":utils"))
  implementation(project(":pointers"))
  implementation(project(":waypoints-api"))
  implementation(project(":api-base"))
  implementation(project(":api-sqlite", "shadow"))
  implementation(project(":signgui"))

  // Dependencies on own projects
  implementation(libs.md5Commons)
  implementation(libs.kinvs)
  implementation(libs.konfig)

  // Required dependencies
  implementation(libs.schedulers)
  implementation(libs.skedule)
  implementation(variantOf(libs.anvilGui) { classifier("latest") })
  implementation(libs.bStats)
  implementation(libs.bundles.commandApi)

  // Optional dependencies
  implementation(libs.vaultApi)

  implementation(variantOf(libs.dynmap.coreApi) { classifier("all") })
  implementation(variantOf(libs.dynmap.api) { classifier("unshaded") }) { isTransitive = false }
  implementation(libs.squaremapApi)
  implementation(libs.bluemapApi)
  implementation(libs.pl3xmap)
}

tasks.register<ResourceIndexTask>("createResourceIndex")

tasks.withType<ProcessResources> {
  dependsOn("createResourceIndex")

  val apiVersion = libs.versions.paper.get().split('.').let { "${it[0]}.${it[1]}" }
  val kotlinVersion = libs.versions.kotlin.get()
  val commandApiVersion = libs.versions.commandApi.get()
  val coroutinesVersion = libs.versions.coroutines.get()

  inputs.property("version", project.version)
  inputs.property("apiVersion", apiVersion)
  inputs.property("kotlinVersion", kotlinVersion)
  inputs.property("commandApiVersion", commandApiVersion)
  inputs.property("coroutinesVersion", coroutinesVersion)

  filteringCharset = "UTF-8"

  filesMatching("paper-plugin.yml") {
    expand(
        "version" to project.version,
        "apiVersion" to apiVersion,
    )
  }
  filesMatching("dependencies.yml") {
    expand(
        "kotlinVersion" to kotlinVersion,
        "commandApiVersion" to commandApiVersion,
        "coroutinesVersion" to coroutinesVersion)
  }
}

kotlin { jvmToolchain(libs.versions.jvmToolchain.get().toInt()) }

tasks.withType<KotlinCompile> {
  // To make sure we have an explicit dependency on the project itself because otherwise we will get
  // a warning that we only depend on an output file and not the project itself
  dependsOn(project(":api-sqlite").tasks["shadowJar"])
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("")

  minimize {
    // Exclude AnvilGUI because the version specific NMS adapters are loaded via reflection and are
    // not directly referenced
    exclude(dependency(libs.anvilGui.get()))

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
    include(project(":signgui"))

    include(dependency(libs.md5Commons.get()))
    include(dependency(libs.kinvs.get()))
    include(dependency(libs.konfig.get()))

    include(dependency(libs.schedulers.get()))
    include(dependency(libs.skedule.get()))
    include(dependency(libs.anvilGui.get()))
    include(dependency("org.bstats::"))
  }

  arrayOf("commons", "kinvs", "konfig", "schedulers", "signgui").forEach {
    relocate("de.md5lukas.$it", "de.md5lukas.waypoints.$it")
  }

  relocate("com.okkero.skedule", "de.md5lukas.waypoints.skedule")
  relocate("net.wesjd.anvilgui", "de.md5lukas.waypoints.anvilgui")
  relocate("org.bstats", "de.md5lukas.waypoints.bstats")
}

runPaper.folia.registerTask()

tasks.withType<RunServer> {
  dependsOn("jar")
  minecraftVersion(libs.versions.paper.get().substringBefore('-'))
}

modrinth {
  val modrinthToken: String? by project

  token.set(modrinthToken)

  projectId.set("waypoints")
  versionType.set("release")
  uploadFile.set(tasks.shadowJar as Any)

  gameVersions.addAll(libs.versions.paper.get().substringBefore('-'))
  loaders.addAll("paper", "folia")

  syncBodyFrom.set(rootProject.file("README.md").readText())

  dependencies {
    with(optional) {
      project("pl3xmap")
      project("bluemap")
      project("squaremap")
      project("dynmap")
    }
    with(embedded) { project("commandapi") }
  }

  debugMode.set(false)
}
