plugins {
    alias(catalog.plugins.fabric.loom)

    alias(catalog.plugins.kotlin.jvm)
    alias(catalog.plugins.kotlin.plugin.serialization)
}

val id: String by rootProject.properties
val name: String by rootProject.properties
val author: String by rootProject.properties
val description: String by rootProject.properties

loom {
    splitEnvironmentSourceSets()

    mods {
        register(id) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runs {
        configureEach { ideConfigGenerated(true) }
        named("client") { name("Fabric Client") }
        named("server") { name("Fabric Server") }
    }
}

dependencies {
    minecraft(catalog.minecraft)
    mappings(variantOf(catalog.yarn) { classifier("v2") })

    modImplementation(catalog.fabric.loader)
    modImplementation(catalog.fabric.api)
    modImplementation(catalog.fabric.kotlin)

    val modClientImplementation by configurations
    modClientImplementation(catalog.modmenu)
}

kotlin { jvmToolchain(17) }

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

val metadata =
    mapOf(
        "group" to group,
        "author" to author,
        "id" to id,
        "name" to name,
        "version" to version,
        "description" to description,
        "source" to "https://github.com/SettingDust/FabricKotlinTemplate",
        "minecraft" to "~1.20",
        "fabric_loader" to ">=0.12",
        "fabric_kotlin" to ">=1.10",
        "modmenu" to "*",
    )

tasks {
    processResources {
        inputs.properties(metadata)
        filesMatching("fabric.mod.json") { expand(metadata) }
    }

    jar { from("LICENSE") }

    ideaSyncTask { enabled = true }
}
