plugins {
    java
    application
    kotlin("jvm") version "1.2.41"
}

group = "ru.velkomfood.mysap.pp.gui"
version = "1.0.2"

application {
    mainClassName = "$group.PusherKt"
}

repositories {
    mavenCentral()
    jcenter()
}

// Where is the SAP Java Connector?
val sapIdoc = "/usr/sap/JCo/sapidoc3.jar"
val sapJco = "/usr/sap/JCo/sapjco3.jar"

dependencies {
    compileOnly(fileTree(sapIdoc))
    compileOnly(fileTree(sapJco))
    compile(kotlin("stdlib"))
    compile("io.vertx", "vertx-web", "3.5.1")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

// Define the task function for the Jar building.
val fatJar = task("fatJar", type = Jar::class) {

    baseName = "${project.name}"
    version = "${project.version}"

    manifest {
        attributes["Implementation-Version"] = "${version}"
        attributes["Main-Class"] = "${application.mainClassName}"
        attributes["Class-Path"] = "${sapIdoc} ${sapJco}"
    }

    from(configurations.compile.map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)

}

// Run the standard task build with the task called "fatJar".
tasks {
    "assemble" {
        dependsOn(fatJar)
    }
    "build" {
        dependsOn(fatJar)
    }
}