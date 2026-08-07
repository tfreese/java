plugins {
    id("java-library")
    id("maven-publish") // Used in jripper-swing
}

description = "An alternative for Swing like the Binding in JavaFX."

dependencyManagement {
    generatedPomCustomization {
        // Disable Spring's "dependencyManagement" in POM.
        setEnabled(false)
        //enabled = false
    }
}

dependencies {
    api("org.slf4j:slf4j-api")
}

tasks.named("build").get().finalizedBy("publishToMavenLocal")

// Deaktiviere Configuration Cache für Maven Publish Tasks (nicht kompatibel).
tasks.withType<GenerateMavenPom>().configureEach {
    notCompatibleWithConfigurationCache("Maven POM generation with configuration cache")
}
tasks.withType<GenerateModuleMetadata>().configureEach {
    notCompatibleWithConfigurationCache("Maven metadata generation with configuration cache")
}

val publishGroup = project.group.toString()
val publishName = project.name
val publishVersion = project.version.toString()
val publishDescription = project.description

// components["java"]
//val componentContainer = components.findByName("java")

// https://docs.gradle.org/current/userguide/publishing_maven.html
publishing {
    publications {
        create<MavenPublication>("binding") {
            groupId = publishGroup
            artifactId = publishName
            version = publishVersion

            from(components["java"])

            versionMapping {
                // id("java-library")
                // - api = <scope>compile</scope>
                // - runtimeOnly = <scope>runtime</scope>
                //
                // id("java")
                // - implementation = <scope>runtime</scope>
                // - runtimeOnly = <scope>runtime</scope>

                usage(Usage.JAVA_API) {
                    // Nimm die Abhängigkeitsauflösung aus genau dieser Konfiguration.
                    fromResolutionOf(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME)
                }
                usage(Usage.JAVA_RUNTIME) {
                    // Nimm das bereits berechnete Resolution-Result der aktuellen Usage/Variante.
                    fromResolutionResult()
                }
            }

            pom {
                name = publishName
                description = publishDescription
            }

            // pom.withXml {
            //     def root = asNode()
            //     // root.dependencies.removeAll { dep ->
            //     //     dep.scope == "test"
            //     // }
            //
            //     if (!root.packaging || root.packaging.isEmpty()) {
            //         // Doesn't work.
            //         // def children = root.children()
            //         // def idx = children.findIndexOf { it instanceof Node && it.name() == "description" }
            //         // def packagingNode = new Node(null, "packaging", "jar")
            //         //
            //         // if (idx >= 0) {
            //         //     children.add(idx + 1, packagingNode) // After description-Node.
            //         // } else {
            //         //     children.add(packagingNode)
            //         // }
            //         def desc = root.description ? root.description[0] : null
            //
            //         if (desc != null) {
            //             desc + {
            //                 packaging("jar")
            //             }
            //         } else {
            //             root.appendNode("packaging", "jar")
            //         }
            //     }
            // }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = layout.buildDirectory.dir("repos/releases")
            val snapshotsRepoUrl = layout.buildDirectory.dir("repos/snapshots")

            val repoUrl = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            url = uri(repoUrl)
            name = "otherLocal"
            isAllowInsecureProtocol = true

            // credentials {
            //     username = deployRepoUsername
            //     password = deployRepoPassword
            // }
        }
    }
}
