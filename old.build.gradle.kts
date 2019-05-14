@file:Suppress("PropertyName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJar

plugins {
    java
    kotlin("jvm") version "1.3.31"
    idea
    id("fabric-loom") version "0.2.2-SNAPSHOT"
    id("com.github.ben-manes.versions") version "0.21.0"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    maven(url = "http://maven.fabricmc.net/") {
        name = "Fabric"
    }
    maven(url = "https://kotlin.bintray.com/kotlinx") {
        name = "Kotlin X"
    }
    maven(url = "https://dl.bintray.com/magneticflux/maven")
    jcenter()
}

minecraft {
}

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project

val archives_base_name: String by project
val mod_version: String by project
val maven_group: String by project

base.archivesBaseName = archives_base_name
project.version = mod_version
project.group = maven_group

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = minecraft_version)
    mappings(group = "net.fabricmc", name = "yarn", version = yarn_mappings)

    modCompile(group = "net.fabricmc", name = "fabric-loader", version = loader_version)
    modCompile(group = "net.fabricmc", name = "fabric", version = fabric_version)
    //include(group = "net.fabricmc", name = "fabric", version = fabric_version)

    modCompile(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.3.31+build.2")
    //include(group = "net.fabricmc", name = "fabric-language-kotlin", version = "1.3.31+build.2")

    modCompile(group = "io.github.prospector.modmenu", name = "ModMenu", version = "1.5.3-84")

    shadow(group = "com.skaggsm", name = "java-mumble-link", version = "0.2.1") {
        exclude(group = "net.java.dev.jna")
    }
}

tasks {
    processResources {
        inputs.property("version", mod_version)
        from(sourceSets.main.get().resources.srcDirs) {
            include("fabric.mod.json")
            expand("version" to mod_version)
        }
    }
}

val shadowJar by tasks.getting(ShadowJar::class) {
    configurations = mutableListOf(project.configurations.shadow.get())
    relocate("com.skaggsm.jmumblelink", "com.skaggsm.mumblelinkmod.shadowed.jmumblelink")
    relocate("com.skaggsm.sharedmemory", "com.skaggsm.mumblelinkmod.shadowed.sharedmemory")
}

val remapShadowJar = tasks.create("remapShadowJar", RemapJar::class.java as Class<Jar>) {
    dependsOn(shadowJar)
    (this as RemapJar).jar = shadowJar.archivePath
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            // add all the jars that should be included when publishing to maven
            artifact(remapShadowJar) {
            }
        }
    }
}
