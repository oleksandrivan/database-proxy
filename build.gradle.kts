import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import java.io.ByteArrayOutputStream

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.19"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.3.6"
    id("io.micronaut.test-resources") version "4.3.6"
    id("io.micronaut.aot") version "4.3.6"
    id("com.avast.gradle.docker-compose") version "0.17.6"
}

version = "0.1"
group = "com.uoc"

val kotlinVersion=project.properties.get("kotlinVersion")
val registry = project.properties.get("dockerRegistry") as String

repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut:micronaut-http-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.kafka:micronaut-kafka")
    implementation("io.micronaut.kafka:micronaut-kafka-streams")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-prometheus")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("io.micronaut.redis:micronaut-redis-lettuce")
    implementation("org.jooq:jooq:3.19.7")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("io.micronaut.sql:micronaut-jooq")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("com.github.rholder:guava-retrying:2.0.0")
}


application {
    mainClass = "com.uoc.ApplicationKt"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
}


graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.uoc.*")
    }
    testResources {
        sharedServer = true
    }
    aot {
    // Please review carefully the optimizations enabled below
    // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

dockerCompose {
    useComposeFiles.set(listOf("docker/docker-compose.yml"))
    isRequiredBy(tasks.named("test"))
    stopContainers.set(false)
}

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}

tasks.named<io.micronaut.gradle.docker.MicronautDockerfile>("dockerfile") {
    baseImage("eclipse-temurin:21-jre-jammy")
    exposedPorts.set(listOf(8080))
}

tasks.named<DockerBuildImage>("dockerBuild") {
    val commitId = execCmd("git rev-parse --short HEAD")
    images.set(listOf("$registry/database-proxy:latest", "$registry/database-proxy:$commitId"))
}

fun execCmd(command: String): String {
    val stdOut = ByteArrayOutputStream()
    project.exec {
        commandLine = command.split(" ")
        standardOutput = stdOut
    }
    return stdOut.toString().trim()
}
