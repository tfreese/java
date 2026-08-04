import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("application")
    id("net.ltgt.errorprone")
}

description = "Check for new Dependency Versions"

val mainClazz = "de.freese.dependency.update.VersionUpdatesLauncher"

application {
    mainClass.set(mainClazz)
}
// run {
//     jvmArgs = ["--enable-native-access=ALL-UNNAMED"]
// }

// Done by Convention-Plugin.
// jar {
//     manifest {
//         attributes(
//                 "Main-Class": mainClazz
//         )
//     }
// }

dependencies {
    implementation("org.jspecify:jspecify")
    errorprone("com.uber.nullaway:nullaway:" + property("version_nullaway"))
    errorprone("com.google.errorprone:error_prone_core:" + property("version_errorProneCore"))

    implementation("tools.jackson.core:jackson-databind")
    implementation("dev.failsafe:failsafe")
    implementation("io.projectreactor:reactor-core")
    implementation("org.apache.httpcomponents.client5:httpclient5")
//    implementation("org.apache.maven:maven-artifact")
    implementation("org.apache.maven:maven-model-builder")
    implementation("org.apache.maven:maven-settings-builder")
    implementation("org.glassfish.jersey.connectors:jersey-apache5-connector")
    // implementation("org.glassfish.jersey.connectors:jersey-jnh-connector")
    implementation("org.slf4j:jul-to-slf4j")

    runtimeOnly("jakarta.activation:jakarta.activation-api") // Required by jakarta.ws.rs.client.
    runtimeOnly("org.glassfish.jersey.inject:jersey-hk2") // Prevent message "Jersey-HK2 module is missing ..."
    runtimeOnly("org.slf4j:jcl-over-slf4j")
    runtimeOnly("org.slf4j:slf4j-simple")

//    testImplementation("org.apache.maven:maven-core")
    testImplementation("org.gradle:gradle-tooling-api")

    // testImplementation("org.apache.maven:maven-core")
    // testImplementation("org.apache.maven:maven-resolver-provider")
    // testImplementation("org.apache.maven.resolver:maven-resolver-connector-basic")
    // testImplementation("org.apache.maven.resolver:maven-resolver-transport-file")
    // testImplementation("org.apache.maven.resolver:maven-resolver-transport-http:")
    // testImplementation("org.apache.maven.resolver:maven-resolver-transport-wagon")
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        // Warnung: [MissingSummary] A summary line is required on public/protected Javadocs.
        // Or with SuppressWarnings.
        disable("MissingSummary")
        disable("UnusedMethod")

        // No byte[] allowed.
        // disable("ArrayRecordComponent")

        warn("NullAway")

        // Scan all Packages.
        option("NullAway:AnnotatedPackages", "de.freese.dependency")

        // Scan only Packages with package.info.java.
        // option("NullAway:OnlyNullMarked", "true")

        // Uncomment below if you are using Java 22+ compiled and you want to check generics nullness.
        option("NullAway:JSpecifyMode", "true")
    }
}

