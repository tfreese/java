pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

    val versionMyJavaConventionPlugin = providers.gradleProperty("version_myJavaConventionPlugin")
    val versionProtobufGradlePlugin = providers.gradleProperty("version_protobufGradlePlugin")
    val versionErrorproneGradlePlugin = providers.gradleProperty("version_errorproneGradlePlugin")
    val versionJavafxPlugin = providers.gradleProperty("version_javafxPlugin")
    val versionSpringBoot = providers.gradleProperty("version_springBoot")

    plugins {
        id("de.freese.gradle.conventions").version(versionMyJavaConventionPlugin).apply(false)
        id("com.google.protobuf").version(versionProtobufGradlePlugin).apply(false)
        id("net.ltgt.errorprone").version(versionErrorproneGradlePlugin).apply(false)
        id("org.openjfx.javafxplugin").version(versionJavafxPlugin).apply(false)
        id("org.springframework.boot").version(versionSpringBoot).apply(false)
    }
}

// Without rootProject.name the Name of the Project-Directory is used.
rootProject.name = "java"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://repo.gradle.org/gradle/libs-releases")
            content {
                includeGroup("org.gradle")
            }
        }
    }
}

include("binding")
include("cellular-machines")
include("dependency-utils")
include("gradle-build-cache")
include("jconky")
include("jsensors")
include("logging")
include("meta-model")

// file("${rootDir}/misc").eachDirMatch(~/misc-.*/) {
//    include("misc:${it.name}")
//}

include("misc:misc-cache")
include("misc:misc-genetic-algorithm")

include("misc:misc-jspecify")
include("misc:misc-junit5")
include("misc:misc-led")
include("misc:misc-micrometer")
include("misc:misc-openstreetmap")
include("misc:misc-protobuf")
include("misc:misc-sonstiges")
include("misc:misc-sudoku")

println("")
println("Gradle version: ${GradleVersion.current().version}")
println("Java version: ${JavaVersion.current()}")
println("MaxWorkerCount: ${gradle.startParameter.maxWorkerCount}")
println("")
