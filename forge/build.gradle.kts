import net.minecraftforge.gradle.common.util.MavenArtifactDownloader

plugins {
    alias(catalog.plugins.forge.gradle)
    alias(catalog.plugins.mixin.gradle)
}

val id: String by rootProject.properties

minecraft {
    mappings("official", catalog.versions.minecraft.get())

    runs {
        configureEach {
            workingDirectory(project.file("run"))

            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            //        property 'forge.logging.markers', 'REGISTRIES'

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read:
            // https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            property("forge.logging.console.level", "debug")
        }

        val CLEAN_ARTIFACT = "net.minecraft:joined:%s:srg"
        afterEvaluate {
            val mcpVersion = project.extra["MCP_VERSION"]
            val cleanArtifactJar =
                MavenArtifactDownloader.generate(
                    project,
                    CLEAN_ARTIFACT.format(mcpVersion),
                    true,
                ) ?: throw RuntimeException("Cannot find clean minecraft artifact")
            configureEach { property("connector.clean.path", cleanArtifactJar) }
        }

        create("client") { ideaModule = "FabricKotlinTemplate.forge.main" }

        create("server") {
            ideaModule = "FabricKotlinTemplate.forge.main"
            args("--nogui")
        }
    }
}

repositories {
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        forRepositories(
            fg.repository,
        ) // Only add this if you're using ForgeGradle, otherwise remove this line
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://maven.su5ed.dev/releases") { name = "Sinytra" }
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
}

dependencies {
    minecraft(catalog.forge)

    runtimeOnly(fg.deobf(catalog.connector.asProvider().get()))
    runtimeOnly(fg.deobf(catalog.connector.extras.get()))
    runtimeOnly(fg.deobf(catalog.forgified.fabric.api.get()))
    runtimeOnly(catalog.fabric.kotlin) { isTransitive = false }
    runtimeOnly(project(":mod")) { isTransitive = false }
}
