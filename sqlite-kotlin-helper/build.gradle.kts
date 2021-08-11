import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "de.md5lukas"

dependencies {
    api(kotlin("stdlib-jdk8"))
    compileOnly("org.jetbrains:annotations:20.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}