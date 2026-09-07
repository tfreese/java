plugins {
    id("java")
    id("maven-publish")
}

description = "Alles zum ausprobieren"

configurations.create("jaxb") {
    //extendsFrom(configurations.implementation.get())

    isCanBeResolved = true
    isCanBeConsumed = false
}

val destDirXjc = layout.buildDirectory.get().dir("generated").dir("xjc")

sourceSets {
    main {
        java {
            srcDir(destDirXjc)
        }
    }
}

dependencies {
    // implementation("com.sun.xml.bind:jaxb-xjc")
    add("jaxb", "com.sun.xml.bind:jaxb-xjc")
    add("jaxb", "com.sun.xml.bind:jaxb-impl")

    implementation("tools.jackson.dataformat:jackson-dataformat-xml")
    implementation("tools.jackson.module:jackson-module-jaxb-annotations")

    implementation("com.lmax:disruptor")
    implementation("com.zaxxer:HikariCP")
    implementation("dev.failsafe:failsafe")
    implementation("io.projectreactor:reactor-test")
    implementation("jakarta.json.bind:jakarta.json.bind-api")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api")
    implementation("net.jthink:jaudiotagger")
    implementation("org.apache.commons:commons-compress")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.logging.log4j:log4j-to-slf4j")
    implementation("org.apache.lucene:lucene-analysis-common")
    implementation("org.apache.lucene:lucene-queryparser")
    implementation("org.apache.xmlgraphics:batik-codec")
    implementation("org.apache.xmlgraphics:batik-swing")
    implementation("org.apache.velocity:velocity-engine-core")
    implementation("org.eclipse.angus:angus-mail")
    implementation("org.freemarker:freemarker")
    implementation("org.jsoup:jsoup")
    implementation("org.mariadb.jdbc:mariadb-java-client")
    implementation("org.springframework.boot:spring-boot-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-mail") {
        exclude(group = "ch.qos.logback", module = "logback-classic")
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }

    runtimeOnly("com.oracle.database.jdbc:ojdbc11")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime") // Implementation of jakarta.json.bind:jakarta.json.bind-api
    runtimeOnly("org.hsqldb:hsqldb")
    runtimeOnly("org.slf4j:slf4j-simple")

    testImplementation("com.h2database:h2")
    //testImplementation("org.apache.tomcat:tomcat-catalina") {
    //    // tomcat-juli ist in tomcat-catalina enthalten, aber auch viele andere Jars die nicht benötigt werden.
    //    exclude(group = "org.apache.tomcat")
    //}
    //testImplementation("org.apache.tomcat:tomcat-juli")
}

tasks.register("xslt") {
    group = "MyTasks"
    description = "Generates HTML from XSLT and XML files"

//    dependsOn("compileJava")

    val srcFolder = layout.projectDirectory.dir("src").dir("xslt")
    val destFolder = layout.buildDirectory.get().dir("classes").dir("java").dir("main").dir("xslt")

    //mkdir(destFolder)

    val xslFile = srcFolder.file("article.xsl")
    val xmlFile = srcFolder.file("article.xml")
    val outputFile = destFolder.file("article.html")

    inputs.files(xslFile, xmlFile)
    outputs.file(outputFile)

    //doLast {
    ant.withGroovyBuilder {
        "xslt"(
            "style" to xslFile, "in" to xmlFile, "out" to outputFile
        )
    }
    //}
}

// class MyTask extends DefaultTask {
//     @TaskAction
//     void runTask() {
//         project.ant.taskdef(..., classpath: projects.configurations.myConfig.asPath)
//     }
// }
//tasks.register<de.freese.gradle.xjc.XjcTask>("testXjcTask") {
//    // group = "MyTasks"
//
//    destDir.set(layout.buildDirectory.get().dir("generated").dir("xjcTask"))
//    schema.set(layout.projectDirectory.dir("schemas").dir("GolfCountryClub").file("GolfCountryClub.xsd"))
//    binding.set(layout.projectDirectory.file("schema.xjb"))
//    packageName.set("de.freese.xjc.golfcountryclub")
//    encoding.set(compileJava.options.encoding)
//    extension.set(true)
//    npa.set(true)
//    removeOldOutput.set(true)
//    verbose.set(true)
//}

tasks.register("genJaxb") {
    group = "MyTasks"
    description = "Generates Java classes from XSD files"

    val schemaDir = layout.projectDirectory.dir("schemas")
    inputs.dir(schemaDir)

    val binding = layout.projectDirectory.file("schema.xjb")
    inputs.file(binding)

    outputs.dir(destDirXjc)

    mkdir(destDirXjc)

    // doLast {
    val compileJavaTask = tasks.named<JavaCompile>("compileJava").get()
    val currentEncoding = compileJavaTask.options.encoding

    ant.withGroovyBuilder {
        "taskdef"(
            "name" to "xjc", "classname" to "com.sun.tools.xjc.XJCTask", "classpath" to configurations["jaxb"].asPath
        )

        "xjc"(
            "destdir" to destDirXjc,
            "schema" to schemaDir.dir("GolfCountryClub").file("GolfCountryClub.xsd"),
            "binding" to binding,
            "package" to "de.freese.xjc.golfcountryclub",
            "encoding" to currentEncoding,
            "extension" to true,
            "removeOldOutput" to true
        ) {
            "arg"("value" to "-npa")

            // XJC has embedded Build-Cache.
            // Files specified as the schema files and binding files are automatically added to the "depends" set as well,
            // but if those schemas are including/importing other schemas, you have to use a nested <depends> elements.

            // Avoid Message: Consider using <depends>/<produces> so that XJC won't do unnecessary compilation.

            // depends(file: binding)
            // depends(dir: schemaDir.dir("GolfCountryClub"), includes: "**/*.xsd")
            // produces(dir: destDirXjc, includes: "**/*.java")
            "produces"("dir" to destDirXjc, "includes" to "de/freese/xjc/golfcountryclub/**/*.java")
        }

        "xjc"(
            "destdir" to destDirXjc,
            "schema" to schemaDir.dir("PhoneBanking").file("PhoneBanking.xsd"),
            "binding" to binding,
            "package" to "de.freese.xjc.phonebanking",
            "encoding" to currentEncoding,
            "extension" to true
        ) {
            "arg"("value" to "-npa")
            "produces"("dir" to destDirXjc, "includes" to "de/freese/xjc/phonebanking/**/*.java")
        }

        "xjc"(
            "destdir" to destDirXjc,
            "schema" to schemaDir.dir("PublicationCatalogue").file("Catalogue.xsd"),
            "binding" to binding,
            "package" to "de.freese.xjc.catalogue",
            "encoding" to currentEncoding,
            "readonly" to true,
            "extension" to true
        ) {
            "arg"("value" to "-npa")
            "produces"("dir" to destDirXjc, "includes" to "de/freese/xjc/catalogue/**/*.java")
        }

        "xjc"(
            "destdir" to destDirXjc,
            "schema" to schemaDir.dir("SpaceWarGame").file("SpaceWarGame.xsd"),
            "binding" to binding,
            "package" to "de.freese.xjc.spacewargame",
            "encoding" to currentEncoding,
            "extension" to true
        ) {
            "arg"("value" to "-npa")
            "produces"("dir" to destDirXjc, "includes" to "de/freese/xjc/spacewargame/**/*.java")
        }
        // }
    }
}
tasks.named("compileJava").get().dependsOn("xslt", "genJaxb")
