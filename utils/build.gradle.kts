import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "de.md5lukas"

repositories {
    mavenLocal()
    mavenCentral()

    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}


dependencies {
    val spigotVersion: String by project
    val md5CommonsVersion: String by project
    val junitVersion: String by project

    api("org.spigotmc:spigot-api:$spigotVersion")
    api(kotlin("stdlib-jdk8"))
    implementation("de.md5lukas:md5-commons:$md5CommonsVersion")

    // Test dependencies
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

tasks.withType<KotlinCompile> {
    val jvmTarget: String by project

    kotlinOptions.jvmTarget = jvmTarget
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}
