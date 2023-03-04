import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
// import io.gitlab.arturbosch.detekt.Detekt
// import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("org.springframework.boot") version "2.7.5" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    kotlin("jvm") version "1.7.22" apply false
    kotlin("plugin.spring") version "1.7.22" apply false
    id("io.gitlab.arturbosch.detekt") version "1.17.1" apply false
    id("com.asarkar.gradle.build-time-tracker") version "4.0.0" apply false

    id("com.google.protobuf") version "0.9.2" apply false
}

allprojects {
    group = "com.bknprocessing"
    version = "0.0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")

    // apply(from = "$rootDir/gradle/ktlint.gradle.kts")
    // apply(plugin = "io.gitlab.arturbosch.detekt")

    the<DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.0.1") {
                bomProperty("kotlin.version", "1.7.22")
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    tasks.getByName<Jar>("jar") {
        enabled = true
    }

    /*
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
            )
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
    */

    /*
    tasks.withType<Detekt> {
        exclude("resources/")
        exclude("build/")
        config.setFrom(files("$rootDir/gradle/detekt-config.yml"))
    }
     */
}
