import net.fabricmc.loom.task.AbstractRunTask
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    alias(catalog.plugins.fabric.loom)

    alias(catalog.plugins.kotlin.jvm)
    alias(catalog.plugins.kotlin.plugin.serialization)
}

val id: String by rootProject.properties
val name: String by rootProject.properties
val author: String by rootProject.properties
val description: String by rootProject.properties

archivesName = name

loom {
    splitEnvironmentSourceSets()

    accessWidenerPath = file("src/main/resources/$id.accesswidener")

    mixin {
        defaultRefmapName = "$id.refmap.json"

        add("main", "$id.refmap.json")
        add("client", "$id.client.refmap.json")
    }

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

val modNeedCopy by configurations.creating { isTransitive = false }

val modClientNeedCopy by
    configurations.creating {
        extendsFrom(modNeedCopy)
        isTransitive = false
    }

dependencies {
    minecraft(catalog.minecraft)
    mappings(variantOf(catalog.yarn) { classifier("v2") })

    modImplementation(catalog.fabric.loader)
    modImplementation(catalog.fabric.api)
    modImplementation(catalog.fabric.kotlin)

    val modClientImplementation by configurations
    modClientImplementation(catalog.modmenu)

    modImplementation(catalog.moonlight.fabric)

    modNeedCopy(catalog.fabric.waystones)
    modNeedCopy(catalog.owo)

    include(catalog.kasechange)
    implementation(catalog.kasechange)

    modNeedCopy(catalog.spell.engine)
    modNeedCopy(catalog.trinkets)
    modNeedCopy(catalog.spell.power)
    modClientNeedCopy(catalog.cloth.config.fabric)
    modNeedCopy(catalog.playeranimator.fabric)

    modNeedCopy(catalog.runes)

    //    modNeedCopy(catalog.amethyst.imbuement)
    //    modNeedCopy(catalog.amethyst.core)
    //    modNeedCopy(catalog.fzzy.core)
    //    modNeedCopy(catalog.patchouli)

    modNeedCopy(catalog.farmers.delight.fabric)

    modNeedCopy(catalog.illager.invasion)
    modNeedCopy(catalog.puzzleslib.fabric)
    modNeedCopy(catalog.forgeconfigapiport.fabric)

    modNeedCopy(catalog.pack.it.up)
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
        "source" to "https://github.com/SettingDust/$name",
        "minecraft" to "~1.20",
        "fabric_loader" to ">=0.12",
        "fabric_kotlin" to ">=1.10",
        "modmenu" to "*",
        "owo" to "*",
        "moonlight" to "*"
    )

tasks {
    withType<ProcessResources> {
        inputs.properties(metadata)
        filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(metadata) }
    }

    jar { from("LICENSE") }

    ideaSyncTask { enabled = true }

    val copyClientMods by
        creating(Copy::class) {
            destinationDir = file("${loom.runs.getByName("client").runDir}/mods")
            from(modClientNeedCopy)
        }

    classes { dependsOn(copyClientMods) }

    withType<AbstractRunTask> { enableAssertions = false }
}
