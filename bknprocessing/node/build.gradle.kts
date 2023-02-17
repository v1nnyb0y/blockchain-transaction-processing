group = "com.bknprocessing.node"

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common"))

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}
