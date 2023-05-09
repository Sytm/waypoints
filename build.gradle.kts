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
    googleJavaFormat()
    formatAnnotations()
  }
  kotlin {
    ratchetFrom("51133f861b61cd5d7a263d7a0778042687c29c13")
    target(
        "*/src/*/kotlin/**/*.kt",
        "**/*.kts",
    )
    ktfmt()
  }
}
