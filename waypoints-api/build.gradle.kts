import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    `maven-publish`
}

description = "Waypoints api"

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    val spigotVersion: String by project
    api("org.spigotmc:spigot-api:$spigotVersion")

    api(kotlin("stdlib-jdk8"))
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaHtml by tasks.getting(DokkaTask::class) {
    dokkaSourceSets {
        configureEach {
            externalDocumentationLink("https://hub.spigotmc.org/javadocs/spigot/", "https://hub.spigotmc.org/javadocs/spigot/element-list")
        }
    }
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

val dokkaHtmlJar by tasks.creating(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("dokka")
    from(tasks.dokkaHtml)
}

publishing {
    repositories {
        maven {
            name = "md5lukasReposilite"

            url = uri(
                "https://repo.md5lukas.de/${
                    if (version.toString().endsWith("-SNAPSHOT")) {
                        "snapshots"
                    } else {
                        "releases"
                    }
                }"
            )

            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(sourcesJar)
            artifact(javadocJar)
            artifact(dokkaHtmlJar)
        }
    }
}