import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
}

group = "top.wsure.guild"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("top.wsure.guild:wsure-guild-common:1.0-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}