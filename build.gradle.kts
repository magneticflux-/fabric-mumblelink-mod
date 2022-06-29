@file:Suppress("PropertyName")

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    `maven-publish`
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("com.diffplug.spotless") version "6.7.2"
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("org.shipkit.shipkit-auto-version") version "1.+"
    id("org.shipkit.shipkit-changelog") version "1.+"
    id("org.shipkit.shipkit-github-release") version "1.+"
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"
    rejectVersionIf {
        candidate.version.contains("""-M\d+""".toRegex()) ||
            candidate.version.contains("RC")
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.fabricmc.net/")
        name = "Fabric"
    }
    maven {
        url = uri("https://maven.shedaniel.me/")
        name = "shedaniel"
    }
    maven {
        url = uri("https://maven.terraformersmc.com/releases/")
        name = "TerraformersMC"
    }
    maven {
        url = uri("https://maven.skaggsm.com/releases")
        name = "Personal"
    }
}

val curseforge_id: String by project
val archives_base_name: String by project
val maven_group: String by project
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project
val kotlin_version: String by project
val cloth_config_version: String by project
val fiber_2_cloth_version: String by project
val fiber_version: String by project
val modmenu_version: String by project

base {
    archivesName.set(archives_base_name)
    group = maven_group
}

loom {
}

dependencies {
    // to change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$yarn_mappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loader_version")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    modImplementation("net.fabricmc:fabric-language-kotlin:$kotlin_version")
    include("net.fabricmc:fabric-language-kotlin:$kotlin_version")

    modImplementation("com.terraformersmc:modmenu:$modmenu_version")

    modImplementation("me.shedaniel.cloth:cloth-config-fabric:$cloth_config_version")
    include("me.shedaniel.cloth:cloth-config-fabric:$cloth_config_version")

    modImplementation("me.shedaniel.cloth:fiber2cloth:$fiber_2_cloth_version") {
        isTransitive = false
    }
    include("me.shedaniel.cloth:fiber2cloth:$fiber_2_cloth_version") {
        isTransitive = false
    }

    modImplementation("me.zeroeightsix:fiber:$fiber_version")
    include("me.zeroeightsix:fiber:$fiber_version")

    include("com.skaggsm:java-mumble-link:0.2.6")
    implementation("com.skaggsm:java-mumble-link:0.2.6")
    include("com.skaggsm:jvm-shared-memory:0.2.9")
    implementation("com.skaggsm:jvm-shared-memory:0.2.9")
}

tasks.processResources {
    inputs.properties(
        "version" to project.version
    )

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version
        )
    }
}

tasks.withType<JavaCompile> {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    // Minecraft 1.17 (21w19a) upwards uses Java 16.
    options.release.set(17)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mod") {
            artifact(tasks.remapJar)
            artifact(tasks["sourcesJar"]) {
                builtBy(tasks.remapSourcesJar)
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/magneticflux-/fabric-mumblelink-mod")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.generateChangelog {
    repository = "magneticflux-/fabric-mumblelink-mod"
    previousRevision = project.ext["shipkit-auto-version.previous-tag"]?.toString()
    githubToken = System.getenv("GITHUB_TOKEN")
}

tasks.githubRelease {
    dependsOn(tasks.generateChangelog)
    repository = "magneticflux-/fabric-mumblelink-mod"
    changelog = tasks.generateChangelog.get().outputFile
    githubToken = System.getenv("GITHUB_TOKEN")
    newTagRevision = System.getenv("GITHUB_SHA")
}

// Configure CurseForge publishing
curseforge {
    // Stored in ~/.gradle/gradle.properties
    when {
        project.hasProperty("curseApiKey") -> apiKey = project.ext["curseApiKey"]
        System.getenv("CURSE_API_KEY") != null -> apiKey = System.getenv("CURSE_API_KEY")
        else -> println("No CurseForge API key found, \'curseforge\' tasks will not work")
    }

    project(
        closureOf<CurseProject> {
            id = curseforge_id
            releaseType = "release"
            addGameVersion(minecraft_version)
            addGameVersion("Fabric")
            changelog =
                "View the latest changelog here: https://github.com/magneticflux-/fabric-mumblelink-mod/releases"
            mainArtifact(
                tasks.remapJar.get(),
                closureOf<CurseArtifact> {
                    relations(
                        closureOf<CurseRelation> {
                            requiredDependency("fabric-api")
                            embeddedLibrary("fabric-language-kotlin")
                            embeddedLibrary("cloth-config")
                            embeddedLibrary("fiber2cloth")
                            optionalDependency("modmenu")
                        }
                    )
                }
            )
            addArtifact(tasks["sourcesJar"])
        }
    )
    options(
        closureOf<Options> {
            forgeGradleIntegration = false
        }
    )
}

spotless {
    kotlin {
        ktlint("0.45.2")
    }
    kotlinGradle {
        ktlint("0.45.2")
    }
}

afterEvaluate {
    // CurseGradle generates tasks in afterEvaluate for each project
    // There isn't really any other way to make it depend on a task unless it is an AbstractArchiveTask
    val curseforgeTask = tasks.getByName("curseforge$curseforge_id")
    tasks.publish {
        dependsOn(curseforgeTask)
    }
}
