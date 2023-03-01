group = "com.bknprocessing.common"

plugins {
    kotlin("jvm")
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.projectreactor.kafka:reactor-kafka:1.3.12")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
