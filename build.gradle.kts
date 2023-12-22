import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    idea
    alias(catalog.plugins.idea.ext)

    alias(catalog.plugins.spotless)

    alias(catalog.plugins.semver)
}

group = "settingdust.betteruitextures"

version = semver.semVersion.toString()

allprojects { repositories { mavenCentral() } }

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        maven("https://maven.wispforest.io") {
            content { includeGroupAndSubgroups("io.wispforest") }
        }
        maven("https://maven.lukebemish.dev/releases/") {
            content { includeGroupAndSubgroups("dev.lukebemish") }
        }
        maven("https://api.modrinth.com/maven") {
            name = "Modrinth"
            content { includeGroup("maven.modrinth") }
        }
        maven("https://maven.terraformersmc.com/releases") {
            content {
                includeGroup("com.terraformersmc")
                includeGroup("dev.emi")
            }
        }
        maven("https://maven.shedaniel.me/") { content { includeGroup("me.shedaniel.cloth") } }
        maven("https://maven.kosmx.dev/") { content { includeGroup("dev.kosmx.player-anim") } }
        maven("https://maven.blamejared.com") { content { includeGroup("vazkii.patchouli") } }
        maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") {
            content { includeGroupAndSubgroups("fuzs") }
        }
        maven("https://maven.ladysnake.org/releases") {
            content { includeGroup("dev.onyxstudios.cardinal-components-api") }
        }
        mavenCentral()
    }
}

spotless {
    java {
        target("*/src/**/*.java")
        palantirJavaFormat("2.29.0")
    }

    kotlin {
        target("*/src/**/*.kt", "*/*.gradle.kts", "*.gradle.kts")
        ktfmt("0.46").kotlinlangStyle()
    }
}

idea.project.settings.taskTriggers { afterSync(":forge:genIntellijRuns") }
