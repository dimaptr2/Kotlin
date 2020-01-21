plugins {
    kotlin("jvm") version "1.3.61"
    idea
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "ru.velkomfood.sap.services"
version = "11.2.1"

// The external dependencies and the additional parameters
val subGroup = "sd.load"
val sapJavaConnector = "/usr/sap/JCo"
val sapConTree = fileTree("${sapJavaConnector}/sapjco3.jar")
val slf4jVersion = "1.7.30"
val coroutinesVersion = "1.3.3"
val jasyncMysqlDriverVersion = "1.0.14"
val kotlinTestVersion = "1.3.61"
val testEngineVersion = "5.5.2"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {

    // SAP Java Connector
    compileOnly(sapConTree)
    testCompileOnly(sapConTree)
    testRuntimeOnly(sapConTree)
    // The main dependencies
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    // Logger
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    // Jasync MySQL driver
    implementation("com.github.jasync-sql:jasync-mysql:$jasyncMysqlDriverVersion")
    // Testing for Kotlin and with the JUnit5 Jupiter
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinTestVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$testEngineVersion")

}

// The start point of the application
application {
    mainClassName = "${project.group}.${subGroup}.LauncherKt"
}

// Here are the basic tasks
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    // Start the testing tasks
    test {
        useJUnitPlatform()
    }
    // create a fat, jar file
    shadowJar {
        manifest {
            attributes["Implementation-Version"] = project.version
            attributes["Class-Path"] = "${sapJavaConnector}/sapjco3.jar"
        }
    }
    // Run the application by Gradle
    create<JavaExec>("runJarFile") {
        main = application.mainClassName
        classpath = sourceSets["main"].runtimeClasspath
    }
}