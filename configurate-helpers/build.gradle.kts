plugins {
  with(libs.plugins) { alias(kotlin) }
  `maven-publish`
}

kotlin { jvmToolchain(21) }

dependencies {
  api(libs.paper)
  api(libs.configurate.core)
  api(libs.configurate.kotlin)
}

group = "de.md5lukas.configurate"

version = project.property("configurateHelpersVersion") as String

val sourcesJar by
    tasks.registering(Jar::class) {
      archiveClassifier = "sources"
      from(sourceSets.main.get().allSource)
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
    }
  }
}
