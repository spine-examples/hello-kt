import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("io.spine.tools.gradle.bootstrap") version "1.7.0"
}

val javaVersion = "1.8"

spine.enableJava().server()

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

repositories {
    jcenter()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = javaVersion
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = javaVersion
}
