pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement.versionCatalogs.create("catalog") {
    // https://plugins.gradle.org/plugin/org.jetbrains.gradle.plugin.idea-ext
    plugin("idea-ext", "org.jetbrains.gradle.plugin.idea-ext").version("1.1.7")

    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
    val kotlin = "1.9.21"
    version("kotlin", kotlin)
    plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").version(kotlin)
    plugin("kotlin-plugin-serialization", "org.jetbrains.kotlin.plugin.serialization")
        .version(kotlin)

    // https://plugins.gradle.org/plugin/com.diffplug.spotless
    plugin("spotless", "com.diffplug.spotless").version("6.23.3")

    // https://github.com/jmongard/Git.SemVersioning.Gradle
    plugin("semver", "com.github.jmongard.git-semver-plugin").version("0.11.0")

    // https://fabricmc.net/develop/
    plugin("fabric-loom", "fabric-loom").version("1.4.+")

    val minecraft = "1.20.1"
    version("minecraft", minecraft)
    library("minecraft", "com.mojang", "minecraft").version(minecraft)

    library("yarn", "net.fabricmc", "yarn").version("$minecraft+build.10")

    library("fabric-loader", "net.fabricmc", "fabric-loader").version("0.15.3")

    val fabricApi = "0.90.7"
    library("fabric-api", "net.fabricmc.fabric-api", "fabric-api").version("$fabricApi+$minecraft")

    // https://modrinth.com/mod/fabric-language-kotlin/versions
    library(
            "fabric-kotlin",
            "net.fabricmc",
            "fabric-language-kotlin",
        )
        .version("1.10.16+kotlin.$kotlin")

    // https://modrinth.com/mod/modmenu/versions?g=1.20.1
    library("modmenu", "com.terraformersmc", "modmenu").version("7.2.2")

    /**
     * ***********
     * Quilt
     * ************
     */
    // https://github.com/QuiltMC/quilt-loom
    // Using 1.2 for https://github.com/QuiltMC/quilt-loom/issues/38
    plugin("quilt-loom", "org.quiltmc.loom").version("1.2.+")

    // https://github.com/QuiltMC/quilt-loader/tags
    library(
            "quilt-loader",
            "org.quiltmc",
            "quilt-loader",
        )
        .version("0.23.0-beta.1")

    // https://modrinth.com/mod/qsl/versions?g=1.20.1
    library(
            "quilt-fabric-api",
            "org.quiltmc.quilted-fabric-api",
            "quilted-fabric-api",
        )
        .version("7.4.0+0.90.0-$minecraft")

    /**
     * ***********
     * Forge
     * ************
     */
    // https://maven.neoforged.net/#/releases/net/neoforged/gradle/userdev
    plugin("forge-gradle", "net.minecraftforge.gradle").version("6.+")

    // https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html
    library("forge", "net.minecraftforge", "forge").version("$minecraft-47.2.18")

    // https://github.com/SpongePowered/MixinGradle
    plugin("mixin-gradle", "org.spongepowered.mixin").version("0.7-SNAPSHOT")

    // https://modrinth.com/mod/connector/versions
    library("connector", "dev.su5ed.sinytra", "Connector").version("1.0.0-beta.31+$minecraft")
    // https://modrinth.com/mod/forgified-fabric-api/versions
    library("forgified-fabric-api", "dev.su5ed.sinytra.fabric-api", "fabric-api")
        .version("$fabricApi+1.10.3+$minecraft")
    // https://modrinth.com/mod/connector-extras/versions
    library("connector-extras", "maven.modrinth", "connector-extras").version("1.8.0+$minecraft")

    // https://modrinth.com/mod/fwaystones/versions
    library("fabric-waystones", "maven.modrinth", "fwaystones").version("3.3.2+mc$minecraft")
    // https://github.com/wisp-forest/owo-lib/releases/
    library("owo", "io.wispforest", "owo-lib").version("0.11.2+1.20")
    // https://modrinth.com/mod/moonlight/versions
    library("moonlight", "maven.modrinth", "moonlight").version("fabric_1.20-2.8.71")
    // https://modrinth.com/mod/spell-engine/versions
    library("spell-engine", "maven.modrinth", "spell-engine").version("0.12.4+$minecraft")
    // https://modrinth.com/mod/trinkets/versions
    library("trinkets", "dev.emi", "trinkets").version("3.7.1")
    // https://modrinth.com/mod/spell-power/versions
    library("spell-power", "maven.modrinth", "spell-power").version("0.9.19+$minecraft-fabric")
    // https://modrinth.com/mod/cloth-config/versions
    library("cloth-config-fabric", "me.shedaniel.cloth", "cloth-config-fabric").version("11.1.118")
    // https://maven.kosmx.dev/dev/kosmx/player-anim/player-animation-lib-fabric/?C=M&O=D
    library("playeranimator-fabric", "dev.kosmx.player-anim", "player-animation-lib-fabric")
        .version("1.0.2-rc1+1.20")
    // https://modrinth.com/mod/runes/versions
    library("runes", "maven.modrinth", "runes").version("0.9.10+$minecraft-fabric")

    // https://modrinth.com/mod/amethyst-imbuement/versions
    library("amethyst-imbuement", "maven.modrinth", "amethyst-imbuement").version("$minecraft-18")
    // https://modrinth.com/mod/fzzy-core/versions
    library("fzzy-core", "maven.modrinth", "fzzy-core").version("0.3.4+$minecraft")
    // https://modrinth.com/mod/patchouli/versions
    library("patchouli", "vazkii.patchouli", "Patchouli").version("$minecraft-84-FABRIC")
    // https://modrinth.com/mod/amethyst-core/versions
    library("amethyst-core", "maven.modrinth", "amethyst-core").version("1.4.5+$minecraft")

    // https://modrinth.com/mod/farmers-delight-fabric/versions
    library("farmers-delight-fabric", "maven.modrinth", "farmers-delight-fabric").version("1.4.3")

    // https://modrinth.com/mod/illager-invasion/versions
    library("illager-invasion", "maven.modrinth", "illager-invasion")
        .version("v8.0.3-$minecraft-Fabric")
    // https://github.com/Fuzss/modresources/tree/main/maven/fuzs/puzzleslib/puzzleslib-fabric
    library("puzzleslib-fabric", "fuzs.puzzleslib", "puzzleslib-fabric").version("8.1.11")
    // https://github.com/Fuzss/modresources/tree/main/maven/fuzs/forgeconfigapiport/forgeconfigapiport-fabric
    library("forgeconfigapiport-fabric", "fuzs.forgeconfigapiport", "forgeconfigapiport-fabric")
        .version("8.0.0")

    // https://modrinth.com/mod/pack-it-up/versions
    library("pack-it-up", "maven.modrinth", "pack-it-up").version("0.3.1+$minecraft")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
    // https://github.com/DanySK/gradle-pre-commit-git-hooks
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.16"
}

gitHooks {
    preCommit {
        from {
            //             git diff --cached --name-only --diff-filter=ACMR | while read -r a; do
            // echo ${'$'}(readlink -f ${"$"}a); ./gradlew spotlessApply -q
            // -PspotlessIdeHook="${'$'}(readlink -f ${"$"}a)" </dev/null; done
            """
            export JAVA_HOME="${System.getProperty("java.home")}"
            ./gradlew spotlessApply spotlessCheck
            """
                .trimIndent()
        }
    }
    commitMsg { conventionalCommits { defaultTypes() } }
    hook("post-commit") {
        from {
            """
            files="${'$'}(git show --pretty= --name-only | tr '\n' ' ')"
            git add ${'$'}files
            git -c core.hooksPath= commit --amend -C HEAD
            """
                .trimIndent()
        }
    }
    createHooks(true)
}

val name: String by settings

rootProject.name = name

include("mod")

include("quilt")

include("forge")
