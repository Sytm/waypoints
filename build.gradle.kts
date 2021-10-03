plugins {
    kotlin("jvm") version "1.5.31" apply false
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://repo.codemc.io/repository/maven-public/")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://repo.minebench.de/")
        maven(url = "https://jitpack.io")
    }
}