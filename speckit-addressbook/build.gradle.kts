plugins {
    id("java")
    id("jacoco")
    id("org.springframework.boot")
    // id("io.spring.dependency-management")
}

sourceSets {
    main {
        resources {
            srcDir(layout.projectDirectory.dir("src").dir("main").dir("webapp"))
        }
    }
}

// java {
//     toolchain {
//         languageVersion = JavaLanguageVersion.of(25)
//     }
// }
//
// val mockitoAgent = configurations.create("mockitoAgent")

// configurations.configureEach {
//     resolutionStrategy {
//         // tech-stack.md: keine SNAPSHOTs, keine dynamischen Versionen
//         failOnDynamicVersions()
//         failOnChangingVersions()
//     }
// }

dependencies {
    implementation("org.joinfaces:primefaces-spring-boot-starter:${providers.gradleProperty("version_joinfaces").get()}") {
        exclude(group = "ch.qos.logback")
    }

    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.springframework.boot:spring-boot-h2console")
    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

    // Nur fuer lokale Entwicklung (nicht im produktiven Fat-Jar enthalten):
    // automatischer Neustart bei Java-Aenderungen + LiveReload-Browser-Refresh.
    // XHTML-Aenderungen loesen dank joinfaces.faces.facelets-refresh-period=0
    // (application.properties) ohnehin KEINEN Neustart aus - nur den LiveReload.
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    // testImplementation("org.mockito:mockito-junit-jupiter")
    // mockitoAgent("org.mockito:mockito-core") {
    //     isTransitive = false
    // }
}

springBoot {
    mainClass.set("de.addressbook.AddressbookApplication")
}

// tasks.withType<JavaCompile>().configureEach {
//     options.encoding = "UTF-8"
// }
//
// tasks.withType<Test>().configureEach {
//     useJUnitPlatform()
//
//     jvmArgs.add("-javaagent:${mockitoAgent.asPath}")
//
//     finalizedBy(tasks.named("jacocoTestReport"))
// }

// tasks.named<JacocoReport>("jacocoTestReport") {
//     dependsOn(tasks.named("test"))
//
//     reports {
//         xml.required.set(false)
//         html.required.set(true)
//     }
//
//     // T054: Fokus der Coverage-Betrachtung (Constitution Principle VII) auf Persistence-
//     // (repository) und Business-Layer (service); Presentation/Web-Layer bleibt ausgeklammert.
//     classDirectories.setFrom(
//         files(classDirectories.files.map {
//             fileTree(it) {
//                 include("de/addressbook/repository/**", "de/addressbook/service/**")
//             }
//         })
//     )
// }
