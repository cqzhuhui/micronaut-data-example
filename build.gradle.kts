plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("org.jetbrains.kotlin.plugin.jpa")
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("io.micronaut.application") version "3.6.7"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("com.google.cloud.tools.jib") version "3.3.0"
}

version = "0.1-SNAPSHOT"
group = project.properties["group"]!!

val kotlinVersion = project.properties["kotlinVersion"]

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven")
    }
}

dependencies {

    // common
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.micronaut:micronaut-validation")

    // client
    kapt("io.micronaut.openapi:micronaut-openapi")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.swagger.core.v3:swagger-annotations")

    // jackson - java 8 localtime/date support
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.4")

    // db
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.postgresql:postgresql")

    // testing
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(0, "seconds")
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

application {
    mainClass.set("example.micronaut.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("example.micronaut.*")
    }
}

allOpen {
    // Mark any classes with the following transactions as `open` automatically.
    annotations(
        "io.micronaut.aop.Around",
        "javax.transaction.Transactional",
        "io.micronaut.http.annotation.Controller",
        "jakarta.inject.Singleton"
    )
}

// Make sure that ktlint and tests have enough RAM to complete
tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
    workerMaxHeapSize.set("2048m")
}

tasks.test {
    maxHeapSize = "2G"
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED") // Allow mocking static functions
}
