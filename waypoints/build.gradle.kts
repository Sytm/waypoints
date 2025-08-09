import de.md5lukas.resourceindex.ResourceIndexTask

plugins {
  with(libs.plugins) {
    alias(kotlin)
    alias(shadow)
    alias(minotaur)
    alias(runPaper)
    alias(changelog)
  }
}

description = "Waypoints plugin"

dependencies {
  implementation(libs.paper)
  implementation(libs.stdlib)
  implementation(libs.coroutines)

  compileOnly(libs.annotations)

  implementation(project(":configurate-helpers"))
  implementation(project(":pointers"))
  implementation(project(":signgui"))

  // Dependencies on own projects
  implementation(libs.md5Commons)
  implementation(libs.kinvs)
  implementation(libs.paperBrigadier)
  implementation(libs.sqliteHelper)

  // Required dependencies
  implementation(libs.schedulers)
  implementation(libs.skedule)
  implementation(libs.anvilGui)
  implementation(libs.bStats)
  implementation(libs.configurate.core)
  implementation(libs.configurate.kotlin)

  // Optional dependencies
  implementation(libs.vaultApi)

  implementation(variantOf(libs.dynmap.coreApi) { classifier("all") })
  implementation(variantOf(libs.dynmap.api) { classifier("unshaded") }) { isTransitive = false }
  implementation(libs.squaremapApi)
  implementation(libs.bluemapApi)
  implementation(libs.pl3xmap)
  implementation(libs.geyser)

  // Testing
  testImplementation(kotlin("test-junit5"))
  testImplementation(libs.junitJupiter)
  testImplementation(libs.mockBukkit)
  testRuntimeOnly(libs.sqliteJdbc)
  testRuntimeOnly(libs.junitLauncher)
}

tasks {
  register<ResourceIndexTask>("createResourceIndex")

  processResources {
    dependsOn("createResourceIndex")

    val properties =
        mapOf(
            "version" to project.version,
            "apiVersion" to libs.versions.paper.get().substringBefore('-'),
            "kotlinVersion" to libs.versions.kotlin.get(),
            "coroutinesVersion" to libs.versions.coroutines.get(),
        )

    inputs.properties(properties)

    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") { expand(properties) }
  }

  shadowJar {
    archiveClassifier = ""

    minimize()

    exclude("META-INF/")

    dependencies {
      include(project(":pointers"))
      include(dependency(libs.pathfinder.get()))
      include(project(":signgui"))
      include(project(":configurate-helpers"))

      include(dependency(libs.md5Commons.get()))
      include(dependency(libs.kinvs.get()))
      include(dependency(libs.paperBrigadier.get()))
      include(dependency(libs.sqliteHelper.get()))

      include(dependency(libs.schedulers.get()))
      include(dependency(libs.skedule.get()))
      include(dependency(libs.anvilGui.get()))
      include(dependency("org.bstats::"))
    }

    arrayOf(
            "commons",
            "kinvs",
            "konfig",
            "schedulers",
            "signgui",
            "paper.brigadier",
            "pathfinder",
            "configurate",
            "jdbc")
        .forEach {
          relocate("de.md5lukas.$it", "de.md5lukas.waypoints.libs.${it.substringAfterLast('.')}")
        }
    arrayOf("com.okkero.skedule", "net.wesjd.anvilgui", "org.bstats").forEach {
      relocate(it, "de.md5lukas.waypoints.libs.${it.substringAfterLast('.')}")
    }

    manifest { attributes("paperweight-mappings-namespace" to "mojang+yarn") }
  }

  runServer {
    dependsOn("jar")
    minecraftVersion(libs.versions.paperTestServer.get().substringBefore('-'))
  }

  test { useJUnitPlatform() }
}

runPaper.folia.registerTask()

kotlin { jvmToolchain(libs.versions.jvmToolchain.get().toInt()) }

changelog { path = rootProject.relativePath("CHANGELOG.md") }

modrinth {
  val modrinthToken: String? by project

  token = modrinthToken

  projectId = "waypoints"
  versionType = "release"
  uploadFile.set(tasks.shadowJar)

  gameVersions.addAll(libs.versions.paper.get().substringBefore('-'))
  loaders.addAll("paper", "folia")

  syncBodyFrom = provider { rootProject.file("README.md").readText() }

  changelog = provider {
    with(project.changelog) { renderItem(getLatest().withEmptySections(false).withHeader(false)) }
  }

  dependencies {
    with(optional) {
      project("pl3xmap")
      project("bluemap")
      project("squaremap")
      project("dynmap")
    }
  }

  debugMode = false
}
