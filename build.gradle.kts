import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlin) apply false
  alias(libs.plugins.spotless)
}

repositories { mavenCentral() }

subprojects {
  repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.md5lukas.de/public/")
    exclusiveContent { // Vault
      forRepository { maven("https://jitpack.io") }
      filter { includeGroup("com.github.MilkBowl") }
    }
    exclusiveContent { // BlueMap
      forRepository { maven("https://repo.bluecolored.de/releases") }
      filter { includeGroup("de.bluecolored") }
    }
    exclusiveContent { // DynMap
      forRepository { maven("https://repo.mikeprimm.com/") }
      filter { includeGroup("us.dynmap") }
    }
    exclusiveContent { // Pl3xMap
      forRepository { maven("https://api.modrinth.com/maven") }
      filter { includeGroup("maven.modrinth") }
    }
    exclusiveContent { // Geyser
      forRepository { maven("https://repo.opencollab.dev/main/") }
      filter {
        includeGroupByRegex("org\\.geysermc\\..+")
        includeGroup("org.cloudburstmc.math")
        includeGroup("org.spongepowered")
      }
    }
  }

  tasks.withType<KotlinCompile> {
    compilerOptions.freeCompilerArgs.addAll(
        "-Xjvm-default=all",
        "-Xlambdas=indy",
    )
  }
}

spotless {
  java {
    target(
        "**/src/*/java/**/*.java",
    )
    palantirJavaFormat().style("GOOGLE")
    formatAnnotations()
  }
  kotlin {
    toggleOffOn()
    target(
        "*/src/*/kotlin/**/*.kt",
        "**/*.kts",
    )
    ktfmt()
  }
}
