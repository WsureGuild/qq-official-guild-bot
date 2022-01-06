import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    application
    kotlin("plugin.serialization") version "1.6.0"
}

group = "top.wsure.guild"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("top.wsure.guild:wsure-guild-common:1.0-SNAPSHOT")
    implementation("ch.qos.logback:logback-core:1.2.10")
    implementation("ch.qos.logback:logback-classic:1.2.10")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}