import com.google.protobuf.gradle.*

// ktlint-disable no-wildcard-imports

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

    implementation("io.grpc:grpc-protobuf:1.53.0")
    implementation("io.grpc:grpc-stub:1.53.0")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")

    implementation("com.google.protobuf:protobuf-kotlin:3.22.0")
    implementation("net.devh:grpc-client-spring-boot-starter:2.14.0.RELEASE")

    protobuf(files("/proto"))

    runtimeOnly("io.netty:netty-resolver-dns-native-macos") {
        artifact {
            classifier = "osx-aarch_64"
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:21.0-rc-1"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.53.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                "$buildDir/generated/proto/main/grpc",
                "$buildDir/generated/proto/main/kotlin"
            )
        }
    }
}
