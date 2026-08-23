import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java")
    // id("application")
    id("net.ltgt.errorprone")
    id("org.springframework.boot")
}

description = "Gradle Build Cache"

dependencies {
    errorprone("com.uber.nullaway:nullaway:" + property("version_nullaway"))
    errorprone("com.google.errorprone:error_prone_core:" + property("version_errorProneCore"))

    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

val mainClazz = "de.freese.gradle.cache.GradleBuildCache"

// gradle run --args="-Dspring.profiles.active=file"
// application {
//     mainClass = project.main
//     // applicationDefaultJvmArgs = ["-Dspring.profiles.active=file"]
// }
// run {
//     jvmArgs = ["-Dspring.profiles.active=file"]
// }

// gradle exec --args=["-Dspring.profiles.active=file"]
// gradle exec -Dspring.profiles.active=file
tasks.register<JavaExec>("exec") {
    group = "MyTasks"
    description = "Start the GradleBuildCache"

    // classpath = files(...)
    classpath = sourceSets["main"].runtimeClasspath

    mainClass.set(mainClazz)

    // args("-Dspring.profiles.active=file")
    environment("spring.profiles.active", "file")

    // executable = ".../java.exe"
    // workingDir = workDir
    // args = ["...","..."]
    // jvmArgs("--enable-native-access=ALL-UNNAMED")
    // debugOptions {
    //     enabled = true
    //     port = 5566
    //     server = true
    //     suspend = false
    // }
}

// Start: gradle bootRun --args="--spring.profiles.active=file"
// The archive name. If the name has not been explicitly set, the pattern for the name is:
// [archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]
// archiveFileName = "my-boot.jar"
springBoot {
    mainClass.set(mainClazz)
}

// gradle bootRun --args="--spring.profiles.active=file --server.port=65111"
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    // systemProperty("com.example.property", findProperty("example") ?: "default")

    args = listOf("--spring.profiles.active=file")
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        // No byte[] allowed.
        // Or with SuppressWarnings.
        disable("ArrayRecordComponent")

        warn("NullAway")

        // Scan all Packages.
        option("NullAway:AnnotatedPackages", "de.freese.gradle.cache")

        // Scan only Packages with package.info.java.
        // option("NullAway:OnlyNullMarked", "true")

        // Uncomment below if you are using Java 22+ compiled, and you want to check generics nullness.
        option("NullAway:JSpecifyMode", "true")
    }
}
