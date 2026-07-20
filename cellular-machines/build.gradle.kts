plugins {
    id("java")
    id("org.springframework.boot")
}

description = "Beispiele für zellulare Automaten (cellular machines)."

dependencies {
    implementation("com.formdev:flatlaf-intellij-themes")
    implementation("org.jfree:jfreechart")
    implementation("org.slf4j:slf4j-api")

    runtimeOnly("org.slf4j:slf4j-simple")
}

// Start: gradle bootRun --args="--spring.profiles.active=dev"
// The archive name. If the name has not been explicitly set, the pattern for the name is:
// [archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]
// archiveFileName = "my-boot.jar"
springBoot {
    mainClass = "de.freese.simulationen.SimulationLauncher"
}

// gradle bootRun --args="-console -type wator -cycles 1500 -size 3840 2160 -dir /tmp/simulationen"
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    args = listOf(
        "-console", "-type", "wator", "-cycles", "1500", "-size", "3840", "2160", "-dir", "/tmp/simulationen"
    )
}

