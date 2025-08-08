import org.jetbrains.dokka.gradle.DokkaTask

plugins {
  with(libs.plugins) {
    alias(kotlin)
    alias(dokka)
    alias(shadow)
  }
  `maven-publish`
}

dependencies {
  api(libs.paper)
  api(libs.stdlib)

  api(libs.configurate.core)
  api(project(":configurate-helpers"))
  implementation(libs.schedulers)
  implementation(libs.pathfinder)
}

kotlin { jvmToolchain(21) }

version = project.property("pointersVersion") as String

val sourcesJar by
    tasks.registering(Jar::class) {
      archiveClassifier = "sources"
      from(sourceSets.main.get().allSource)
    }

val dokkaHtml by
    tasks.getting(DokkaTask::class) {
      dokkaSourceSets {
        configureEach {
          val majorVersion =
              libs.versions.paper.get().substringBefore('-').split('.').take(2).joinToString(".")
          externalDocumentationLink(
              "https://jd.papermc.io/paper/$majorVersion/",
              "https://jd.papermc.io/paper/$majorVersion/element-list")
        }
      }
    }

val dokkaHtmlJar by
    tasks.registering(Jar::class) {
      dependsOn(tasks.dokkaHtml)
      archiveClassifier = "javadoc"
      from(tasks.dokkaHtml)
    }

tasks {
  shadowJar {
    archiveClassifier = ""

    dependencies { include(dependency(libs.pathfinder.get())) }

    relocate("de.md5lukas.pathfinder", "de.md5lukas.waypoints.pointers.path")
  }
}

publishing {
  repositories {
    maven {
      name = "md5lukasReposilite"

      url =
          uri(
              "https://repo.md5lukas.de/${
                    if (version.toString().endsWith("-SNAPSHOT")) {
                        "snapshots"
                    } else {
                        "releases"
                    }
                }")

      credentials(PasswordCredentials::class)
      authentication { create<BasicAuthentication>("basic") }
    }
  }
  publications {
    create<MavenPublication>("maven") {
      from(components["shadow"])
      artifact(sourcesJar)
      artifact(dokkaHtmlJar)
    }
  }
}
