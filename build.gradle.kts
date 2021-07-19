import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    // static code analysis
    id("io.gitlab.arturbosch.detekt") version "1.14.1"
    id("com.diffplug.spotless") version "5.6.1"

    // spring
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"

    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"

    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"

    // deployment
    id("net.mayope.deployplugin") version ("0.0.51")

    // docs
    id("io.github.danakuban.docs-gradle-plugin") version ("1.0.2")
}

group = "io.github.clemens-code.bitbucketwatcher"
version = "0.0.1"

deploy {
    serviceName = "bitbucketwatcher"
    default {
        dockerBuild {
            version = project.version.toString()
        }
        dockerPush {
            registryRoot = property("registryRoot").toString() ?: ""
            loginMethod = net.mayope.deployplugin.tasks.DockerLoginMethod.DOCKERHUB
            loginUsername = property("dockerUser").toString() ?: ""
            loginPassword = property("dockerPswd").toString() ?: ""
        }
        deploy {
            targetNamespaces = listOf(property("deployNameSpace").toString())
        }
        helmPush {
            repositoryUrl = property("helmRepo").toString() ?: ""
            repositoryUsername = property("helmUser").toString() ?: ""
            repositoryPassword = property("helmPswd").toString() ?: ""
        }
    }
}

repositories {
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.fabric8:kubernetes-client:4.11.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    // Feign
    implementation("io.github.openfeign:feign-core:11.1")
    implementation("io.github.openfeign:feign-jackson:11.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.9.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.0")
}

val ktLintVersion = "0.39.0"
allprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            ktlint(ktLintVersion)
        }
        kotlinGradle {
            target("*.gradle.kts", "**/*.gradle.kts")
            ktlint(ktLintVersion)
        }
    }
}

ktlint {
    version.set(ktLintVersion)
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.HTML)
    }
    filter {
        include("**/kotlin/**")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    group = "test"
    description = "Executes Tests"

    maxHeapSize = "1024m"

    filter {
        isFailOnNoMatchingTests = false
        includeTestsMatching("*Test")
        includeTestsMatching("*IT")
        includeTestsMatching("*ITFull")
    }
}
