plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val spigotVersion: String by project
    api("io.papermc.paper:paper-api:$spigotVersion")

    implementation(kotlin("stdlib-jdk8"))
    api(project(":waypoints-api"))
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}