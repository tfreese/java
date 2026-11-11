plugins {
    id("java")
    id("maven-publish")
}

description = "Alles zum ausprobieren"

configurations.create("jaxb") {
    // extendsFrom(configurations.implementation.get())

    // Kann als Dependency-Graph aufgelöst werden (Default).
    isCanBeResolved = true

    // Wird nicht als veröffentlichbares/verbrauchbares Artefakt nach außen exponiert (Default).
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
    // testImplementation("org.apache.tomcat:tomcat-catalina") {
    //    // tomcat-juli ist in tomcat-catalina enthalten, aber auch viele andere Jars die nicht benötigt werden.
    //    exclude(group = "org.apache.tomcat")
    //}
    // testImplementation("org.apache.tomcat:tomcat-juli")
}

tasks.register("xslt") {
    group = "MyTasks"
    description = "Generates HTML from XSLT and XML files"

    val srcFolder = layout.projectDirectory.dir("src").dir("xslt")
    val destFolder = layout.buildDirectory.get().dir("classes").dir("java").dir("main").dir("xslt")

    val xslFile = srcFolder.file("article.xsl")
    val xmlFile = srcFolder.file("article.xml")
    val outputFile = destFolder.file("article.html")

    inputs.files(xslFile, xmlFile)
    outputs.file(outputFile)

    doLast {
        outputFile.asFile.parentFile.mkdirs()

        val transformer = javax.xml.transform.TransformerFactory.newInstance()
            .newTransformer(javax.xml.transform.stream.StreamSource(xslFile.asFile))

        transformer.transform(
            javax.xml.transform.stream.StreamSource(xmlFile.asFile),
            javax.xml.transform.stream.StreamResult(outputFile.asFile)
        )
    }
}

val schemaDir = layout.projectDirectory.dir("schemas")
val binding = layout.projectDirectory.file("schema.xjb")
val currentEncoding = tasks.named<JavaCompile>("compileJava").get().options.encoding

val xjcTasks = listOf(
    Triple("GolfCountryClub", "GolfCountryClub/GolfCountryClub.xsd", "de.freese.xjc.golfcountryclub"),
    Triple("PhoneBanking", "PhoneBanking/PhoneBanking.xsd", "de.freese.xjc.phonebanking"),
    Triple("Catalogue", "PublicationCatalogue/Catalogue.xsd", "de.freese.xjc.catalogue"),
    Triple("SpaceWarGame", "SpaceWarGame/SpaceWarGame.xsd", "de.freese.xjc.spacewargame")
).map { (taskSuffix, schemaPath, packageName) ->
    val schemaFile = schemaDir.file(schemaPath)
    val packageOutputDir = destDirXjc.dir(packageName.replace('.', '/'))

    tasks.register<JavaExec>("generate$taskSuffix") {
        group = "MyTasks"
        description = "Generates JAXB classes for $taskSuffix"

        classpath = configurations["jaxb"]
        mainClass.set("com.sun.tools.xjc.Driver")

        inputs.files(schemaDir, binding)
        outputs.dir(packageOutputDir)

        doFirst {
            logger.lifecycle("Lösche generierte JAXB-Dateien in ${packageOutputDir.asFile}")
            delete(packageOutputDir)
            packageOutputDir.asFile.mkdirs()
        }

        args(
            "-d", destDirXjc.asFile.absolutePath,
            "-b", binding.asFile.absolutePath,
            "-p", packageName,
            "-extension", "-npa"
        )

        currentEncoding?.let { args("-encoding", it) }

        if (taskSuffix == "Catalogue") {
            args("-readOnly")
        }

        args(schemaFile.asFile.absolutePath)
    }
}

tasks.register("genJaxb") {
    group = "MyTasks"
    description = "Generates Java classes from XSD files"

    dependsOn(xjcTasks)
}

// tasks.named("genJaxb") {
//     dependsOn(xjcTasks)
// }

tasks.named("compileJava").get().dependsOn("xslt", "genJaxb")
