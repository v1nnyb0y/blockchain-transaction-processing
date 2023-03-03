group = "com.bknprocessing.common"

plugins {
    kotlin("jvm")
    id("com.google.protobuf")
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.projectreactor.kafka:reactor-kafka:1.3.12")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.grpc:grpc-protobuf:.")
    implementation("io.grpc:grpc-stub:.")
    implementation("io.grpc:grpc-kotlin-stub:.")

    //  implementation("com.google.protobuf:protobuf-java")
    implementation("net.devh:grpc-client-spring-boot-starter:.")

    runtimeOnly("io.netty:netty-resolver-dns-native-macos") {
        artifact {
            classifier = "osx-aarch_64"
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${protobufVersion}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.1.0:jdk7@jar"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            task.builtins {
                java {}
                kotlin {}
            }
            it.plugins {
                id("kotlin")
                id("grpc")
                id("grpckt")
            }
        }
    }
}
