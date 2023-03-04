group = "com.bknprocessing.node"

plugins {
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.protobuf")
}

dependencies {
    implementation(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus:1.10.3")
    // TODO check and remove two row bellow
    implementation("net.devh:grpc-server-spring-boot-starter:2.14.0.RELEASE")
//    implementation("org.springframework.metrics:spring-metrics:0.5.1.RELEASE")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}
