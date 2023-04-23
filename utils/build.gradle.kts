plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.paper)
    api(kotlin("stdlib-jdk8"))
    implementation(libs.md5Commons)

    // Test dependencies
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.junitJupiter)
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}
