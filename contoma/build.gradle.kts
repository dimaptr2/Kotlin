import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    application
    kotlin("jvm") version "1.3.31"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "ru.velkomfood.sap.conto.matrix"
version = "12.1.1"

// Application plugin's settings
application {
    mainClassName = "${group}.LauncherKt"
}

// Versions of dependencies
val slf4jVersion = "1.7.26"
val ktorVersion = "1.2.0"

repositories {
    mavenCentral()
    jcenter()
}

// Task for the processing of dependencies
dependencies {
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "12"
}

// Create an executable jar file with all dependencies
tasks.shadowJar {
    manifest {
        attributes["Implementation-Version"] = "${project.version}"
        attributes["Main-Class"] = application.mainClassName
    }
}