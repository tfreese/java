plugins {
    id("java")
    id("com.google.protobuf")
}

description = "Google Protobuf Demo"

//sourceSets {
//    main {
//        proto {
//            srcDir "src/main/proto"
//        }
//    }
//}

protobuf {
    protoc {
//        path = "/usr/bin/protoc"
        artifact = "com.google.protobuf:protoc:" + property("version_protobufProtoc")
    }
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:" + property("version_protobufProtoc"))

    testImplementation("com.google.protobuf:protobuf-java-util:" + property("version_protobufProtoc"))
}
