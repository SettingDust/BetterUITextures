import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    idea
    alias(catalog.plugins.idea.ext)

    alias(catalog.plugins.spotless)

    alias(catalog.plugins.semver)
}

group = "settingdust.template"

version = semver.semVersion.toString()

allprojects { repositories { mavenCentral() } }

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        maven("https://maven.terraformersmc.com/releases") {
            content { includeGroup("com.terraformersmc") }
        }
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
