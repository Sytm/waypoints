import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    val paperVersion: String by project

    val md5CommonsVersion: String by project
    val sqliteHelperVersion: String by project

    val junitVersion: String by project
    val mockBukkitVersion: String by project
    val sqliteDriverVersion: String by project

    api("io.papermc.paper:paper-api:$paperVersion")

    implementation("de.md5lukas:sqlite-kotlin-helper:$sqliteHelperVersion")

    api(kotlin("stdlib-jdk8"))
    api(project(":api-base"))
    implementation(project(":utils"))
    implementation("de.md5lukas:md5-commons:$md5CommonsVersion")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:$mockBukkitVersion")
    testRuntimeOnly("org.spigotmc:spigot-api:$paperVersion")
    testRuntimeOnly("org.xerial:sqlite-jdbc:$sqliteDriverVersion")
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    dependencies {
        include(dependency("de.md5lukas:sqlite-kotlin-helper"))
    }

    relocate("de.md5lukas.jdbc", "de.md5lukas.waypoints.api.sqlite.jdbc")
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}