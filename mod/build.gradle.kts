import lol.bai.explosion.ExplosionExt
import net.fabricmc.loom.task.AbstractRunTask
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    alias(catalog.plugins.fabric.loom)

    alias(catalog.plugins.kotlin.jvm)
    alias(catalog.plugins.kotlin.plugin.serialization)

    alias(catalog.plugins.explosion)
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
        val client by this.named("client") { name("Fabric Client") }
        val server by this.named("server") { name("Fabric Server") }
        create("datagen") {
            inherit(client)
            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")
            vmArg("-Dfabric-api.datagen.modid=${id}")

            runDir("build/datagen")
        }
    }
}

kotlin { jvmToolchain(17) }

sourceSets { main { resources { srcDirs(file("src/main/generated")) } } }

val modNeedCopy by configurations.creating { isTransitive = false }

val modClientNeedCopy by
    configurations.creating {
        extendsFrom(modNeedCopy)
        isTransitive = false
    }

fun <T : Dependency> ExplosionExt.fabric(dependency: Provider<T>) =
    fabric(dependency.get().toString())

dependencies {
    minecraft(catalog.minecraft)
    mappings(variantOf(catalog.yarn) { classifier("v2") })

    modImplementation(catalog.fabric.loader)
    modImplementation(catalog.fabric.api)
    modImplementation(catalog.fabric.kotlin)

    val modClientImplementation by configurations
    val modClientRuntimeOnly by configurations
    modClientImplementation(catalog.modmenu)

    modImplementation(catalog.moonlight.fabric)

    modRuntimeOnly(catalog.fabric.waystones)
    modRuntimeOnly(catalog.owo)

    include(catalog.kasechange)
    implementation(catalog.kasechange)

    modRuntimeOnly(catalog.spell.engine)
    modRuntimeOnly(catalog.trinkets)
    modRuntimeOnly(catalog.spell.power)
    modRuntimeOnly(catalog.cloth.config.fabric)
    modRuntimeOnly(catalog.playeranimator.fabric)

    modRuntimeOnly(catalog.runes)

    modRuntimeOnly(explosion.fabric(catalog.amethyst.imbuement))
    modRuntimeOnly(explosion.fabric(catalog.amethyst.core))
    modRuntimeOnly(catalog.fzzy.core)
    modRuntimeOnly(explosion.fabric(catalog.patchouli))

    modRuntimeOnly(catalog.farmers.delight.fabric)

    modRuntimeOnly(explosion.fabric(catalog.illager.invasion))
    modRuntimeOnly(catalog.puzzleslib.fabric)
    modRuntimeOnly(catalog.forgeconfigapiport.fabric)

    modRuntimeOnly(catalog.pack.it.up)
}

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
