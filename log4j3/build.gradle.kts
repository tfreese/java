plugins {
    id("java")
    // id("java-library")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(26))
    }
}

dependencies {
    // api(...)

    implementation(platform("org.apache.logging.log4j:log4j-bom:" + property("version_log4j")))
    implementation(platform("org.slf4j:slf4j-bom:" + property("version_slf4j")))

    implementation("org.slf4j:slf4j-api")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.hsqldb:hsqldb:" + property("version_hsqldb"))

    runtimeOnly("org.apache.logging.log4j:log4j-jdbc-jndi")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl")

    testImplementation(platform("org.junit:junit-bom:" + property("version_junit")))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    isEnabled = true

    useJUnitPlatform()
}
