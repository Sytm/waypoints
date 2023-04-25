import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") apply false
    id("com.github.johnrengelman.shadow") apply false
    id("org.jetbrains.dokka") apply false
}

subprojects {
    tasks.withType<KotlinCompile> {
        compilerOptions.freeCompilerArgs.addAll(
            "-Xjvm-default=all",
            "-Xlambdas=indy",
        )
    }
}