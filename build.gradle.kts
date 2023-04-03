plugins {
    kotlin("jvm") apply false
    id("com.github.johnrengelman.shadow") apply false
    id("org.jetbrains.dokka") apply false
}

subprojects {
    repositories {
        mavenCentral()

        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.md5lukas.de/public/")
    }
}