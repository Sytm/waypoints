plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()

    maven("https://repo.md5lukas.de/releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
}


dependencies {
    val spigotVersion: String by project
    val md5CommonsVersion: String by project
    val junitVersion: String by project

    api("io.papermc.paper:paper-api:$spigotVersion")
    api(kotlin("stdlib-jdk8"))
    implementation("de.md5lukas:md5-commons:$md5CommonsVersion")

    // Test dependencies
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}
