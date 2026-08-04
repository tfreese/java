import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java")
    id("net.ltgt.errorprone")
}

dependencies {
    implementation("org.jspecify:jspecify")

    errorprone("com.uber.nullaway:nullaway:" + property("version_nullaway"))
    errorprone("com.google.errorprone:error_prone_core:" + property("version_errorProneCore"))
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        // Warnung: [MissingSummary] A summary line is required on public/protected Javadocs.
        // Or with SuppressWarnings.
        // disable("MissingSummary")

        // check("NullAway", net.ltgt.gradle.errorprone.CheckSeverity.ERROR)
        // check("NullAway", CheckSeverity.WARN)
        warn("NullAway")
        // error("NullAway")

        // Scan all Packages.
        // option("NullAway:AnnotatedPackages", "de.freese.jspecify")

        // Scan only Packages with package.info.java.
        option("NullAway:OnlyNullMarked", "true")

        // Uncomment below if you are using Java 22+ compiled and you want to check generics nullness.
        option("NullAway:JSpecifyMode", "true")

        // disableAllChecks = true // Other error prone checks are disabled.
        // option("NullAway:CustomContractAnnotations", "org.springframework.lang.Contract")

        // allErrorsAsWarnings.set(true)
        // disableWarningsInGeneratedCode.set(false)
    }

    // options.errorprone.allErrorsAsWarnings.set(true)
    // options.errorprone.disableWarningsInGeneratedCode.set(false)

    // Disable NullAway on test code.
    // if (name.toLowerCase().contains("test")) {
    //     options.errorprone {
    //         disable("NullAway")
    //     }
    // }
}

//tasks.named("compileTestJava").configure {
//    options.errorprone {
//        // Disable All.
//        enabled = false
//
//        disable("NullAway")
//
//        // Disable Test-Classes.
//        option("NullAway:ExcludedClassAnnotations", "org.junit.jupiter.api.Test,org.junit.Test")
//
//        // Disable Test-Packages.
//        option("NullAway:UnannotatedSubPackages", "de.freese.jspecify,de.freese.jspecify.test")
//    }
//}
