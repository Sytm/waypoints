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
    maven("https://repo.dmulloy2.net/repository/public/")
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
        "buildSrc/src/*/java/**/*.java",
        "waypoints/src/*/java/**/*.java",
    )
    palantirJavaFormat()
    formatAnnotations()
  }
  kotlin {
    target(
        "*/src/*/kotlin/**/*.kt",
        "**/*.kts",
    )
    ktfmt()
  }
}
