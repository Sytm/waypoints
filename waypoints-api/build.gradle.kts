import org.jetbrains.dokka.gradle.DokkaTask

plugins {
  with(libs.plugins) {
    alias(kotlin)
    alias(dokka)
  }
  `maven-publish`
}

description = "Waypoints api"

dependencies {
  api(libs.paper)
  api(libs.stdlib)
  implementation(libs.coroutines)
}

kotlin { jvmToolchain(libs.versions.jvmToolchain.get().toInt()) }

val sourcesJar by
    tasks.creating(Jar::class) {
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
    tasks.creating(Jar::class) {
      dependsOn(tasks.dokkaHtml)
      archiveClassifier = "javadoc"
      from(tasks.dokkaHtml)
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
      from(components["kotlin"])
      artifact(sourcesJar)
      artifact(dokkaHtmlJar)
    }
  }
}
