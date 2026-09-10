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
    implementation(platform("org.springframework.boot:spring-boot-dependencies:" + property("version_springBoot")))

    implementation(platform("org.apache.logging.log4j:log4j-bom:" + property("version_log4j")))

    implementation("org.slf4j:slf4j-api")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.hsqldb:hsqldb")

    runtimeOnly("org.apache.logging.log4j:log4j-jdbc-jndi")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl")

    testImplementation("org.junit.jupiter:junit-jupiter")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    isEnabled = true

    useJUnitPlatform()
}
