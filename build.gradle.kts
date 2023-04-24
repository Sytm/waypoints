import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version libs.versions.kotlin apply false
}

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