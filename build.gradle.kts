// Execute Tasks in SubModule: gradle MODULE:clean build
plugins {
    id("de.freese.gradle.conventions").apply(false)
    id("com.google.protobuf").apply(false)
    id("net.ltgt.errorprone").apply(false)
    id("org.openjfx.javafxplugin").apply(false)
    id("org.springframework.boot").apply(false)
}

allprojects {
    plugins.apply("base")
}

subprojects {
    plugins.apply("de.freese.gradle.conventions")
    plugins.apply("io.spring.dependency-management")

    // val dependencyManagement = extensions.getByType<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>()

    extensions.configure(io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension::class.java) {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:" + property("version_springBoot"))
        }

        dependencies {
            dependency("ch.qos.logback.db:logback-classic-db:" + property("version_logbackClassicDb"))
            dependency("com.danielflower.apprunner:javasysmon:" + property("version_javasysmon"))
            dependency("com.formdev:flatlaf-intellij-themes:" + property("version_flatLaf"))
            dependency("com.github.spotbugs:spotbugs-annotations:" + property("version_spotbugs"))
            dependency("com.lmax:disruptor:" + property("version_disruptor"))
            dependency("dev.failsafe:failsafe:" + property("version_failsafe"))
            dependency("net.jthink:jaudiotagger:" + property("version_jaudiotagger"))

            dependency("org.apache.commons:commons-compress:" + property("version_commonsCompress"))
            dependency("org.apache.lucene:lucene-queryparser:" + property("version_lucene"))
            dependencySet("org.apache.lucene:" + property("version_lucene")) {
                entry("lucene-analysis-common")
                entry("lucene-queryparser")
            }
            dependencySet("org.apache.maven:" + property("version_maven")) {
                entry("maven-model-builder")
                entry("maven-settings-builder")
            }
            // dependencySet("org.apache.tomcat:" + dependencyManagement.importedProperties["tomcat.version"]) {
            //    entry("tomcat-catalina")
            //    entry("tomcat-juli")
            //}
            dependencySet("org.apache.xmlgraphics:" + property("version_batik")) {
                entry("batik-codec")
                entry("batik-swing")
            }
            dependency("org.gradle:gradle-tooling-api:" + property("version_gradleToolingApi"))
            dependency("org.apache.velocity:velocity-engine-core:" + property("version_velocity"))

            dependency("org.apiguardian:apiguardian-api:" + property("version_apiGuardian"))
            dependency("org.jfree:jfreechart:" + property("version_jfreechart")) {
                // exclude("com.lowagie:itext")
                // exclude("xml-apis:xml-apis")
            }
            dependency("org.jsoup:jsoup:" + property("version_jsoup"))
            // dependency("org.jspecify:jspecify:" + property("version_jspecify"))
        }
    }

    plugins.withType<JavaPlugin> {
        val mockitoAgent = configurations.create("mockitoAgent")

        dependencies {
            // add("implementation", platform("org.springframework.boot:spring-boot-dependencies:$version_springBoot"))

            add("testImplementation", "org.assertj:assertj-core")
            add("testImplementation", "org.awaitility:awaitility")
            add("testImplementation", "org.junit.jupiter:junit-jupiter")

            add("testImplementation", "org.mockito:mockito-junit-jupiter")
            mockitoAgent("org.mockito:mockito-core") {
                isTransitive = false
            }

            // To avoid compiler warnings about @API annotations in Log4j Code.
            add("compileOnly", "com.github.spotbugs:spotbugs-annotations")

            // To avoid compiler warnings about @API annotations in JUnit Code.
            // add("testCompileOnly", "org.apiguardian:apiguardian-api")

            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
        }

        tasks.withType<Test>().configureEach {
            doFirst {
                jvmArgs.add("-javaagent:${mockitoAgent.asPath}")
            }
        }
    }
}
