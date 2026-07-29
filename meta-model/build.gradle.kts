plugins {
    id("java-library")
}

description = "Code-Generierung durch MetaDaten."

dependencies {
    api("jakarta.persistence:jakarta.persistence-api")
    api("jakarta.validation:jakarta.validation-api")
    api("org.hibernate.orm:hibernate-core")

    implementation("org.slf4j:slf4j-api")

    testImplementation("com.oracle.database.jdbc:ojdbc11")
    testImplementation("org.hibernate.validator:hibernate-validator")
    testImplementation("org.hsqldb:hsqldb")
    testImplementation("org.slf4j:slf4j-simple")
    testImplementation("org.mariadb.jdbc:mariadb-java-client")
    testImplementation("org.xerial:sqlite-jdbc")
}

tasks.register<Delete>("deleteAppFolder") {
    description = "Delete app-Folder."
    group = "MyTasks"

    logger.lifecycle("delete app-Folder: ${projectDir}/src/test/generated")

    // Delete directory.
    delete("src/test/generated")

    // Delete files recursively.
    // delete(fileTree("src/test/generated") {
    //     include("**/*.*")
    // })
}
tasks.named("clean").get().finalizedBy("deleteAppFolder")
