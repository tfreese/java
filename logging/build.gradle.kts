plugins {
    id("java")
}

description = "Basis-Projekt für Logging-APIs"

// configurations.configureEach {
//    exclude group = "ch.qos.logback", module = "logback-core"
//}

dependencies {
    testImplementation("ch.qos.logback:logback-core")
    testImplementation("com.lmax:disruptor") // For Async-Logging

    testImplementation("ch.qos.logback.db:logback-classic-db") {
        exclude(group = "ch.qos.logback", module = "logback-core")
    }
    testImplementation("org.apache.logging.log4j:log4j-core")
    testImplementation("org.hsqldb:hsqldb")
}
